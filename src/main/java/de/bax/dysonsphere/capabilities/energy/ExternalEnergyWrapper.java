package de.bax.dysonsphere.capabilities.energy;

import net.minecraftforge.energy.IEnergyStorage;

//used to pass different parameters via Capabilities then internal
//change needed Methods per Instance Overwrite.
public class ExternalEnergyWrapper implements IEnergyStorage {

    protected final IEnergyStorage storage;

    public ExternalEnergyWrapper(IEnergyStorage wrappedStorage){
        this.storage = wrappedStorage;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return storage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return storage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return storage.canReceive();
    }
    
}
