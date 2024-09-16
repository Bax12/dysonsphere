package de.bax.dysonsphere.containers;

import de.bax.dysonsphere.items.laser.LaserControllerItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class LaserControllerInventoryContainer extends BaseContainer {

    public final ItemStack containingStack;
    protected int playerInventoryChanges = 0;

    public static LaserControllerInventoryContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new LaserControllerInventoryContainer(windowId, inv, data.readItem());
    }

    public LaserControllerInventoryContainer(int windowId, Inventory inv, ItemStack stack) {
        super(ModContainers.LASER_CONTROLLER_INVENTORY_CONTAINER.get(), windowId, inv);
        this.containingStack = stack;

        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((itemHandler) -> {
            for(int i = 0; i < getInventorySlotCount(); i++){
                this.addSlot(new SlotItemHandler(itemHandler, i, 50 + i * 20, 30));
            }
        });

        addInventorySlots(inv);
    }

    @Override
    protected int getInventorySlotCount() {
        return LaserControllerItem.slots;
    }

    @Override
    protected BlockEntity getTileEntity() {
        return null;
    }

    @Override
    protected boolean canQuickMoveToInventory(ItemStack newStack) {
        return containingStack.getCapability(ForgeCapabilities.ITEM_HANDLER).map((inventory) -> {
            return inventory.isItemValid(0, newStack); //Just using index 0 as the check ignores the slot anyways
        }).orElse(false);
    }

    @Override
    protected boolean quickMoveToInventory(ItemStack newStack) {
        return !this.moveItemStackTo(newStack, 0, 6, false);
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
