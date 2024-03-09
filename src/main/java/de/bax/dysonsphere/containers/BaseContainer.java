package de.bax.dysonsphere.containers;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BaseContainer extends AbstractContainerMenu {
    
    public BaseContainer(MenuType<?> menuType, int windowId, Inventory inv) {
        super(menuType, windowId);
    }

    protected abstract int getInventorySlotCount();
    protected abstract BlockEntity getTileEntity();
    protected abstract boolean canQuickMoveToInventory(ItemStack newStack);
    protected abstract boolean quickMoveToInventory(ItemStack newStack);

    public void addInventorySlots(Inventory inv){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 97 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inv, i, 8 + i * 18, 155));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        int inventoryStart = getInventorySlotCount();
        int inventoryEnd = inventoryStart + 26;
        int hotbarStart = inventoryEnd + 1;
        int hotbarEnd = hotbarStart + 8;

        Slot theSlot = this.slots.get(slot);

        if (theSlot != null && theSlot.hasItem()) {
            ItemStack newStack = theSlot.getItem();
            ItemStack currentStack = newStack.copy();

            //Slots in Inventory to shift from
            if (slot < inventoryStart) {
                if (!this.moveItemStackTo(newStack, inventoryStart, hotbarEnd + 1, true)) {
                    return ItemStack.EMPTY;
                }
                theSlot.onQuickCraft(newStack, currentStack);
            }
            //Other Slots in Inventory excluded
            else if (slot >= inventoryStart) {
                if(canQuickMoveToInventory(newStack)){
                    if(quickMoveToInventory(newStack)){
                        return ItemStack.EMPTY;
                    }
                } else if (slot >= inventoryStart && slot <= inventoryEnd) {
                    if (!this.moveItemStackTo(newStack, hotbarStart, hotbarEnd + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slot >= inventoryEnd + 1 && slot < hotbarEnd + 1 && !this.moveItemStackTo(newStack, inventoryStart, inventoryEnd + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(newStack, inventoryStart, hotbarEnd + 1, false)) {
                return ItemStack.EMPTY;
            }

            if (newStack.isEmpty()) {
                theSlot.set(ItemStack.EMPTY);
            } else {
                theSlot.setChanged();
            }

            if (newStack.getCount() == currentStack.getCount()) {
                return ItemStack.EMPTY;
            }
            theSlot.onTake(player, newStack);

            return currentStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(getTileEntity().getBlockPos().getX() + 0.5D, getTileEntity().getBlockPos().getY() + 0.5D, getTileEntity().getBlockPos().getZ() + 0.5D) <= 64 && !getTileEntity().isRemoved() && getTileEntity().getLevel().getBlockEntity(getTileEntity().getBlockPos()) == getTileEntity();
    }

    

}
