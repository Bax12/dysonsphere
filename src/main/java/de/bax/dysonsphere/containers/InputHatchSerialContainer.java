package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.InputHatchTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class InputHatchSerialContainer extends BaseContainer {

    public final InputHatchTile.Serial tile;

    public static InputHatchSerialContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new InputHatchSerialContainer(windowId, inv, (InputHatchTile.Serial) Objects.requireNonNull(inv.player.level().getBlockEntity(data.readBlockPos())));
    }

    public InputHatchSerialContainer(int windowId, Inventory inv, InputHatchTile.Serial tile){
        super(ModContainers.INPUT_HATCH_SERIAL_CONTAINER.get(), windowId, inv);
        this.tile = tile;

        for(int i = 0; i < getInventorySlotCount(); i++){
            this.addSlot(new SlotItemHandler(tile.input, i, 40 + i * 20, 30));
        }

        addInventorySlots(inv);
    }

    @Override
    protected int getInventorySlotCount() {
        return tile.input.getSlots();
    }

    @Override
    protected BlockEntity getTileEntity() {
        return tile;
    }

    @Override
    protected boolean canQuickMoveToInventory(ItemStack newStack) {
        return tile.input.isItemValid(0, newStack); //Just using index 0 as the check ignores the slot anyways
    }

    @Override
    protected boolean quickMoveToInventory(ItemStack newStack) {
        return !this.moveItemStackTo(newStack, 0, 5, false);
    }
    
}
