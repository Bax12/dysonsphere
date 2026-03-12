package de.bax.dysonsphere.containers;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import de.bax.dysonsphere.tileentities.ListenerTile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ListenerContainer extends BaseContainer {

    public final ListenerTile tile;

    public static ListenerContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf data){
        return new ListenerContainer(windowId, inv, (ListenerTile) Objects.requireNonNull(inv.player.level().getBlockEntity(data.readBlockPos())));
    }

    public ListenerContainer(int windowId, Inventory inv, ListenerTile tile) {
        super(ModContainers.LISTENER_CONTAINER.get(), windowId, inv);
        this.tile = tile;

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                addSlot(new SlotItemHandler(tile.inventory, i*3 + j, 30 + 18 * j, 26 + 18 * i));
            }
        }
        addSlot(new SlotItemHandler(tile.inventory, 9, 106, 26));
        addSlot(new SlotItemHandler(tile.inventory, 10, 141, 43){
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        addInventorySlots(inv);
    }

    @Override
    protected int getInventorySlotCount() {
        return tile.inventory.getSlots();
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
        return  !this.moveItemStackTo(newStack, ListenerTile.slotShards, ListenerTile.slotShards+1, false) && !this.moveItemStackTo(newStack, ListenerTile.slotGridStart, ListenerTile.slotGridEnd+1, false);
    }
    
}
