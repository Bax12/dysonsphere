package de.bax.dysonsphere.capabilities.dysonSphere;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;

import de.bax.dysonsphere.DSConfig;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.dsEnergyReciever.IDSEnergyReceiver;
import de.bax.dysonsphere.capabilities.dsPart.IDSPart;
import de.bax.dysonsphere.network.DSLightSyncPackage;
import de.bax.dysonsphere.network.ModPacketHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public class DysonSphereContainer implements ICapabilitySerializable<CompoundTag> {

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

        Map<Item, Integer> parts = new HashMap<>();
        protected double energy = 0.0d; //prone to rounding errors, only visible with large changes in the part lists without restart.
        protected float completion = 0.0f; //in percent 0.0 - 100.0
        Set<LazyOptional<IDSEnergyReceiver>> receivers = new HashSet<>();


        public CompoundTag save(){
            CompoundTag tag = new CompoundTag();
            CompoundTag invTag = new CompoundTag();
            //TODO
            parts.forEach((item, count) -> {
                ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(item);
                if(itemKey != null){
                    invTag.putInt(itemKey.toString(), count);
                }
            });
            if(invTag.size() > 0){
                tag.put("inv", invTag);
            }

            return tag;
        }

        public void load(CompoundTag tag){
            CompoundTag inv = tag.getCompound("inv");
            if(inv != null){
                for(String itemKey : inv.getAllKeys()){
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemKey));
                    int count = inv.getInt(itemKey);
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
        }

        @Override
        public boolean addDysonSpherePart(ItemStack stack, boolean simulate) {
            if(stack.getCapability(DSCapabilities.DS_PART).isPresent()){
                if(completion >= 100f) return false; //Deny new parts when already full.
                if(!simulate){
                    int count = parts.getOrDefault(stack.getItem(), 0);
                    parts.put(stack.getItem(), count + stack.getCount());
                    stack.getCapability(DSCapabilities.DS_PART).ifPresent((part) -> {
                        energy += (part.getEnergyProvided() * stack.getCount());
                        completion += (part.getCompletionProgress() * stack.getCount());
                    });
                    receivers.forEach((lazyReceiver) -> {
                        lazyReceiver.ifPresent((receiver) -> {
                            receiver.handleDysonSphereChange(this);
                        });
                    });
                    //update client light level
                    ModPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new DSLightSyncPackage(completion));
                }
                
                return true;
            }
            return false;
        }

        @Override
        public boolean removeDysonSpherePart(ItemStack stack, boolean simulate) {
            if(stack.getCapability(DSCapabilities.DS_PART).isPresent()){
                if(!parts.containsKey(stack.getItem()) || parts.get(stack.getItem()) < stack.getCount()) return false;
                if(!simulate){
                    int count = parts.get(stack.getItem()) - stack.getCount();
                    if(count > 0){
                        parts.put(stack.getItem(), count);
                    } else {
                        parts.remove(stack.getItem());
                    }
                    stack.getCapability(DSCapabilities.DS_PART).ifPresent((part) -> {
                        energy -= (part.getEnergyProvided() * stack.getCount());
                        completion -= (part.getCompletionProgress() * stack.getCount());
                    });
                    receivers.forEach((lazyReceiver) -> {
                        lazyReceiver.ifPresent((receiver) -> {
                            receiver.handleDysonSphereChange(this);
                        });
                    });
                }
                return true;
            }
            return false;
        }

        @Override
        public ImmutableMap<Item, Integer> getDysonSphereParts() {
            return ImmutableMap.copyOf(parts);
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
            return (float) ((getEnergyRequested() / energy) * 100f);
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
        public int getDysonSpherePartCount(Item part) {
            return parts.getOrDefault(part, 0);
        }

        public int getDysonSpherePartCount(Predicate<ItemStack> item){
            return parts.keySet().stream().filter((part) -> {
                return item.test(part.getDefaultInstance());
            }).mapToInt((part) -> {
                return parts.getOrDefault(part, 0);
            }).sum();
        }

        @Override
        public boolean resetDysonSphereParts() {
            parts.clear();
            return true;
        }
        
    }

    

}
