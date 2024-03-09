package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.RailgunTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class RailgunContainer extends BaseContainer {

    public final RailgunTile tile;

    public static RailgunContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new RailgunContainer(windowId, inv, (RailgunTile) Objects.requireNonNull(inv.player.level().getBlockEntity(data.readBlockPos())));
    }

    public RailgunContainer(int windowId, Inventory inv, RailgunTile tile) {
        super(ModContainers.RAILGUN_CONTAINER.get(), windowId, inv);
        this.tile = tile;

        this.addSlot(new SlotItemHandler(tile.inventory, 0, 80, 64));

        this.addInventorySlots(inv);
    }

    @Override
    protected BlockEntity getTileEntity() {
        return tile;
    }

    @Override
    protected int getInventorySlotCount() {
        return tile.inventory.getSlots();
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
