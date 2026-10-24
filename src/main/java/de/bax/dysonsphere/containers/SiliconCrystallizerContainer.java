package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.SiliconCrystallizerTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class SiliconCrystallizerContainer extends BaseContainer {

    public static SiliconCrystallizerContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf buf) {
        return new SiliconCrystallizerContainer(windowId, inv, (SiliconCrystallizerTile) Objects.requireNonNull(inv.player.level().getBlockEntity(buf.readBlockPos())));
    }

    public final SiliconCrystallizerTile tile;

    public SiliconCrystallizerContainer(int windowId, Inventory inv, SiliconCrystallizerTile tile) {
        super(ModContainers.SILICON_CRYSTALLIZER_CONTAINER.get(), windowId, inv);
        
        this.tile = tile;

        addSlot(new SlotItemHandler(tile.seedInput, 0, 78, 24));
        addSlot(new SlotItemHandler(tile.output, 0, 127, 40));

        addInventorySlots(inv);
    }

    @Override
    protected int getInventorySlotCount() {
        return SiliconCrystallizerTile.SLOTS;
    }

    @Override
    protected BlockEntity getTileEntity() {
        return tile;
    }

    @Override
    protected boolean canQuickMoveToInventory(ItemStack newStack) {
        return tile.seedInput.isItemValid(SiliconCrystallizerTile.SLOT_INPUT, newStack);
    }

    @Override
    protected boolean quickMoveToInventory(ItemStack newStack) {
        return !this.moveItemStackTo(newStack, SiliconCrystallizerTile.SLOT_INPUT, SiliconCrystallizerTile.SLOT_INPUT + 1, false);
    }
    
}
