package de.bax.dysonsphere.capabilities.inputHatch;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

public interface IInputAcceptor {

    // public BlockEntity getTile();

    // public boolean addSerialProvider(IInputProvider provider);

    // public boolean addParallelProvider(IInputProvider provider);

    // public boolean removeSerialProvider(IInputProvider provider);

    // public boolean removeParallelProvider(IInputProvider provider);

    // public List<ItemStack> getSerialInputs();

    // public List<ItemStack> getParallelInputs();

    public List<ItemStack> getItemInputs(IInputProvider.ProviderType type);

    public List<Ingredient> consumeItemInputs(List<Ingredient> items);

    public int getEnergyInput();

    public int consumeEnergy(int energy);

    public void addInputProvider(LazyOptional<IInputProvider> provider);

    public void markForRefresh(); //set a bool to refresh the collection of connected providers on the next tick. Should cascade through the providers and set the direction and distance to the acceptor. Should be called when a provider is placed or broken.

    public BlockEntity getTile();

} 
