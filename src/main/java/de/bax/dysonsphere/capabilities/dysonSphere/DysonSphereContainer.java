package de.bax.dysonsphere.capabilities.dysonSphere;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.bax.dysonsphere.DSConfig;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.dsEnergyReciever.IDSEnergyReceiver;
import de.bax.dysonsphere.capabilities.dsPart.IDSPart;
import de.bax.dysonsphere.constructs.Construct;
import de.bax.dysonsphere.constructs.ModConstructs;
import de.bax.dysonsphere.network.DSLightSyncPackage;
import de.bax.dysonsphere.network.ModPacketHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public class DysonSphereContainer implements ICapabilitySerializable<CompoundTag> {

    public static final float DS_COMPLETED = 100f; //does this really count as magic number?

    boolean allowOverworldAccess;

    DysonSphere dysonSphere = new DysonSphere();
    LazyOptional<DysonSphere> lazyDysonSphere = LazyOptional.of(() -> dysonSphere);

    public DysonSphereContainer(){
        allowOverworldAccess = !(DSConfig.DYSON_SPHERE_DIM_BLACKLIST_VALUE.contains(Level.OVERWORLD.location().toString()) ^ DSConfig.DYSON_SPHERE_IS_WHITELIST_VALUE);
        /*
        inList    whiteList         allowed
        0       0               1
        0       1               0
        1       0               0
        1       1               1
        */
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        //allow sided calls to ignore the overworld blacklist.
        //Basically: all blocks call with null, only the proxycontainer for the other dimension(s) calls sided.
        //if something has a reason to ignore the potential overworld dimension blacklist it should call this method with a side as well.
        if(allowOverworldAccess || side != null){
            if(cap.equals(DSCapabilities.DYSON_SPHERE)){
                return lazyDysonSphere.cast();
            }
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return dysonSphere.save();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        dysonSphere.load(nbt);
    }
    
    public class DysonSphere implements IDysonSphereContainer {

        Map<Item, Long> parts = new HashMap<>();
        protected double energy = 0.0d; //prone to rounding errors, only visible with large changes in the part lists without restart.
        protected float completion = 0.0f; //in percent 0.0 - 100.0
        Set<LazyOptional<IDSEnergyReceiver>> receivers = new HashSet<>();
        Set<Construct> constructsActive = new HashSet<>();
        Set<Construct> constructsInactive = new HashSet<>();

        public CompoundTag save(){
            CompoundTag tag = new CompoundTag();
            CompoundTag invTag = new CompoundTag();
            parts.forEach((item, count) -> {
                ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(item);
                if(itemKey != null){
                    invTag.putLong(itemKey.toString(), count);
                }
            });
            if(invTag.size() > 0){
                tag.put("inv", invTag);
            }
            saveConstructs(tag, "constructsActive", constructsActive);
            saveConstructs(tag, "constructsInactive", constructsInactive);

            return tag;
        }

        protected static void saveConstructs(CompoundTag tag, String key, Set<Construct> constructs){
            StringBuilder conKeys = new StringBuilder();
            constructs.forEach((construct) -> {
                ResourceLocation conKey = ModConstructs.registry().getKey(construct);
                if(conKey != null){
                    conKeys.append(conKey.toString());
                    conKeys.append(',');
                }
            });
            if(!conKeys.isEmpty()){
                conKeys.deleteCharAt(conKeys.length()-1);//remove last separator
                tag.putString(key, conKeys.toString());
            }
        }

        public void load(CompoundTag tag){
            CompoundTag inv = tag.getCompound("inv");
            if(inv != null){
                for(String itemKey : inv.getAllKeys()){
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemKey));
                    long count = inv.getLong(itemKey);
                    parts.put(item, count);
                }
            }
            parts.forEach((item, count) -> {
                LazyOptional<IDSPart> cap = new ItemStack(item).getCapability(DSCapabilities.DS_PART);
                cap.ifPresent((part) -> {
                    energy += (part.getEnergyProvided() * count);
                    completion += (part.getCompletionProgress() * count);
                });
            });
            loadConstructs(tag, "constructsActive", constructsActive);
            loadConstructs(tag, "constructsInactive", constructsInactive);
        }

        protected static void loadConstructs(CompoundTag tag, String key, Set<Construct> constructs){
            if(tag.contains(key)){
                for(String conKey : tag.getString(key).split(",")){
                    constructs.add(ModConstructs.registry().getValue(new ResourceLocation(conKey)));
                }
            }
        }

        @Override
        public boolean addDysonSpherePart(ItemStack stack, boolean simulate) {
            if(stack.getCapability(DSCapabilities.DS_PART).isPresent()){
                if(completion >= DS_COMPLETED) return false; //Deny new parts when already full.
                if(!simulate){
                    long count = parts.getOrDefault(stack.getItem(), 0l);
                    parts.put(stack.getItem(), count + stack.getCount());
                    stack.getCapability(DSCapabilities.DS_PART).ifPresent((part) -> {
                        energy += (part.getEnergyProvided() * stack.getCount());
                        completion += (part.getCompletionProgress() * stack.getCount());
                    });
                    updateDSPartListeners();
                }
                
                return true;
            }
            return false;
        }

        
        @Override
        public int addDysonSpherePartBulk(ItemStack stack, int amount){
            long count = parts.getOrDefault(stack.getItem(), 0l);

            LazyOptional<IDSPart> cap = stack.getCapability(DSCapabilities.DS_PART);
            if(!cap.isPresent() || amount <= 0) return 0;

            Tuple<Integer, Float> stats = cap.map((part) -> {
                return new Tuple<Integer,Float>(part.getEnergyProvided() * amount, part.getCompletionProgress() * amount);
            }).orElse(new Tuple<>(0, 0f));
            
            if(completion + stats.getB() <= DS_COMPLETED){
                completion += stats.getB();
                energy += stats.getA();
                parts.put(stack.getItem(), count + amount);

                updateDSPartListeners();
                return amount;
            } else {
                float singleCompletion = stats.getB() / amount;
                int partsToAdd = (int) ((DS_COMPLETED - completion) / singleCompletion);
                return addDysonSpherePartBulk(stack, partsToAdd); //should only ever recurse once.
            }
        }

        @Override
        public boolean removeDysonSpherePart(ItemStack stack, boolean simulate) {
            if(stack.getCapability(DSCapabilities.DS_PART).isPresent()){
                if(!parts.containsKey(stack.getItem()) || parts.get(stack.getItem()) < stack.getCount()) return false;
                if(!simulate){
                    long count = parts.get(stack.getItem()) - stack.getCount();
                    if(count > 0){
                        parts.put(stack.getItem(), count);
                    } else {
                        parts.remove(stack.getItem());
                    }
                    stack.getCapability(DSCapabilities.DS_PART).ifPresent((part) -> {
                        energy -= (part.getEnergyProvided() * stack.getCount());
                        completion -= (part.getCompletionProgress() * stack.getCount());
                    });
                    updateDSPartListeners();
                }
                return true;
            }
            return false;
        }

        @Override
        public int removeDysonSpherePartBulk(ItemStack stack, int amount) {
            if(amount <= 0 || !parts.containsKey(stack.getItem())) return 0;
            long present = parts.get(stack.getItem());

            LazyOptional<IDSPart> cap = stack.getCapability(DSCapabilities.DS_PART); //has to be present, as it's in the partlist.
            Tuple<Integer, Float> singleStats = cap.map((part) -> {
                return new Tuple<Integer,Float>(part.getEnergyProvided(), part.getCompletionProgress());
            }).orElse(new Tuple<>(0, 0f));

            if(present - amount >= 0){
                completion -= singleStats.getB() * amount;
                energy -= singleStats.getA() * amount;
                parts.put(stack.getItem(), present - amount);
                updateDSPartListeners();
                return amount;
            } else {
                completion -= singleStats.getB() * present;
                energy -= singleStats.getA() * present;
                parts.remove(stack.getItem());
                updateDSPartListeners();
                return (int) present;
            }
        }

        protected void updateDSPartListeners(){
            receivers.forEach((lazyReceiver) -> {
                lazyReceiver.ifPresent((receiver) -> {
                    receiver.handleDysonSphereChange(this);
                });
            });
            ModPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new DSLightSyncPackage(completion / DS_COMPLETED));//completion is 0 - 100, light is 0-1
        }

        @Override
        public Map<Item, Long> getDysonSphereParts() {
            return ImmutableMap.copyOf(parts);
        }

        @Override
        public boolean addConstruct(Construct construct){
            if(construct == null) return false;
            if(!constructsInactive.contains(construct)){
                return constructsActive.add(construct);
            }
            return false;
        }

        @Override
        public boolean removeConstruct(Construct construct){
            if(!constructsInactive.remove(construct)){
                return constructsActive.remove(construct);
            }
            return true;
        }

        @Override
        public boolean enableConstruct(Construct construct){
            if(constructsInactive.remove(construct)){
                return constructsActive.add(construct);
            }
            return false;
        }

        @Override
        public boolean disableConstruct(Construct construct){
            if(constructsActive.remove(construct)){
                return constructsInactive.add(construct);
            }
            return false;
        }

        @Override
        public List<Construct> getAllConstructs(){
            return ImmutableList.<Construct>builder().addAll(constructsActive).addAll(constructsInactive).build();
        }

        @Override
        public List<Construct> getEnabledConstructs(){
            return ImmutableList.copyOf(constructsActive);
        }

        @Override
        public List<Construct> getDisabledConstructs(){
            return ImmutableList.copyOf(constructsInactive);
        }

        @Override
        public double getDysonSphereEnergy() {
            return energy;
        }

        @Override
        public float getCompletionPercentage() {
            return Math.max(0f, Math.min(completion, 100.0f)); //Prevent printing something like 100,001% or -0,00001%
        }

        @Override
        public float getUtilization() {
            if(energy <= 0){
                return Float.NaN;
            }
            return (float) ((getEnergyRequested() / energy) * DS_COMPLETED);
        }

        
        @Override
        public double getEnergyProvided(){
            double energyProvided = getEnergyRequested();
            if(energyProvided >= energy){
                energyProvided = energy;
            }
            // energyProvided = 0.0d;
            // receivers.forEach((reciever) -> {
            //     reciever.ifPresent((rec) -> {
            //         if(rec.canReceive()){
            //             energyProvided += rec.getCurrentReceive(this);
            //         }
            //     });
            // });
            return energyProvided;
        }

        protected double energyRequested = 0.0d;
        @Override
        public double getEnergyRequested() {
            energyRequested = 0.0d;
            receivers.forEach((receiver) -> {
                receiver.ifPresent((rec) -> {
                    if(rec.canReceive()){
                        energyRequested += rec.getMaxReceive();
                    }
                });
            });
            return energyRequested;
        }

        @Override
        public void registerEnergyReceiver(LazyOptional<IDSEnergyReceiver> energyReceiver) {
            if(energyReceiver.isPresent()){
                receivers.add(energyReceiver);
            }
            energyReceiver.addListener((receiver) -> {
                receivers.remove(receiver);
            });
        }

        @Override
        public void removeEnergyReceiver(LazyOptional<IDSEnergyReceiver> energyReceiver) {
            receivers.remove(energyReceiver);
        }

        @Override
        public long getDysonSpherePartCount(Item part) {
            return parts.getOrDefault(part, 0l);
        }

        public long getDysonSpherePartCount(Predicate<ItemStack> item){
            return parts.keySet().stream().filter((part) -> {
                return item.test(part.getDefaultInstance());
            }).mapToLong((part) -> {
                return parts.getOrDefault(part, 0l);
            }).sum();
        }

        @Override
        public boolean resetDysonSphereParts() {
            parts.clear();
            completion = 0f;
            //update client light level
            updateDSPartListeners();
            return true;
        }
        
    }

    

}
