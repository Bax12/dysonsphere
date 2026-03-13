package de.bax.dysonsphere.tileentities;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class OreSpireTile extends BaseTile {

    public ItemStackHandler inventory = new ItemStackHandler(1){
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            ItemStack ret = super.insertItem(slot, stack, simulate);
            if(!simulate){
                if(maxInventory < this.getStackInSlot(0).getCount()){
                    maxInventory = this.getStackInSlot(0).getCount();
                }
                setChanged();
            }
            return ret;
        };

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack ret = super.extractItem(slot, amount, simulate);
            if(!simulate){
                setChanged();
            }
            return ret;
        };

        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(Tags.Items.RAW_MATERIALS);
        };

        @Override
        public int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 1024; //normal chest holds 1728
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = super.serializeNBT();
            tag.putInt("intCount", getStackInSlot(0).getCount());//always int, until saved and loaded... then it is a byte.
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            super.deserializeNBT(nbt);
            getStackInSlot(0).setCount(nbt.getInt("intCount"));
        }
    };

    protected int maxInventory = 0;

    protected LazyOptional<IItemHandler> lazyInventory = LazyOptional.of(() -> inventory);

    public OreSpireTile(BlockPos pos, BlockState state) {
        super(ModTiles.ORE_SPIRE.get(), pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(ForgeCapabilities.ITEM_HANDLER)) {
            return lazyInventory.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyInventory.invalidate();
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inv", inventory.serializeNBT());
        pTag.putInt("max", maxInventory);
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        inventory.deserializeNBT(pTag.getCompound("inv"));
        maxInventory = pTag.getInt("max");
    }

    @Override
    public void setChanged() {
        super.setChanged();
        sendSyncPackageToNearbyPlayers();
    }

    public void dropContent() {
        for(int i = 0; i < inventory.getSlots(); i++){
            ItemEntity entity = new ItemEntity(level, getBlockPos().getX(),getBlockPos().getY(), getBlockPos().getZ(), inventory.getStackInSlot(i));
            level.addFreshEntity(entity);
        }
    }

    public int getMaxInventory() {
        return maxInventory;
    }
    
}
