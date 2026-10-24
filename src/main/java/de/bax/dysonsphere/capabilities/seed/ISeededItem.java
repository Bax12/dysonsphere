package de.bax.dysonsphere.capabilities.seed;

import net.minecraft.world.item.ItemStack;

public interface ISeededItem {

    public void setSeed(ItemStack stack);

    public ItemStack getSeed();
} 
