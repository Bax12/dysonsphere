package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.LaserControllerTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class LaserControllerContainer extends BaseContainer {

    public final LaserControllerTile tile;

    public static LaserControllerContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new LaserControllerContainer(windowId, inv, (LaserControllerTile) Objects.requireNonNull(inv.player.level().getBlockEntity(data.readBlockPos())));
    }

    public LaserControllerContainer(int windowId, Inventory inv, LaserControllerTile tile) {
        super(ModContainers.LASER_CONTROLLER_CONTAINER.get(), windowId, inv);
        this.tile = tile;

        this.addSlot(new SlotItemHandler(tile.inventory, 0, 47, 64));

        this.addInventorySlots(inv);
    }

    @Override
    protected int getInventorySlotCount() {
        return tile.inventory.getSlots();
    }

    @Override
    protected BlockEntity getTileEntity() {
        return tile;
    }

    @Override
    protected boolean canQuickMoveToInventory(ItemStack newStack) {
        return tile.inventory.isItemValid(0, newStack);
    }

    @Override
    protected boolean quickMoveToInventory(ItemStack newStack) {
        return !this.moveItemStackTo(newStack, 0, 1, false); //slotindex_start, slotindex_end +1
    }

    
    
}
