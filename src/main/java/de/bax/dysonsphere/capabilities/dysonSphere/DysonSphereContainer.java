package de.bax.dysonsphere.capabilities.dysonSphere;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.dsEnergyReciever.IDSEnergyReceiver;
import de.bax.dysonsphere.capabilities.dsPart.IDSPart;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;

public class DysonSphereContainer implements ICapabilitySerializable<CompoundTag> {

    DysonSphere dysonSphere = new DysonSphere();
    LazyOptional<DysonSphere> lazyDysonSphere = LazyOptional.of(() -> dysonSphere);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.DYSON_SPHERE)){
            return lazyDysonSphere.cast();
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
        protected double energy = 0.0d;
        protected float completion = 0.0f;
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
                if(!simulate){
                    int count = parts.getOrDefault(stack.getItem(), 0);
                    parts.put(stack.getItem(), count + stack.getCount());
                    stack.getCapability(DSCapabilities.DS_PART).ifPresent((part) -> {
                        energy += part.getEnergyProvided();
                        completion += part.getCompletionProgress();
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
            return completion;
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
            return parts.get(part) != null ? parts.get(part) : 0;
        }
        
    }

    

}
