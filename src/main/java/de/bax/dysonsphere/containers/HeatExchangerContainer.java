package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.HeatExchangerTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class HeatExchangerContainer extends BaseContainer {
    
    public final HeatExchangerTile tile;

    public static HeatExchangerContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new HeatExchangerContainer(windowId, inv, (HeatExchangerTile) Objects.requireNonNull(inv.player.level().getBlockEntity(data.readBlockPos())));
    }

    public HeatExchangerContainer(int windowId, Inventory inv, HeatExchangerTile tile){
        super(ModContainers.HEAT_EXCHANGER_CONTAINER.get(), windowId, inv);

        this.tile = tile;

        this.addSlot(new SlotItemHandler(tile.inventory, tile.slotInput, 39, 69));
        this.addSlot(new SlotItemHandler(tile.inventory, tile.slotOutput, 121, 69));

        addInventorySlots(inv);
    }

    @Override
    protected int getInventorySlotCount() {
        return 2;
    }

    @Override
    protected BlockEntity getTileEntity() {
        return tile;
    }

    @Override
    protected boolean canQuickMoveToInventory(ItemStack newStack) {
        return tile.inventory.isItemValid(0, newStack) || tile.inventory.isItemValid(1, newStack);
    }

    @Override
    protected boolean quickMoveToInventory(ItemStack newStack) {
        return !this.moveItemStackTo(newStack, 0, 2, false);
    }

}
