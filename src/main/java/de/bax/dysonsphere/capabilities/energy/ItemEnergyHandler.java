package de.bax.dysonsphere.capabilities.energy;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

public class ItemEnergyHandler implements IEnergyStorage {

    public static final String ENERGY_TAG = "energy";

    protected final ItemStack container;

    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public ItemEnergyHandler(ItemStack container, int capacity){
        this(container, capacity, capacity, capacity, 0);
    }

    public ItemEnergyHandler(ItemStack container, int capacity, int maxTransfer){
        this(container, capacity, maxTransfer, maxTransfer, 0);
    }

    public ItemEnergyHandler(ItemStack container, int capacity, int maxReceive, int maxExtract){
        this(container, capacity, maxReceive, maxExtract, 0);
    }

    public ItemEnergyHandler(ItemStack container, int capacity, int maxReceive, int maxExtract, int energy){
        this.container = container;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int energy = getEnergy();
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate){
            setEnergy(energy + energyReceived);
        }
            
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;
        int energy = getEnergy();
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate){
            setEnergy(energy - energyExtracted);
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return getEnergy();
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }   

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    public int getEnergy(){
        return container.getOrCreateTag().getInt(ENERGY_TAG);
    }

    public void setEnergy(int energy){
        container.getOrCreateTag().putInt(ENERGY_TAG, energy);
    }
    
    
}
