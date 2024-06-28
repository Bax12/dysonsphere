package de.bax.dysonsphere.containers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LaserPatternControllerContainer extends BaseContainer {

    public LaserPatternControllerContainer(int windowId, Inventory inv) {
        super(ModContainers.LASER_PATTERN_CONTROLLER.get(), windowId, inv);

        // addInventorySlots(inv);
        
    }

    public static LaserPatternControllerContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new LaserPatternControllerContainer(windowId, inv);
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    protected int getInventorySlotCount() {
        return 0;
    }

    @Override
    protected BlockEntity getTileEntity() {
        return null;
    }

    @Override
    protected boolean canQuickMoveToInventory(ItemStack newStack) {
        return false;
    }

    @Override
    protected boolean quickMoveToInventory(ItemStack newStack) {
        return false;
    }
    
}
