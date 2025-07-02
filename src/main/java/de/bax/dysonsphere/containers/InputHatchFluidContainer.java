package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.InputHatchTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class InputHatchFluidContainer extends BaseContainer {
    public final InputHatchTile.Fluid tile;

    public static InputHatchFluidContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new InputHatchFluidContainer(windowId, inv, (InputHatchTile.Fluid) Objects.requireNonNull(inv.player.level().getBlockEntity(data.readBlockPos())));
    }

    public InputHatchFluidContainer(int windowId, Inventory inv, InputHatchTile.Fluid tile) {
        super(ModContainers.INPUT_HATCH_FLUID_CONTAINER.get(), windowId, inv);
        this.tile = tile;

        this.addSlot(new SlotItemHandler(tile.input, InputHatchTile.Fluid.SLOT_INPUT, 79, 25));
        this.addSlot(new SlotItemHandler(tile.input, InputHatchTile.Fluid.SLOT_OUTPUT, 79, 55));

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
        return tile.input.isItemValid(InputHatchTile.Fluid.SLOT_INPUT, newStack) || tile.input.isItemValid(InputHatchTile.Fluid.SLOT_OUTPUT, newStack);
    }

    @Override
    protected boolean quickMoveToInventory(ItemStack newStack) {
        return !this.moveItemStackTo(newStack, 0, 2, false);
    }
}
