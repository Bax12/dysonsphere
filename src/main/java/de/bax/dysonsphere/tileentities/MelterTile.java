package de.bax.dysonsphere.tileentities;

import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.fluid.FluidTankCustom;
import de.bax.dysonsphere.capabilities.heat.AirHeatHandler;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import de.bax.dysonsphere.color.ModColors.ITintableTile;
import de.bax.dysonsphere.recipes.MelterRecipe;
import de.bax.dysonsphere.recipes.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class MelterTile extends BaseTile implements ITintableTile, IHeatTile {

    public static final int SLOTS = 2;
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;//fluid container to fill with output slot

    public static double MAX_HEAT = 1700;

    public static int fluidCapacity = 10000;

    public static List<Ingredient> inputs;


    public ItemStackHandler input = new ItemStackHandler(SLOTS){
        protected void onContentsChanged(int slot) {
            setChanged();
        };

        public int getSlotLimit(int slot) {
            return 64;
        };

        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == SLOT_INPUT && inputs.stream().anyMatch(i -> i.test(stack)) ||
                    slot == SLOT_OUTPUT && stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
        };

        protected int getStackLimit(int slot, ItemStack stack) {
            return slot == SLOT_OUTPUT ? 1 : super.getStackLimit(slot, stack);
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

    public FluidTankCustom fluidStorage = new FluidTankCustom(fluidCapacity){
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            setChanged();
        };

        @Override
        public boolean canFill() {
            return false;
        }
    };



    protected LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> input);
    protected LazyOptional<IHeatContainer> lazyHeat = LazyOptional.of(() -> heatHandler);
    public LazyOptional<IFluidHandler> lazyFluidStorage = LazyOptional.of(() -> fluidStorage);

    protected boolean dirty;
    protected int ticksElapsed = 0;
    protected double lastHeat = 0;
    protected MelterRecipe curRecipe = null;
    protected int curWorkTime = 0;
    protected int maxWorkTime = 0;

    protected LazyOptional<IFluidHandler>[] fluidNeighbors = new LazyOptional[6];

    public MelterTile(BlockEntityType<?> type, BlockPos pos, BlockState state){
        super(type, pos, state);
    }

    public MelterTile(BlockPos pos, BlockState state) {
        super(ModTiles.HEAT_MELTER.get(), pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(ForgeCapabilities.ITEM_HANDLER)){
            return lazyInv.cast();
        }
        if(cap.equals(DSCapabilities.HEAT)){
            return lazyHeat.cast();
        }
        if(cap.equals(ForgeCapabilities.FLUID_HANDLER)){
            return lazyFluidStorage.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidStorage.invalidate();
    }

    public void tick(){
        if(!level.isClientSide){
            if(++ticksElapsed % 5 == 0){

                internalTick();
                heatHandler.splitShare();
                if(dirty){
                    sendSyncPackageToNearbyPlayers();
                    dirty = false;
                }
            }
        } else {
            if(lastHeat != heatHandler.getHeatStored()){
                level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), getBlockState(), getBlockState(), 2, 0); 
                lastHeat = heatHandler.getHeatStored();
            }
            
        }
    }

    protected void internalTick(){
        if(curRecipe == null || !curRecipe.matches(input.getStackInSlot(0))){
            setRecipe();
            curWorkTime = 0;
        }
        if(curRecipe != null){
            while(heatHandler.getHeatStored() > curRecipe.minHeat() && curWorkTime < curRecipe.workTime()){
                heatHandler.extractHeat(curRecipe.heatAbsorbed(), false);
                curWorkTime++;
            }

            if(curWorkTime >= curRecipe.workTime()){
                int filled = fluidStorage.fillInternal(curRecipe.output(), FluidAction.SIMULATE);
                if(filled == curRecipe.output().getAmount()){
                    input.extractItem(0, 1, false);
                    fluidStorage.fillInternal(curRecipe.output(), FluidAction.EXECUTE);
                    curWorkTime = 0;
                }
            }
        }

        
        if(!input.getStackInSlot(SLOT_OUTPUT).isEmpty() && !fluidStorage.isEmpty()){
            input.getStackInSlot(SLOT_OUTPUT).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent((handler) -> {
                FluidUtil.tryFluidTransfer(handler, fluidStorage, fluidStorage.getCapacity(), true);
                input.setStackInSlot(SLOT_OUTPUT, handler.getContainer());
            });
        }


        //neighbor tile interaction
        for (LazyOptional<IFluidHandler> neighbor : fluidNeighbors){
            if(neighbor == null) continue;
            neighbor.ifPresent((neiFluid) -> {
                int filled = fluidStorage.fillInternal(neiFluid.drain(new FluidStack(Fluids.WATER, fluidStorage.getSpace()), FluidAction.SIMULATE), FluidAction.SIMULATE);
                if(filled > 0){
                    fluidStorage.fillInternal(neiFluid.drain(new FluidStack(Fluids.WATER, filled), FluidAction.EXECUTE), FluidAction.EXECUTE);
                }
            });
        }
    }

    protected void setRecipe(){
        curRecipe = level.getRecipeManager().getAllRecipesFor(ModRecipes.MELTER_TYPE.get()).stream().filter((recipe) -> recipe.matches(input.getStackInSlot(0))).findFirst().orElse(null);
        if(curRecipe != null){
            maxWorkTime = curRecipe.workTime();
        } else {
            maxWorkTime = 0;
        }
    }

    public int getProgressScaled(int scale){
        if(maxWorkTime == 0) return 0;
        return scale * curWorkTime / maxWorkTime;
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("invInput")){
            input.deserializeNBT(pTag.getCompound("invInput"));
        }
        if(pTag.contains("heat")){
            heatHandler.deserializeNBT(pTag.getCompound("heat"));
        }
        if(pTag.contains("fluid")){
            fluidStorage.readFromNBT(pTag.getCompound("fluid"));
        }
        if(pTag.contains("workTime")){
            curWorkTime = pTag.getInt("workTime");
        }
        if(pTag.contains("maxWorkTime")){
            maxWorkTime = pTag.getInt("maxWorkTime");
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("invInput", input.serializeNBT());
        pTag.put("heat", heatHandler.serializeNBT());
        pTag.put("fluid", fluidStorage.writeToNBT(new CompoundTag()));
        pTag.putInt("workTime", curWorkTime);
        pTag.putInt("maxWorkTime", maxWorkTime);
    }

    public void onRemove(){
        for(int i = 0; i < input.getSlots(); i++){
            ItemStack stack = input.getStackInSlot(i);
            if(!stack.isEmpty()){
                ItemEntity entity = new ItemEntity(level, getBlockPos().getX(),getBlockPos().getY(), getBlockPos().getZ(), stack);
                level.addFreshEntity(entity);
            }
        }
    }

    public void onNeighborChange(){
        heatHandler.updateNeighbors(level, worldPosition);
        for(Direction dir : Direction.values()){
            BlockPos neiPos = worldPosition.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neiPos);
            if(neighbor != null){
                LazyOptional<IFluidHandler> neighborHandler = neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite());
                if (neighborHandler.isPresent()){
                    fluidNeighbors[dir.ordinal()] = neighborHandler;
                }
            } else {
                fluidNeighbors[dir.ordinal()] = LazyOptional.empty();
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(inputs == null){
            inputs = level.getRecipeManager().getAllRecipesFor(ModRecipes.MELTER_TYPE.get()).stream().map(r -> r.input()).toList();
        }
        onNeighborChange();
    }

    @Override
    public void setChanged(){
        dirty = true;
        super.setChanged();
    }

    @Override
    public IHeatContainer getHeatContainer() {
        return heatHandler;
    }

    @Override
    public int getTintColor(int tintIndex) {
        if (tintIndex == 0){
            int col = 0xFFFF0000;
            int offset = 255 - (int) Math.min(Math.max(heatHandler.getHeatStored() - HeatHandler.HEAT_AMBIENT, 0) / 5, 255);

            return col + offset + (offset << 8);
        }
        return 0xFFFFFFFF;
    }
    
    public static class Induction extends MelterTile {

        public static int energyUsage = 200;
        public static double energyToHeatRate = 10;
        public static int energyCapacity = 50000;

        public EnergyStorage energyStorage = new EnergyStorage(energyCapacity) {
            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                if(!simulate){
                    setChanged();
                }
                return super.extractEnergy(maxExtract, simulate);
            };

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                if(!simulate){
                    setChanged();
                }
                return super.receiveEnergy(maxReceive, simulate);
            };

        };

        public LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
        
        public Induction(BlockPos pos, BlockState state){
            super(ModTiles.INDUCTION_MELTER.get(), pos, state);
        }

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if(cap.equals(ForgeCapabilities.ENERGY)){
                return lazyEnergyStorage.cast();
            }
            return super.getCapability(cap, side);
        }

        @Override
        protected void internalTick() {
            if(curRecipe != null && curRecipe.minHeat() < heatHandler.getHeatStored() || level.hasNeighborSignal(worldPosition) && heatHandler.getHeatStored() < MAX_HEAT){
                heatHandler.receiveHeat(energyToHeatRate * energyStorage.extractEnergy(energyUsage, false), false);
            }
            super.internalTick();
        }

        @Override
        public void invalidateCaps() {
            lazyEnergyStorage.invalidate();    
            super.invalidateCaps();
        }

        @Override
        public void load(@Nonnull CompoundTag pTag) {
            super.load(pTag);
            if(pTag.contains("Energy")){
                energyStorage.deserializeNBT(pTag.get("Energy"));
            }
        }

        @Override
        protected void saveAdditional(@Nonnull CompoundTag pTag) {
            super.saveAdditional(pTag);
            pTag.put("Energy", energyStorage.serializeNBT());
        }

    }


}
