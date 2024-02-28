package de.bax.dysonsphere.tileentities;

import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class HeatGeneratorTile extends BlockEntity {

    public static final double maxHeat = 1500;
    public static final double maxHeatTransfer = 5;

    protected HeatHandler heatHandler = new HeatHandler(300, maxHeat){
        public double getMaxSplitShareAmount() {
            return maxHeatTransfer;
        };

        public double extractHeat(double maxExtract, boolean simulate) {
            maxExtract = Math.min(maxExtract, maxHeatTransfer);
            return super.extractHeat(maxExtract, simulate);
        };

        public double receiveHeat(double maxReceive, boolean simulate) {
            maxReceive = Math.min(maxReceive, maxHeatTransfer);
            return super.receiveHeat(maxReceive, simulate);
        };
    };
    protected EnergyStorage energyStorage = new EnergyStorage(25000){
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if(!simulate) setChanged();
            return super.receiveEnergy(maxReceive, simulate);
        };

        public int extractEnergy(int maxExtract, boolean simulate) {
            if(!simulate) setChanged();
            return super.receiveEnergy(maxExtract, simulate);
        };
    };

    protected LazyOptional<IHeatContainer> lazyHeatContainer = LazyOptional.of(() -> heatHandler);
    protected LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);

    protected int ticksElapsed = 0;
    protected double lastHeat = 0;

    public HeatGeneratorTile(BlockPos pos, BlockState state) {
        super(ModTiles.HEAT_GENERATOR.get(), pos, state);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyHeatContainer.invalidate();
        lazyEnergyStorage.invalidate();
    }

    public void tick(){
        if(!level.isClientSide){
            if(ticksElapsed++ % 5 == 0){
                heatHandler.splitShare();
                if(lastHeat != heatHandler.getHeatStored()){
                    this.setChanged();
                    lastHeat = heatHandler.getHeatStored();
                }
            }
            //TODo: generate energy on heat difference and energy transfer
        }
    }

        @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("Energy")){
            energyStorage.deserializeNBT(tag.get("Energy"));
        } 
        if(tag.contains("Heat")) {
            heatHandler.deserializeNBT(tag.getCompound("heat"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("Heat", heatHandler.serializeNBT());

    }

    public void onNeighborChange() {
        heatHandler.updateNeighbors(level, getBlockPos());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        heatHandler.updateNeighbors(level, getBlockPos());
    }

    
}
