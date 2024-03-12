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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemStackHandler;

public class HeatExchangerTile extends BaseTile {

    public static final double maxHeat = 1700;
    public static final double minHeat = 450;
    public static final int baseProduce = 5;
    public static final int bonusProduce = 1;
    public static final double bonusHeat = 50;
    public static final int fluidCapacity = 4000;
    public static final float heatConsumtion = 2.5f;

    public static final int slotInput = 0;
    public static final int slotOutput = 1;

    public HeatHandler heatHandler = new HeatHandler(300, maxHeat);
    public FluidTankCustom inputTank = new FluidTankCustom(fluidCapacity){
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.isFluidEqual(new FluidStack(Fluids.WATER, 5));
        }
        protected void onContentsChanged() {
            shouldUpdate = true;
        };
        @Override
        public boolean canDrain() {
            return false;
        }
        
    };
    public FluidTankCustom outputTank = new FluidTankCustom(fluidCapacity){
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.isFluidEqual(new FluidStack(ModFluids.STEAM.get(), 5));
        }
        protected void onContentsChanged() {
            shouldUpdate = true;
        };
        public boolean canFill() {
            return false;
        };
    };

    public ItemStackHandler inventory = new ItemStackHandler(2){
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        };
        public int getSlotLimit(int slot) {
            return 1;
        };
        public boolean isItemValid(int slot, net.minecraft.world.item.ItemStack stack) {
            if(stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()){
                if(slot == slotInput){
                    return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map((handler) -> {
                        return handler.drain(new FluidStack(Fluids.WATER, Integer.MAX_VALUE), FluidAction.SIMULATE).getAmount() > 0;
                    }).get();
                } else if (slot == slotOutput){
                    ItemStack copyStack = stack.copyWithCount(1);
                    return copyStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map((handler) -> {
                        return handler.fill(new FluidStack(ModFluids.STEAM.get(), Integer.MAX_VALUE), FluidAction.SIMULATE) > 0;
                    }).get();
                }
            }
            return false;
        };

    };

    protected FluidHandlerMap handlerMap = new FluidHandlerMap();   

    protected LazyOptional<IHeatContainer> lazyHeatContainer = LazyOptional.of(() -> heatHandler);
    protected LazyOptional<IFluidHandler> lazyFluidHandlerMap = LazyOptional.of(() -> handlerMap);

    protected double lastHeat = 0;
    protected int ticksElapsed = 0;
    protected LazyOptional<IFluidHandler>[] fluidNeighbors = new LazyOptional[6];
    protected boolean shouldUpdate;


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
                pushPullFluids();
                generateSteam();
                heatHandler.splitShare();
                if(lastHeat != heatHandler.getHeatStored()){
                    shouldUpdate = true;
                    lastHeat = heatHandler.getHeatStored();
                }
                if(shouldUpdate){
                    this.setChanged();
                    sendSyncPackageToNearbyPlayers();
                    shouldUpdate = false;
                }
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        heatHandler.deserializeNBT(tag.getCompound("Heat"));
        inputTank.readFromNBT(tag.getCompound("inputTank"));
        outputTank.readFromNBT(tag.getCompound("outputTank"));
        inventory.deserializeNBT(tag.getCompound("inventory"));
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
        tag.put("inventory", inventory.serializeNBT());
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
        //internal fluid item interaction
        if(!inventory.getStackInSlot(slotInput).isEmpty()){
            // LazyOptional<IFluidHandlerItem> itemFluidhandler = FluidUtil.getFluidHandler(inventory.getStackInSlot(slotInput));
            // itemFluidhandler.ifPresent((handler) -> {
            //     FluidUtil.tryFluidTransfer(inputTank, handler, inputTank.getCapacity(), true);
            //     inventory.setStackInSlot(slotInput, handler.getContainer());
            // });
            LazyOptional<IFluidHandlerItem> lazyHandler = inventory.getStackInSlot(slotInput).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
            lazyHandler.ifPresent((handler) -> {
                FluidStack fluid = handler.drain(new FluidStack(Fluids.WATER, Integer.MAX_VALUE), FluidAction.SIMULATE);
                if(fluid.getAmount() > 0){
                    int fillAmount = inputTank.fillInternal(fluid, FluidAction.SIMULATE);
                    if(fillAmount > 0){
                        fluid.setAmount(fillAmount);
                        handler.drain(fluid, FluidAction.EXECUTE);
                        inputTank.fillInternal(fluid, FluidAction.EXECUTE);
                        inventory.setStackInSlot(slotInput, handler.getContainer());
                    }
                }
            });
        }
        if(!inventory.getStackInSlot(slotOutput).isEmpty()){
            LazyOptional<IFluidHandlerItem> itemFluidhandler = FluidUtil.getFluidHandler(inventory.getStackInSlot(slotOutput));
            itemFluidhandler.ifPresent((handler) -> {
                FluidUtil.tryFluidTransfer(handler, outputTank, outputTank.getCapacity(), true);
                inventory.setStackInSlot(slotOutput, handler.getContainer());
            });
        }
        //neighbor tile interaction
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
            int produce = (int) (baseProduce + (bonusProduce * curHeat / bonusHeat) * 5);//executed once every 5 ticks
            produce = outputTank.fillInternal(new FluidStack(ModFluids.STEAM.get(), inputTank.drainInternal(produce, FluidAction.SIMULATE).getAmount() * 10), FluidAction.SIMULATE);//*10 for steam expansion */
            if(produce > 0){
                outputTank.fillInternal(new FluidStack(ModFluids.STEAM.get(), produce), FluidAction.EXECUTE);
                inputTank.drainInternal(produce / 10, FluidAction.EXECUTE); //accounting expansion
                heatHandler.extractHeat(produce * heatConsumtion / 10, false); //accounting expansion
            }
        }
    }

}
