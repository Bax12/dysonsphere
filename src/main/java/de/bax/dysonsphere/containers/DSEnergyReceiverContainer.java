package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.DSEnergyReceiverTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DSEnergyReceiverContainer extends BaseContainer {

    public final DSEnergyReceiverTile tile;

    public static DSEnergyReceiverContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new DSEnergyReceiverContainer(windowId, inv, (DSEnergyReceiverTile) Objects.requireNonNull(inv.player.level().getBlockEntity(data.readBlockPos())));
    }

    public DSEnergyReceiverContainer(int windowId, Inventory inv, DSEnergyReceiverTile tile) {
        super(ModContainers.DS_ENERGY_RECEIVER_CONTAINER.get(), windowId, inv);
        this.tile = tile;


        this.addInventorySlots(inv);
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
