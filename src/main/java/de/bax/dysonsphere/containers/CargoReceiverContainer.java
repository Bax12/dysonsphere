package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.CargoReceiverTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class CargoReceiverContainer extends BaseContainer {

    public final CargoReceiverTile tile;

    public static CargoReceiverContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new CargoReceiverContainer(windowId, inv, (CargoReceiverTile) Objects.requireNonNull(inv.player.level().getBlockEntity(data.readBlockPos())));
    }

    public CargoReceiverContainer(int windowId, Inventory inv, CargoReceiverTile tile) {
        super(ModContainers.CARGO_RECEIVER_CONTAINER.get(), windowId, inv);
        this.tile = tile;

        for(int i = 0; i < 10; i++){
            addSlot(new SlotItemHandler(tile.inventory, i, 99 + 18 * (i % 2), 11 + 18 * (i / 2)));
        }

        addInventorySlots(inv, 8, 142);
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
        return !this.moveItemStackTo(newStack, 0, 9, false);
    }
    
}
