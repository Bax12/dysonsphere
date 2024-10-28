package de.bax.dysonsphere.tileentities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class InputHatchTile extends BaseTile {


    public ItemStackHandler input = new ItemStackHandler(getSlotCount()){
        protected void onContentsChanged(int slot) {
            setChanged();
        };

        public int getSlotLimit(int slot) {
            return getSlotSize(slot);
        };
    };

    protected LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> input);

    protected boolean dirty = false;

    public InputHatchTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(ForgeCapabilities.ITEM_HANDLER)){
            return lazyInv.cast();
        }
        return LazyOptional.empty();
    }

    protected abstract int getSlotCount();
    protected abstract int getSlotSize(int slot);
    
    @Override
    public void setChanged() {
        super.setChanged();
        this.dirty = true;
    }


    public static class Serial extends InputHatchTile {

        public static final int SLOTS = 5;

        public Serial(BlockPos pos, BlockState state) {
            super(ModTiles.INPUT_HATCH_SERIAL.get(), pos, state);
        } 

        @Override
        protected int getSlotCount() {
            return SLOTS;
        }

        @Override
        protected int getSlotSize(int slot) {
            return 64;
        }

    }
    

    public static class Parallel extends InputHatchTile {

        public static final int SLOTS = 1;

        public Parallel(BlockPos pos, BlockState state) {
            super(ModTiles.INPUT_HATCH_PARALLEL.get(), pos, state);
        } 

        @Override
        protected int getSlotCount() {
            return SLOTS;
        }

        @Override
        protected int getSlotSize(int slot) {
            return 1;
        }

    }

}
