package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.HeatGeneratorTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class HeatGeneratorContainer extends BaseContainer {
    
    public final HeatGeneratorTile tile;
    public char axis;

    public static HeatGeneratorContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new HeatGeneratorContainer(windowId, inv, (HeatGeneratorTile) Objects.requireNonNull(inv.player.level().getBlockEntity(data.readBlockPos())), data.readChar());
    }

    public HeatGeneratorContainer(int windowId, Inventory inv, HeatGeneratorTile tile, char axis) {
        super(ModContainers.HEAT_GENERATOR_CONTAINER.get(), windowId, inv);

        this.tile = tile;
        this.axis = axis;

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
