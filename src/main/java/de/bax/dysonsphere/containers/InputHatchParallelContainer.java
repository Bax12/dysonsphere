package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.InputHatchTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class InputHatchParallelContainer extends BaseContainer {
    
    public final InputHatchTile.Parallel tile;

    public static InputHatchParallelContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new InputHatchParallelContainer(windowId, inv, (InputHatchTile.Parallel) Objects.requireNonNull(inv.player.level().getBlockEntity(data.readBlockPos())));
    }

    public InputHatchParallelContainer(int windowId, Inventory inv, InputHatchTile.Parallel tile){
        super(ModContainers.INPUT_HATCH_PARALLEL_CONTAINER.get(), windowId, inv);

        this.tile = tile;

        this.addSlot(new SlotItemHandler(tile.input, 0, 65, 30));

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
        return tile.input.isItemValid(0, newStack);
    }

    @Override
    protected boolean quickMoveToInventory(ItemStack newStack) {
        return !this.moveItemStackTo(newStack, 0, 1, false);
    }

}
