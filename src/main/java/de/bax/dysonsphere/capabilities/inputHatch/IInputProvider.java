package de.bax.dysonsphere.capabilities.inputHatch;

import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public interface IInputProvider {

    public static enum ProviderType {
        PARALLEL,
        SERIAL;
    }
    
    public BlockEntity getTile();

    public LazyOptional<IItemHandler> getInventory();

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
