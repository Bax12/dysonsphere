package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.MelterTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class MelterContainer extends BaseContainer {

    public final MelterTile tile;

    public static MelterContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf buf) {
        return new MelterContainer(windowId, inv, (MelterTile) Objects.requireNonNull(inv.player.level().getBlockEntity(buf.readBlockPos())));
    }

    public MelterContainer(int windowId, Inventory inv, MelterTile tile) {
        super(ModContainers.MELTER_CONTAINER.get(), windowId, inv);
        
        this.tile = tile;

        addSlot(new SlotItemHandler(tile.input, MelterTile.SLOT_INPUT, 78, 38));
        addSlot(new SlotItemHandler(tile.input, MelterTile.SLOT_OUTPUT, 121, 69));

        addInventorySlots(inv);
    }

    @Override
    protected int getInventorySlotCount() {
        return MelterTile.SLOTS;
    }

    @Override
    protected BlockEntity getTileEntity() {
        return tile;
    }

    @Override
    protected boolean canQuickMoveToInventory(ItemStack newStack) {
        return tile.input.isItemValid(MelterTile.SLOT_INPUT, newStack) || tile.input.isItemValid(MelterTile.SLOT_OUTPUT, newStack);
    }

    @Override
    protected boolean quickMoveToInventory(ItemStack newStack) {
        return !this.moveItemStackTo(newStack, MelterTile.SLOT_INPUT, MelterTile.SLOTS, false);
    }
    
}
