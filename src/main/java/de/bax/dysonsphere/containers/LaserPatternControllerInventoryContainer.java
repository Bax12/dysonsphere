package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.LaserPatternControllerTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class LaserPatternControllerInventoryContainer extends BaseContainer {

    public final LaserPatternControllerTile tile;

    public LaserPatternControllerInventoryContainer(int windowId, Inventory inv, LaserPatternControllerTile tile) {
        super(ModContainers.LASER_PATTERN_CONTROLLER_INVENTORY_CONTAINER.get(), windowId, inv);
        this.tile = tile;

        this.addSlot(new SlotItemHandler(tile.inventory, 0, 100, 30));

        addInventorySlots(inv);
    }
    

    public static LaserPatternControllerInventoryContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new LaserPatternControllerInventoryContainer(windowId, inv, (LaserPatternControllerTile) Objects.requireNonNull(inv.player.level().getBlockEntity(data.readBlockPos())));
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
        return !this.moveItemStackTo(newStack, 0, 1, false);
    }


}
