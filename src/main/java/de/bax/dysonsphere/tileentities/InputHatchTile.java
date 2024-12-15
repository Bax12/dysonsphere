package de.bax.dysonsphere.tileentities;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import de.bax.dysonsphere.color.ModColors.ITintableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class InputHatchTile extends BaseTile {


    public static double MAX_HEAT = 1700;

    public ItemStackHandler input = new ItemStackHandler(getSlotCount()){
        protected void onContentsChanged(int slot) {
            setChanged();
        };

        public int getSlotLimit(int slot) {
            return getSlotSize(slot);
        };
    };

    public HeatHandler heatHandler = new HeatHandler(MAX_HEAT){
        @Override
        public double receiveHeat(double maxReceive, boolean simulate) {
            if(!simulate){
                setChanged();
            }
            return super.receiveHeat(maxReceive, simulate);
        }

        @Override
        public double extractHeat(double maxExtract, boolean simulate) {
            if(!simulate){
                setChanged();
            }
            return super.extractHeat(maxExtract, simulate);
        }
    };

    protected LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> input);
    protected LazyOptional<IHeatContainer> lazyHeat = LazyOptional.of(() -> heatHandler);

    protected boolean dirty = false;
    protected boolean isHeatConducting = false;
    protected int ticksElapsed = 0;

    public InputHatchTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract int getSignalStrength();


    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("invInput")){
            input.deserializeNBT(pTag.getCompound("invInput"));
        }
        if(pTag.contains("heat")){
            heatHandler.deserializeNBT(pTag.getCompound("heat"));
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("invInput", input.serializeNBT());
        pTag.put("heat", heatHandler.serializeNBT());
    }

    @SuppressWarnings("null")
    public void tick(){
        if(!level.isClientSide){
            ticksElapsed++;
            if(ticksElapsed % 5 == 0){

                if(isHeatConducting){
                    heatHandler.splitShare();
                    sendSyncPackageToNearbyPlayers();
                }
            }
        } else {
            level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), getBlockState(), getBlockState(), 2, 0); 
        }
    }

    public void onNeighborChange(){
        heatHandler.updateNeighbors(level, worldPosition);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(ForgeCapabilities.ITEM_HANDLER)){
            return lazyInv.cast();
        }
        if(isHeatConducting && cap.equals(DSCapabilities.HEAT)){
            return lazyHeat.cast();
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

    
    public int getTintColor(int tintIndex) {
        if(!isHeatConducting) return 0x00252525;
        int col = 0xFFFF0000;
        int offset = 255 - (int) Math.min(Math.max(this.heatHandler.getHeatStored() - HeatHandler.HEAT_AMBIENT, 0) / 5, 255);

        return 0x00b4b4b4 & (col + offset + (offset << 8));
    }

    @SuppressWarnings("null")
    public void dropContent() {
        for(int i = 0; i < input.getSlots(); i++){
            ItemEntity entity = new ItemEntity(level, getBlockPos().getX(),getBlockPos().getY(), getBlockPos().getZ(), input.getStackInSlot(i));
            level.addFreshEntity(entity);
        }
    }


    public static class Serial extends InputHatchTile {

        public static final int SLOTS = 5;

        public Serial(BlockPos pos, BlockState state) {
            this(ModTiles.INPUT_HATCH_SERIAL.get(), pos, state);
        } 

        public Serial(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
            super(blockEntityType, pos, state);
        }
        
                @Override
        protected int getSlotCount() {
            return SLOTS;
        }

        @Override
        protected int getSlotSize(int slot) {
            return 64;
        }

        @Override
        public int getSignalStrength() {
            float signal = 0;
            for(int i = 0; i < input.getSlots(); i++){
                ItemStack stack = input.getStackInSlot(i);
                signal += stack.getCount() / stack.getMaxStackSize();
            }
            return (int) Math.round(signal * 15f / SLOTS);
        }

    }
    

    public static class Parallel extends InputHatchTile {

        public static final int SLOTS = 1;

        public Parallel(BlockPos pos, BlockState state) {
            this(ModTiles.INPUT_HATCH_PARALLEL.get(), pos, state);
        } 

        public Parallel(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
            super(blockEntityType, pos, state);
        }
        
                @Override
        protected int getSlotCount() {
            return SLOTS;
        }

        @Override
        protected int getSlotSize(int slot) {
            return 1;
        }

        @Override
        public int getSignalStrength() {
            return input.getStackInSlot(0).isEmpty() ? 0 : 15;
        }

    }

    public static class SerialHeat extends Serial implements ITintableTile, IHeatTile{
        
        public SerialHeat(BlockPos pos, BlockState state){
            super(ModTiles.INPUT_HATCH_SERIAL_HEAT.get(), pos, state);
            this.isHeatConducting = true;
        }

        @Override
        public IHeatContainer getHeatContainer() {
            return heatHandler;
        }

    }

    public static class ParallelHeat extends Parallel implements ITintableTile, IHeatTile{
        
        public ParallelHeat(BlockPos pos, BlockState state){
            super(ModTiles.INPUT_HATCH_PARALLEL_HEAT.get(), pos, state);
            this.isHeatConducting = true;
        }

        @Override
        public IHeatContainer getHeatContainer() {
            return heatHandler;
        }

    }



}
