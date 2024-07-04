package de.bax.dysonsphere.capabilities.items;

import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemItemStackHandler implements IItemHandlerModifiable{

    public static final String INVENTORY_TAG = "inventory";

    protected ItemStack container;
    protected int slots;

    public ItemItemStackHandler(ItemStack container, int slots){
        this.container = container;
        this.slots = slots;
    }

    @Override
    public int getSlots() {
        return slots;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if(slot < 0 && slot >= slots){
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + slots + ")");
        }
        
        ItemStack stack = ItemStack.of((CompoundTag) container.getOrCreateTagElement(INVENTORY_TAG).get("" + slot));
        return stack;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if(slot < 0 && slot >= slots){
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + slots + ")");
        }
        container.getOrCreateTagElement(INVENTORY_TAG).put("" + slot, stack.serializeNBT());
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        

        ItemStack existing = getStackInSlot(slot);

        int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

        if (!existing.isEmpty())
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate)
        {
            setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
        return ItemStack.EMPTY;

        ItemStack existing = getStackInSlot(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract)
        {
            if (!simulate)
            {
                setStackInSlot(slot, ItemStack.EMPTY);
                
                return existing;
            }
            else
            {
                return existing.copy();
            }
        }
        else
        {
            if (!simulate)
            {
                setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return getStackInSlot(slot).getMaxStackSize();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }

}
