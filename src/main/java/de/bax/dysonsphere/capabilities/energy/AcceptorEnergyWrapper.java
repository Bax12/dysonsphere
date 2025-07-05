package de.bax.dysonsphere.capabilities.energy;

import java.util.Iterator;

import de.bax.dysonsphere.capabilities.inputHatch.IInputAcceptor;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class AcceptorEnergyWrapper implements IEnergyStorage {

    // protected Set<LazyOptional<IEnergyStorage>> storages;

    // public AcceptorEnergyWrapper(){
    //     // storages = new HashSet<LazyOptional<IEnergyStorage>>();
    // }

    protected LazyOptional<IEnergyStorage> internalStorage;
    protected IInputAcceptor inputAcceptor;

    public AcceptorEnergyWrapper(LazyOptional<IEnergyStorage> internalStorage, IInputAcceptor inputAcceptor){
        this.internalStorage = internalStorage;
        this.inputAcceptor = inputAcceptor;
    }


    // public void addStorage(LazyOptional<IEnergyStorage> storage){
    //     if(storages.add(storage)){
    //         storage.addListener((lazy) -> {
    //             storages.remove(lazy);
    //         });
    //     }
    // }

    // public void removeStorage(LazyOptional<IEnergyStorage> storage){
    //     storages.remove(storage);
    // }

    public boolean hasExtraStorages(){
        return !inputAcceptor.getEnergyProviders().isEmpty();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        Iterator<LazyOptional<IEnergyStorage>> storageIterator = inputAcceptor.getEnergyProviders().iterator();
        int received = 0;
        while (storageIterator.hasNext() && maxReceive - received > 0) {
            int toReceive = maxReceive - received;
            received += storageIterator.next().map((energy) -> {
                return energy.receiveEnergy(toReceive, simulate);
            }).orElse(0);
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        Iterator<LazyOptional<IEnergyStorage>> storageIterator = inputAcceptor.getEnergyProviders().iterator();
        int extracted = 0;
        while (storageIterator.hasNext() && maxExtract - extracted > 0) {
            int toExtract = maxExtract - extracted;
            extracted += storageIterator.next().map((energy) -> {
                return energy.extractEnergy(toExtract, simulate);
            }).orElse(0);
        }
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return inputAcceptor.getEnergyProviders().stream().mapToInt((lazy) -> {
            return lazy.map((energy) -> {
                return energy.getEnergyStored();
            }).orElse(0);
        }).sum();
    }

    @Override
    public int getMaxEnergyStored() {
        return inputAcceptor.getEnergyProviders().stream().mapToInt((lazy) -> {
            return lazy.map((energy) -> {
                return energy.getMaxEnergyStored();
            }).orElse(0);
        }).sum();
    }

    @Override
    public boolean canExtract() {
        return inputAcceptor.getEnergyProviders().stream().filter((lazy) -> {
            return lazy.map((energy) -> {
                return energy.canExtract();
            }).orElse(false);
        }).count() > 0;
    }

    @Override
    public boolean canReceive() {
        return inputAcceptor.getEnergyProviders().stream().filter((lazy) -> {
            return lazy.map((energy) -> {
                return energy.canReceive();
            }).orElse(false);
        }).count() > 0;
    }
    
}
