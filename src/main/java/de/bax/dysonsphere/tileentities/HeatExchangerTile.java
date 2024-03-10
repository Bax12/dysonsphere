package de.bax.dysonsphere.tileentities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.fluid.FluidHandlerMap;
import de.bax.dysonsphere.capabilities.fluid.FluidTankCustom;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import de.bax.dysonsphere.fluids.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class HeatExchangerTile extends BaseTile {

    public static final double maxHeat = 1500;
    public static final double minHeat = 450;
    public static final int baseProduce = 5;
    public static final int bonusProduce = 1;
    public static final double bonusHeat = 50;
    public static final int fluidCapacity = 4000;
    public static final float heatConsumtion = 5;

    protected HeatHandler heatHandler = new HeatHandler(300, maxHeat);
    protected FluidTankCustom inputTank = new FluidTankCustom(fluidCapacity){
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.isFluidEqual(new FluidStack(ModFluids.STEAM.get(), 5));
        }
        protected void onContentsChanged() {
            setChanged();
        };
        @Override
        public boolean canDrain() {
            return false;
        }
        
    };
    protected FluidTankCustom outputTank = new FluidTankCustom(fluidCapacity){
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.isFluidEqual(new FluidStack(Fluids.WATER, 5));
        }
        protected void onContentsChanged() {
            setChanged();
        };
        public boolean canFill() {
            return false;
        };
    };

    protected FluidHandlerMap handlerMap = new FluidHandlerMap();   

    protected LazyOptional<IHeatContainer> lazyHeatContainer = LazyOptional.of(() -> heatHandler);
    protected LazyOptional<IFluidHandler> lazyFluidHandlerMap = LazyOptional.of(() -> handlerMap);

    protected double lastHeat = 0;
    protected int ticksElapsed = 0;
    protected LazyOptional<IFluidHandler>[] fluidNeighbors = new LazyOptional[6];


    public HeatExchangerTile(BlockPos pos, BlockState state) {
        super(ModTiles.HEAT_EXCHANGER.get(), pos, state);
        handlerMap.addFluidHandler(Fluids.WATER, inputTank);
        handlerMap.addFluidHandler(ModFluids.STEAM.get(), outputTank);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.HEAT)){
            return lazyHeatContainer.cast();
        } else if (cap.equals(ForgeCapabilities.FLUID_HANDLER)){
            return lazyFluidHandlerMap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyHeatContainer.invalidate();
        lazyFluidHandlerMap.invalidate();
    }

    public void tick(){
        if(!level.isClientSide){
            if(ticksElapsed++ % 5 == 0){
                if(lastHeat != heatHandler.getHeatStored()){
                    this.setChanged();
                    lastHeat = heatHandler.getHeatStored();
                }
                pushPullFluids();
                generateSteam();
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        heatHandler.deserializeNBT(tag.getCompound("Heat"));
        inputTank.readFromNBT(tag.getCompound("inputTank"));
        outputTank.readFromNBT(tag.getCompound("outputTank"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Heat",heatHandler.serializeNBT());
        CompoundTag nbt = new CompoundTag();
        inputTank.writeToNBT(nbt);
        tag.put("inputTank", nbt);
        nbt = new CompoundTag();
        outputTank.writeToNBT(nbt);
        tag.put("outputTank", nbt);
    }

    public void onNeighborChange() {
        updateNeighbors();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateNeighbors();
    }
    
    protected void updateNeighbors(){
        heatHandler.updateNeighbors(level, getBlockPos());
        for(Direction dir : Direction.values()){
            BlockEntity neighbor = level.getBlockEntity(getBlockPos().relative(dir));
            if(neighbor != null){
                LazyOptional<IFluidHandler> neighborHandler = neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite());
                if (neighborHandler.isPresent()){
                    fluidNeighbors[dir.ordinal()] = neighborHandler;
                }
            }
        }
    }

    protected void pushPullFluids(){
        for (LazyOptional<IFluidHandler> neighbor : fluidNeighbors){
            if(neighbor != null && neighbor.isPresent()){
                neighbor.ifPresent((neiFluid) -> {
                    int filled = inputTank.fillInternal(neiFluid.drain(new FluidStack(Fluids.WATER, inputTank.getSpace()), FluidAction.SIMULATE), FluidAction.SIMULATE);
                    if(filled > 0){
                        inputTank.fillInternal(neiFluid.drain(new FluidStack(Fluids.WATER, filled), FluidAction.EXECUTE), FluidAction.EXECUTE);
                    }
                    int drained = neiFluid.fill(outputTank.drainInternal(outputTank.getCapacity(), FluidAction.SIMULATE), FluidAction.SIMULATE);
                    if(drained > 0){
                        neiFluid.fill(outputTank.drainInternal(outputTank.getCapacity(), FluidAction.EXECUTE), FluidAction.EXECUTE);
                    }
                });
            }
        }
    }

    protected void generateSteam() {
        double curHeat = heatHandler.getHeatStored();
        if(curHeat >= minHeat){
            curHeat -= minHeat;
            int produce = (int) (baseProduce + (bonusProduce * curHeat / bonusHeat));
            produce = outputTank.fillInternal(new FluidStack(ModFluids.STEAM.get(), inputTank.drainInternal(produce, FluidAction.SIMULATE).getAmount()), FluidAction.SIMULATE);
            if(produce > 0){
                outputTank.fillInternal(new FluidStack(ModFluids.STEAM.get(), produce), FluidAction.EXECUTE);
                inputTank.drainInternal(produce, FluidAction.EXECUTE);
                heatHandler.extractHeat(produce * heatConsumtion, false);
            }
        }
    }

}
