package de.bax.dysonsphere.capabilities.items;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
/**
 * SimpleItemStackHandler
 * implementation of @ItemStackHandler that can ignore the slot to insert and extract items.
 */
public class SimpleItemStackHandler extends ItemStackHandler {
    
    public SimpleItemStackHandler(int slots){
        super(slots);
    }
    
    public @NotNull ItemStack insertItem(@NotNull ItemStack stack, boolean simulate) {
        int i = 0;
        ItemStack returnStack = stack.copy();
        while(!returnStack.isEmpty() && i < getSlots()){
            returnStack = super.insertItem(i, returnStack, simulate);
            ++i;
        }

        return returnStack;
    }

    public @NotNull ItemStack extractItem(int amount, boolean simulate) {
        int i = 0;
        ItemStack returnStack = ItemStack.EMPTY;
        while(returnStack.isEmpty() && i < getSlots()){
            returnStack = super.extractItem(i, amount, simulate);
            ++i;
        }

        return returnStack;
    }

}
