package de.bax.dysonsphere.containers;

import java.util.Objects;

import de.bax.dysonsphere.tileentities.SchematicWorkbenchTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class SchematicWorkbenchInventoryContainer extends BaseContainer {

    public static SchematicWorkbenchInventoryContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf buf){
        return new SchematicWorkbenchInventoryContainer(windowId, inv, (SchematicWorkbenchTile) Objects.requireNonNull(inv.player.level().getBlockEntity(buf.readBlockPos())));
    }

    public final SchematicWorkbenchTile tile;

    public SchematicWorkbenchInventoryContainer(int windowId, Inventory inv, SchematicWorkbenchTile tile) {
        super(ModContainers.SCHEMATIC_WORKBENCH_INVENTORY_CONTAINER.get(), windowId, inv);
        
        this.tile = tile;

        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 9; j++){
                addSlot(new SlotItemHandler(tile.inventory, i*9+j, 8 + 18 * j, 28 + 18 * i));
            }
        }

        addInventorySlots(inv);
    }

    @Override
    protected int getInventorySlotCount() {
        return SchematicWorkbenchTile.SLOTS;
    }

    @Override
    protected BlockEntity getTileEntity() {
        return tile;
    }

    @Override
    protected boolean canQuickMoveToInventory(ItemStack newStack) {
        return tile.inventory.isItemValid(0, newStack);
    }

    @Override
    protected boolean quickMoveToInventory(ItemStack newStack) {
        return !this.moveItemStackTo(newStack, 0, SchematicWorkbenchTile.SLOTS, false);
    }
    
}
