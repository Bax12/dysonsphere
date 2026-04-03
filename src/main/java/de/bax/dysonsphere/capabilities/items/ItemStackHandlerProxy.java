package de.bax.dysonsphere.capabilities.items;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerProxy extends ItemStackHandler {
    
    protected ItemStackHandler internalHandler;

    public ItemStackHandlerProxy(ItemStackHandler handler){
        super(handler.getSlots());
        this.internalHandler = handler;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return internalHandler.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return internalHandler.extractItem(slot, amount, simulate);
    }
    
    @Override
    public int getSlots() {
        return internalHandler.getSlots();
    }

    @Override
    public int getSlotLimit(int slot) {
        return internalHandler.getSlotLimit(slot);
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return internalHandler.getStackInSlot(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return internalHandler.isItemValid(slot, stack);
    }

    @Override
    public void setSize(int size) {
        internalHandler.setSize(size);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        internalHandler.setStackInSlot(slot, stack);
    }

}
