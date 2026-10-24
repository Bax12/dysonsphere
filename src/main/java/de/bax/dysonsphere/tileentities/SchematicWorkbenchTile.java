package de.bax.dysonsphere.tileentities;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class SchematicWorkbenchTile extends BaseTile {

    public static final int SLOTS = 18;

    public ItemStackHandler inventory = new ItemStackHandler(SLOTS){ //no published capability. That would only cause issues.
        protected void onContentsChanged(int slot) {
            setChanged();
        };

        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getCapability(DSCapabilities.SCHEMATIC_CONTAINER).isPresent() || stack.is(ModItems.BLUEPRINT_BOOK.get());
        };
    };

    public SchematicWorkbenchTile(BlockPos pos, BlockState state) {
        super(ModTiles.SCHEMATIC_WORKBENCH.get(), pos, state);
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("inv")){
            inventory.deserializeNBT(pTag.getCompound("inv"));
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inv", inventory.serializeNBT());
    }

    public void onRemove(){
        for(int i = 0; i < inventory.getSlots(); i++){
            ItemEntity item = new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), inventory.getStackInSlot(i));
            level.addFreshEntity(item);
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }
    
}
