package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.LaserPatternControllerTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LaserPatternControllerContainer extends BaseContainer {

    public final LaserPatternControllerTile tile;

    public LaserPatternControllerContainer(int windowId, Inventory inv, LaserPatternControllerTile tile) {
        super(ModContainers.LASER_PATTERN_CONTROLLER.get(), windowId, inv);
        this.tile = tile;
        // addInventorySlots(inv);
        
    }

    public static LaserPatternControllerContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new LaserPatternControllerContainer(windowId, inv, (LaserPatternControllerTile) Objects.requireNonNull(inv.player.level().getBlockEntity(data.readBlockPos())));
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return ItemStack.EMPTY;
    }

    @Override
    protected int getInventorySlotCount() {
        return 0;
    }

    @Override
    protected BlockEntity getTileEntity() {
        return tile;
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
