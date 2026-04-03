package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.HeatConverterTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class HeatConverterContainer extends BaseContainer {

    public final HeatConverterTile tile;

    public static HeatConverterContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new HeatConverterContainer(windowId, inv, (HeatConverterTile) Objects.requireNonNull(inv.player.level().getBlockEntity(data.readBlockPos())));
    }

    public HeatConverterContainer(int windowId, Inventory inv, HeatConverterTile tile) {
        super(ModContainers.HEAT_CONVERTER_CONTAINER.get(), windowId, inv);
        
        this.tile = tile;

        addInventorySlots(inv);
    }

    @Override
    protected int getInventorySlotCount() {
        return 0;
    }

    @Override
    protected BlockEntity getTileEntity() {
        return tile;
    }

    @Override
    protected boolean canQuickMoveToInventory(ItemStack newStack) {
        return false;
    }

    @Override
    protected boolean quickMoveToInventory(ItemStack newStack) {
        return false;
    }
    
}
