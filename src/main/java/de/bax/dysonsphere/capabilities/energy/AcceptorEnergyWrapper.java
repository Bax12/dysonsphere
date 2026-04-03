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

    //receive to internal first
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = internalStorage.map(e -> e.receiveEnergy(maxReceive, simulate)).orElse(0);
        Iterator<LazyOptional<IEnergyStorage>> storageIterator = inputAcceptor.getEnergyProviders().iterator();
        while (storageIterator.hasNext() && maxReceive - received > 0) {
            int toReceive = maxReceive - received;
            received += storageIterator.next().map(e -> e.receiveEnergy(toReceive, simulate)).orElse(0);
        }
        return received;
    }

    //extract from providers first
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        Iterator<LazyOptional<IEnergyStorage>> storageIterator = inputAcceptor.getEnergyProviders().iterator();
        int extracted = 0;
        while (storageIterator.hasNext() && maxExtract - extracted > 0) {
            int toExtract = maxExtract - extracted;
            extracted += storageIterator.next().map(e -> e.extractEnergy(toExtract, simulate)).orElse(0);
        }
        int leftover = maxExtract - extracted;
        if(leftover > 0){
            extracted += internalStorage.map(e -> e.extractEnergy(leftover, simulate)).orElse(0);
        }
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return inputAcceptor.getEnergyProviders().stream().mapToInt((lazy) -> {
            return lazy.map(IEnergyStorage::getEnergyStored).orElse(0);
        }).sum() + 
        internalStorage.map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    @Override
    public int getMaxEnergyStored() {
        return inputAcceptor.getEnergyProviders().stream().mapToInt((lazy) -> {
            return lazy.map(IEnergyStorage::getMaxEnergyStored).orElse(0);
        }).sum() + 
        internalStorage.map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }

    @Override
    public boolean canExtract() {
        return internalStorage.map(IEnergyStorage::canExtract).orElse(false) || 
            inputAcceptor.getEnergyProviders().stream().filter((lazy) -> {
                return lazy.map(IEnergyStorage::canExtract).orElse(false);
            }).count() > 0;
    }

    @Override
    public boolean canReceive() {
        return internalStorage.map(IEnergyStorage::canReceive).orElse(false) || 
            inputAcceptor.getEnergyProviders().stream().filter((lazy) -> {
                return lazy.map(IEnergyStorage::canReceive).orElse(false);
            }).count() > 0;
    }
    
}
