package de.bax.dysonsphere.tileentities;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.energy.ExternalEnergyWrapper;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import de.bax.dysonsphere.color.ModColors.ITintableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class HeatConverterTile extends BaseTile implements ITintableTile, IHeatTile {

    public static double MAX_HEAT = 1700;
    public static int ENERGY_CAPACITY = 20000;
    public static float CONVERSION_RATE = 0.5f;
    public static float BASE_WORK_RATE = 2f;
    public static double BASE_HEAT = 600;

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

        public double getThermalConductivity() {
            return 0.5d;
        };
    };

    public EnergyStorage energyStorage = new EnergyStorage(ENERGY_CAPACITY){
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if(!simulate){
                setChanged();
            }
            return super.receiveEnergy(maxReceive, simulate);
        };
        public int extractEnergy(int maxExtract, boolean simulate) {
            if(!simulate){
                setChanged();
            }
            return super.extractEnergy(maxExtract, simulate);
        };
    };

    protected IEnergyStorage externalEnergy = new ExternalEnergyWrapper(energyStorage) {
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        };
        public boolean canReceive() {
            return false;
        };
    };

    protected LazyOptional<IHeatContainer> lazyHeatContainer = LazyOptional.of(() -> heatHandler);
    protected LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> externalEnergy);

    // protected float conversionRate = 0;
    protected float workRate = 0f;
    protected float floatingEnergy = 0f;

    protected LazyOptional<IEnergyStorage>[] energyNeighbors = new LazyOptional[6];

    protected boolean dirty = false;
    protected int ticksElapsed = 0;
    protected double lastHeat = 0d;

    public HeatConverterTile(BlockPos pos, BlockState state) {
        super(ModTiles.HEAT_CONVERTER.get(), pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.HEAT)){
            return lazyHeatContainer.cast();
        } else if (cap.equals(ForgeCapabilities.ENERGY)){
            return lazyEnergyStorage.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyHeatContainer.invalidate();
        lazyEnergyStorage.invalidate();
    }

    public void tick() {
        if(!level.isClientSide){
            ticksElapsed++;
            if(ticksElapsed % 5 == 0){
                heatHandler.splitShare();
                splitShareEnergy();
                if(!this.level.hasNeighborSignal(worldPosition)){
                // calculateConversionRate();
                calculateWorkRate();
                work();
            }
            }
            
            

            if(dirty){
                dirty = false;
                sendSyncPackageToNearbyPlayers();
            }
        } else {
            if(lastHeat != heatHandler.getHeatStored()){
                level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), getBlockState(), getBlockState(), 2, 0);
                lastHeat = heatHandler.getHeatStored();
            }
        }
    }

    // protected void calculateConversionRate(){
    //     float heatFactor =(float) (Math.max(heatHandler.getHeatStored() - HeatHandler.HEAT_AMBIENT, 0) / heatHandler.getMaxHeatStored());
    //     if(heatFactor > 0){
    //         heatFactor += 1f;
    //     }
    //     float rate = BASE_CONVERSION_RATE * heatFactor * heatFactor;

    //     if(Math.abs(this.conversionRate - rate) > 0.01){
    //         this.dirty = true;
    //         this.conversionRate = rate;
    //     }
    // }

    protected void calculateWorkRate(){
        float energyFactor = Math.max(energyStorage.getEnergyStored() - 1000f, 0f) / (energyStorage.getMaxEnergyStored() - 1000f);
        int heatFactor = (int) (heatHandler.getHeatStored() - BASE_HEAT) / 20;
        float rate = 0;
        if(heatFactor > 5){ //skip pow if rate is so high, lack of internal heat becomes the limit.
            rate = BASE_WORK_RATE * (1f - energyFactor) * 200;
        } else if(heatFactor > 0){
            rate = BASE_WORK_RATE * (1f - energyFactor) * ((float) Math.pow(Math.E, heatFactor)-1);
        }
        if(Math.abs(this.workRate - rate) > 0.01){
            this.dirty = true;
            this.workRate = rate;
        }
    }

    protected void work(){
        if(workRate > 0){
            // double heat = heatHandler.extractHeat(workRate, true);
            // if(heat > 0){
            //     int energy = energyStorage.receiveEnergy((int) (heat * conversionRate), true);
            //     if(energy > 0){
            floatingEnergy += (heatHandler.extractHeat(workRate, false) * CONVERSION_RATE);
            if(floatingEnergy > 1){
                energyStorage.receiveEnergy((int) floatingEnergy, false);
                floatingEnergy -= (int) floatingEnergy;
            }
            
        //         }
        //     }
        }
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("Energy")){
            energyStorage.deserializeNBT(pTag.get("Energy"));
        } 
        if(pTag.contains("Heat")) {
            heatHandler.deserializeNBT(pTag.getCompound("Heat"));
        }
        if(pTag.contains("workRate")){
            workRate = pTag.getFloat("workRate");
        }
        // if(pTag.contains("conversionRate")){
        //     conversionRate = pTag.getFloat("conversionRate");
        // }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Energy", energyStorage.serializeNBT());
        pTag.put("Heat", heatHandler.serializeNBT());
        pTag.putFloat("workRate", workRate);
        // pTag.putFloat("conversionRate", conversionRate);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.dirty = true;
    }

    public void onNeighborChange(){
        heatHandler.updateNeighbors(level, worldPosition);
        for(Direction dir : Direction.values()){
            BlockEntity neighbor = level.getBlockEntity(getBlockPos().relative(dir));
            if(neighbor != null){
                LazyOptional<IEnergyStorage> neighborHandler = neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite());
                if (neighborHandler.isPresent()){
                    energyNeighbors[dir.ordinal()] = neighborHandler;
                }
            }
        }
    }

    protected void splitShareEnergy(){
        for (LazyOptional<IEnergyStorage> neighbor : energyNeighbors){
            if(neighbor != null && neighbor.isPresent()){
                neighbor.ifPresent((neiEnergy) -> {
                    int maxTransfer = neiEnergy.receiveEnergy(energyStorage.getEnergyStored(), true);
                    neiEnergy.receiveEnergy(energyStorage.extractEnergy(maxTransfer, false), false);
                });
            }
        }
    }

    public double getCurrentHeat(){
        return heatHandler.getHeatStored();
    }

    public float getCurrentProductionRate(){
        return (float) (workRate / CONVERSION_RATE * heatHandler.getThermalConductivity() * 0.2f); // *0.2 as we work every 5 ticks.
    }

    //corners = 0, center = 1
    @Override
    public int getTintColor(int tintIndex) {
        int col, offset;
        switch (tintIndex) {
            case 0:
                col = 0xFFFF0000;
                offset = 255 - (int) Math.min(Math.max(this.getCurrentHeat() - HeatHandler.HEAT_AMBIENT, 0) / 5, 255);
                return col + offset + (offset << 8);
        
            case 1:
                double scale = Math.min(Math.max(this.workRate / 200, 0), 1);
                int rb = (int) (0xDA * scale + 0x25);
                int g = (int) (0xFF - 0xFF * scale);
                return rb | g << 8 | rb << 16;
        }
        return 0;
    }

    @Override
    public IHeatContainer getHeatContainer() {
        return heatHandler;
    }
    
}
