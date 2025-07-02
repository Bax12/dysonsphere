package de.bax.dysonsphere.tileentities;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.blocks.InputHatchBlock;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.fluid.FluidTankCustom;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import de.bax.dysonsphere.capabilities.inputHatch.IInputProvider.ProviderType;
import de.bax.dysonsphere.capabilities.inputHatch.InputProviderHandler;
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
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
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

        public boolean isItemValid(int slot, ItemStack stack) {
            return allowItem(slot, stack);
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

    public InputProviderHandler providerHandler = new InputProviderHandler(this, input){
        @Override
        public void onUplinkChange() {
            super.onUplinkChange();
            updateBlockState();
        };

        @Override
        public ProviderType getType() {
            return getProviderType();
        }
    };

    protected LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> input);
    protected LazyOptional<IHeatContainer> lazyHeat = LazyOptional.of(() -> heatHandler);
    // protected LazyOptional<IInputProvider> lazyProvider = LazyOptional.of(() -> providerHandler);

    protected boolean dirty = false;
    protected boolean isHeatConducting = false;
    protected int ticksElapsed = 0;

    public InputHatchTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract int getSignalStrength();

    protected abstract ProviderType getProviderType();

    protected abstract int getSlotCount();
    protected abstract int getSlotSize(int slot);
    protected void internalTick(){};

    protected boolean allowItem(int slot, ItemStack stack){
        return true;
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
        if(pTag.contains("provider")){
            providerHandler.deserializeNBT(pTag.getCompound("provider"));
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("invInput", input.serializeNBT());
        pTag.put("heat", heatHandler.serializeNBT());
        pTag.put("provider", providerHandler.serializeNBT());
    }

    @SuppressWarnings("null")
    public void tick(){
        if(!level.isClientSide){
            ticksElapsed++;
            if(ticksElapsed % 5 == 0){

                if(isHeatConducting){
                    heatHandler.splitShare();
                }
                internalTick();
                if(dirty){
                    sendSyncPackageToNearbyPlayers();
                    dirty = false;
                }
            }

            // if (ticksElapsed == 50){
            //     providerHandler.updateNeighbors(level, worldPosition);
            // }
        } else {
            // level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), getBlockState(), getBlockState(), 2, 0); 
        }
    }

    public void onNeighborChange(){
        heatHandler.updateNeighbors(level, worldPosition);
        providerHandler.updateNeighbors();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        heatHandler.updateNeighbors(level, worldPosition);
        // providerHandler.updateNeighbors(level, worldPosition);
        // providerHandler.onLoad();
    }

    public void onRemove(){
        dropContent();
        providerHandler.onRemove();
    }

    public void onPlacedInWorld(){
        providerHandler.onPlacedInWorld();
    }


    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(ForgeCapabilities.ITEM_HANDLER)){
            return lazyInv.cast();
        }
        if(isHeatConducting && cap.equals(DSCapabilities.HEAT)){
            return lazyHeat.cast();
        }
        if(cap.equals(DSCapabilities.INPUT_PROVIDER)){
            return providerHandler.lazyProvider.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyInv.invalidate();
        lazyHeat.invalidate();
        providerHandler.lazyProvider.invalidate();
    }
    
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
            ItemStack stack = input.getStackInSlot(i);
            if(!stack.isEmpty()){
                ItemEntity entity = new ItemEntity(level, getBlockPos().getX(),getBlockPos().getY(), getBlockPos().getZ(), stack);
                level.addFreshEntity(entity);
            }
        }
    }

    protected void updateBlockState(){
        boolean changed = false;
        if(level != null){
            BlockState state = level.getBlockState(worldPosition);
            if((state.getBlock() instanceof InputHatchBlock)) {
                if(state.getValue(InputHatchBlock.ATTACHED)){
                    if(providerHandler.getAcceptorUplinkDirection() == null){
                        state = state.setValue(InputHatchBlock.ATTACHED, false);
                        changed = true;
                    } else {
                        if(!state.getValue(InputHatchBlock.FACING).equals(providerHandler.getAcceptorUplinkDirection())){
                            state = state.setValue(InputHatchBlock.FACING, providerHandler.getAcceptorUplinkDirection());
                            changed = true;
                        }
                    }
                } else if(providerHandler.getAcceptorUplinkDirection() != null){
                    state = state.setValue(InputHatchBlock.ATTACHED, true);
                    if(!state.getValue(InputHatchBlock.FACING).equals(providerHandler.getAcceptorUplinkDirection())){
                        state = state.setValue(InputHatchBlock.FACING, providerHandler.getAcceptorUplinkDirection());
                    }
                    changed = true;
                }
                if(changed && !level.isClientSide()){
                    level.setBlock(worldPosition, state, 67);
                }    
            }
        }
    }

    //the following section could probably be more generic...
    public static class Serial extends InputHatchTile {

        public static final int SLOTS = 5;

        public Serial(BlockPos pos, BlockState state) {
            this(ModTiles.INPUT_HATCH_SERIAL.get(), pos, state);
        } 

        public Serial(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
            super(blockEntityType, pos, state);
        }

        @Override
        protected ProviderType getProviderType() {
            return ProviderType.SERIAL;
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
        protected ProviderType getProviderType() {
            return ProviderType.PARALLEL;
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

    public static class Proxy extends InputHatchTile {
        
        public Proxy(BlockPos pos, BlockState state){
            this(ModTiles.INPUT_HATCH_PROXY.get(), pos, state);
        }

        public Proxy(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
            super(blockEntityType, pos, state);
        }

        @Override
        protected int getSlotCount() {
            return 0;
        }

        @Override
        protected int getSlotSize(int slot) {
            return 0;
        }

        @Override
        public int getSignalStrength() {
            return 0;
        }

        @Override
        protected ProviderType getProviderType() {
            return ProviderType.PROXY;
        }

        //no item handling here. Call super for possible heat handling though.
        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if(cap.equals(ForgeCapabilities.ITEM_HANDLER)){
                return LazyOptional.empty();
            }
            return super.getCapability(cap, side);
        }

    }

    public static class Energy extends InputHatchTile{

        public static final int SLOTS = 1;
        public static int energyCapacity = 150000; //todo: add config

        public EnergyStorage energyStorage = new EnergyStorage(energyCapacity){
            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                if(!simulate){
                    setChanged();
                }
                return super.extractEnergy(maxExtract, simulate);
            }

            public int receiveEnergy(int maxReceive, boolean simulate) {
                if(!simulate){
                    setChanged();
                }
                return super.receiveEnergy(maxReceive, simulate);
            };
        };

        public LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
        
        public Energy(BlockPos pos, BlockState state){
            this(ModTiles.INPUT_HATCH_ENERGY.get(), pos, state);
        }

        public Energy(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
            super(blockEntityType, pos, state);

        }

        @Override
        public int getSignalStrength() {
            return energyStorage.getEnergyStored() * 15 / energyStorage.getMaxEnergyStored(); //multiply to target scale first to avoid issues with int division
        }

        @Override
        protected ProviderType getProviderType() {
            return ProviderType.ENERGY;
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
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if(cap.equals(ForgeCapabilities.ENERGY)){
                return lazyEnergyStorage.cast();
            }
            return super.getCapability(cap, side);
        }

        @Override
        public void invalidateCaps() {
            super.invalidateCaps();
            lazyEnergyStorage.invalidate();
        }

        @Override
        protected void saveAdditional(@Nonnull CompoundTag pTag) {
            super.saveAdditional(pTag);
            pTag.put("Energy", energyStorage.serializeNBT());
        }

        @Override
        public void load(@Nonnull CompoundTag pTag) {
            super.load(pTag);
            if(pTag.contains("Energy")){
                energyStorage.deserializeNBT(pTag.get("Energy"));
            } 
        }

        @Override
        protected void internalTick() {
            if(energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()){
                input.getStackInSlot(0).getCapability(ForgeCapabilities.ENERGY).ifPresent((itemEnergy) -> {
                    if(energyStorage.receiveEnergy(itemEnergy.extractEnergy(Integer.MAX_VALUE, true), true) > 0){
                        energyStorage.receiveEnergy(itemEnergy.extractEnergy(Integer.MAX_VALUE, false), false);
                    }
                });
            }
        }

        @Override
        protected boolean allowItem(int slot, ItemStack stack) {
            return stack.getCapability(ForgeCapabilities.ENERGY).map((energy) -> {
                return energy.canExtract();
            }).orElse(false);
        }
    }

    public static class Fluid extends InputHatchTile {

        public static final int SLOTS = 2;
        public static final int SLOT_INPUT = 0;
        public static final int SLOT_OUTPUT = 1;
        public static int fluidCapacity = 10000; //todo: add config

        public FluidTankCustom fluidStorage = new FluidTankCustom(fluidCapacity){
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                setChanged();
            }
        };

        public LazyOptional<IFluidHandler> lazyFluidStorage = LazyOptional.of(() -> fluidStorage);

        public Fluid(BlockPos pos, BlockState state) {
            this(ModTiles.INPUT_HATCH_FLUID.get(), pos, state);
        }

        public Fluid(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

        @Override
        public int getSignalStrength() {
            return fluidStorage.getFluidAmount() * 15 / fluidStorage.getCapacity();
        }

        @Override
        protected ProviderType getProviderType() {
            return ProviderType.FLUID;
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
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
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

        @Override
        protected void saveAdditional(@Nonnull CompoundTag pTag) {
            super.saveAdditional(pTag);
            pTag.put("Fluid", fluidStorage.writeToNBT(new CompoundTag()));
        }

        @Override
        public void load(@Nonnull CompoundTag pTag) {
            super.load(pTag);
            if(pTag.contains("Fluid")){
                fluidStorage.readFromNBT(pTag.getCompound("Fluid"));
            }
        }

        @Override
        protected void internalTick() {
            if(fluidStorage.getFluidAmount() < fluidStorage.getCapacity()){
                input.getStackInSlot(SLOT_INPUT).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent((item) -> {
                    if(fluidStorage.isEmpty()){
                        if(fluidStorage.fill(item.drain(fluidStorage.getCapacity(), FluidAction.SIMULATE), FluidAction.SIMULATE) > 0){
                            fluidStorage.fill(item.drain(fluidStorage.getCapacity(), FluidAction.EXECUTE), FluidAction.EXECUTE);
                        }
                    } else {
                        if(fluidStorage.fill(item.drain(new FluidStack(fluidStorage.getFluid(), fluidStorage.getCapacity()-fluidStorage.getFluidAmount()), FluidAction.SIMULATE), FluidAction.SIMULATE) > 0){
                            fluidStorage.fill(item.drain(new FluidStack(fluidStorage.getFluid(), fluidStorage.getCapacity()-fluidStorage.getFluidAmount()), FluidAction.EXECUTE), FluidAction.EXECUTE);
                        }
                    }
                });
            }
            if(fluidStorage.getFluidAmount() > 0){
                input.getStackInSlot(SLOT_OUTPUT).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent((item) -> {
                    if(item.fill(fluidStorage.drain(Integer.MAX_VALUE, FluidAction.SIMULATE), FluidAction.SIMULATE) > 0){
                        item.fill(fluidStorage.drain(Integer.MAX_VALUE, FluidAction.EXECUTE), FluidAction.EXECUTE);
                    }
                });
            }
        }

        @Override
        protected boolean allowItem(int slot, ItemStack stack) {
            return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map((fluid) -> {
                return (slot == SLOT_INPUT && !fluid.drain(Integer.MAX_VALUE, FluidAction.SIMULATE).isEmpty())
                        || (slot == SLOT_OUTPUT && (fluid.fill(fluidStorage.getFluid(), FluidAction.SIMULATE) > 0));
            }).orElse(false);
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

    public static class ProxyHeat extends Proxy implements ITintableTile, IHeatTile{

        public ProxyHeat(BlockPos pos, BlockState state){
            super(ModTiles.INPUT_HATCH_PROXY_HEAT.get(), pos, state);
            this.isHeatConducting = true;
        }

        @Override
        public IHeatContainer getHeatContainer() {
            return heatHandler;
        }

    }

    public static class EnergyHeat extends Energy implements ITintableTile, IHeatTile{

        public EnergyHeat(BlockPos pos, BlockState state){
            super(ModTiles.INPUT_HATCH_ENERGY_HEAT.get(), pos, state);
            this.isHeatConducting = true;
        }

        @Override
        public IHeatContainer getHeatContainer() {
            return heatHandler;
        }

    }

    public static class FluidHeat extends Fluid implements ITintableTile, IHeatTile{

        public FluidHeat(BlockPos pos, BlockState state) {
            super(ModTiles.INPUT_HATCH_FLUID_HEAT.get(), pos, state);
            this.isHeatConducting = true;
        }

        @Override
        public IHeatContainer getHeatContainer() {
            return heatHandler;
        }
        
    }


}
