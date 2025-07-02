package de.bax.dysonsphere.capabilities.inputHatch;

import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public interface IInputProvider {

    public static enum ProviderType {
        PARALLEL,
        SERIAL,
        PROXY,
        ENERGY,
        FLUID;
    }
    
    public BlockEntity getTile();

    public default LazyOptional<IItemHandler> getInventory(){
        return LazyOptional.empty();
    };

    public default LazyOptional<IEnergyStorage> getEnergy(){
        return LazyOptional.empty();
    }

    public default LazyOptional<IFluidHandler> getFluid(){
        return LazyOptional.empty();
    }

    public LazyOptional<IInputAcceptor> getAcceptor();

    public int getAcceptorDistance();

    public Direction getAcceptorUplinkDirection();

    public Set<LazyOptional<IInputProvider>> getSubProviders(Set<LazyOptional<IInputProvider>> providerSet);

    //Convenience Method to call the 'recursive' Method
    public default Set<LazyOptional<IInputProvider>> getSubProviders(){
        return getSubProviders(new LinkedHashSet<>());
    }

    public ProviderType getType();

    public void updateUplink();

    public void resetUplink();



}
