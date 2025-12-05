package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.DSControllerTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DSControllerContainer extends BaseContainer {

    public final DSControllerTile tile;

    public static DSControllerContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new DSControllerContainer(windowId, inv, (DSControllerTile) Objects.requireNonNull(inv.player.level().getBlockEntity(data.readBlockPos())));
    }

    public DSControllerContainer(int windowId, Inventory inv, DSControllerTile tile) {
        super(ModContainers.DS_CONTROLLER_CONTAINER.get(), windowId, inv);

        this.tile = tile;
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
