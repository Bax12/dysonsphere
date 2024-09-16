package de.bax.dysonsphere.containers;

import de.bax.dysonsphere.items.grapplingHook.GrapplingHookHarnessItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class GrapplingHookHarnessInventoryContainer extends BaseContainer {
    
    public final ItemStack containingStack;
    protected int playerInventoryChanges = 0;

    protected static GrapplingHookHarnessInventoryContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf buf){
        return new GrapplingHookHarnessInventoryContainer(windowId, inv, buf.readItem());
    }

    public GrapplingHookHarnessInventoryContainer(int windowId, Inventory inv, ItemStack stack){
        super(ModContainers.GRAPPLING_HOOK_HARNESS_INVENTORY_CONTAINER.get(), windowId, inv);
        this.containingStack = stack;

        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((itemHandler) -> {
            for(int i = 0; i < getInventorySlotCount(); i++){
                this.addSlot(new SlotItemHandler(itemHandler, i, 80, 50 - 20 * i));
            }
        });

        addInventorySlots(inv);
    }

    @Override
    protected int getInventorySlotCount() {
        return GrapplingHookHarnessItem.SLOTS;
    }

    @Override
    protected BlockEntity getTileEntity() {
        return null;
    }

    @Override
    protected boolean canQuickMoveToInventory(ItemStack newStack) {
        return containingStack.getCapability(ForgeCapabilities.ITEM_HANDLER).map((inventory) -> {
            return inventory.isItemValid(GrapplingHookHarnessItem.SLOT_ENGINE, newStack) || inventory.isItemValid(GrapplingHookHarnessItem.SLOT_ROPE, newStack) || inventory.isItemValid(GrapplingHookHarnessItem.SLOT_HOOK, newStack);
        }).orElse(false);
    }

    @Override
    protected boolean quickMoveToInventory(ItemStack newStack) {
        return !this.moveItemStackTo(newStack, 0, 3, false);
    }

    @Override
    public boolean stillValid(Player player) {
        //indicates the inventory was not changed since the last successful check
        //as a unsuccessful check will close the container. 
        //Should reduce the amount of inventory iterations to find the stack.
        if(playerInventoryChanges == player.getInventory().getTimesChanged()){ 
            return true;                                                       
        }                                                                      
        playerInventoryChanges = player.getInventory().getTimesChanged();
        return player.getInventory().contains(containingStack);
    }


}
