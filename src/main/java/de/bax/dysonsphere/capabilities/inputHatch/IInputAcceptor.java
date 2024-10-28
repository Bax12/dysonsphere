package de.bax.dysonsphere.capabilities.inputHatch;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IInputAcceptor {

    public BlockEntity getTile();

    public boolean addSerialProvider(IInputProvider provider);

    public boolean addParallelProvider(IInputProvider provider);

    public boolean removeSerialProvider(IInputProvider provider);

    public boolean removeParallelProvider(IInputProvider provider);

    public List<ItemStack> getSerialInputs();

    public List<ItemStack> getParallelInputs();
} 
