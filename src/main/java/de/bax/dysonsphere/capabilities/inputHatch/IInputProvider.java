package de.bax.dysonsphere.capabilities.inputHatch;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public interface IInputProvider {
    
    public BlockEntity getTile();

    public LazyOptional<IInputAcceptor> getAcceptor();

    public int getAcceptorDistance();

    public LazyOptional<IItemHandler> getInventory();

}
