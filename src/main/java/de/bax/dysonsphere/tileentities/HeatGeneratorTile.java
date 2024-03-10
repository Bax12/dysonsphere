package de.bax.dysonsphere.tileentities;

import java.util.Random;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
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

public class HeatGeneratorTile extends BaseTile {

    public static final double maxHeat = 1700;
    public static final double maxHeatTransfer = 25;
    public static final int energyCapacity = 25000;

    public static final double minHeatDifference = 25;
    public static final int energyGenerated = 1;

    public HeatHandler heatHandler = new HeatHandler(300, maxHeat){
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
    public EnergyStorage energyStorage = new EnergyStorage(energyCapacity);

    protected LazyOptional<IHeatContainer> lazyHeatContainer = LazyOptional.of(() -> heatHandler);
    protected LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);

    protected int ticksElapsed = 0;
    protected double lastHeat = 0;
    protected int lastEnergy = 0;

    protected double heatDifference = 0.0d;
    protected char lastAxis;

    protected LazyOptional<IEnergyStorage>[] energyNeighbors = new LazyOptional[6];

    public HeatGeneratorTile(BlockPos pos, BlockState state) {
        super(ModTiles.HEAT_GENERATOR.get(), pos, state);
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

    public void tick(){
        if(!level.isClientSide){
            if(ticksElapsed++ % 5 == 0){
                heatHandler.splitShare();
                boolean shouldSync = false;
                if(lastHeat != heatHandler.getHeatStored()){
                    lastHeat = heatHandler.getHeatStored();
                    shouldSync = true;
                }
                generateEnergy();
                splitShareEnergy();
                if(lastEnergy != energyStorage.getEnergyStored()){
                    lastEnergy = energyStorage.getMaxEnergyStored();
                    shouldSync = true;
                }
                if(shouldSync){
                    this.setChanged();
                    sendSyncPackageToNearbyPlayers();
                }
            }
            
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("Energy")){
            energyStorage.deserializeNBT(tag.get("Energy"));
        } 
        if(tag.contains("Heat")) {
            heatHandler.deserializeNBT(tag.getCompound("Heat"));
        }
        heatDifference = tag.getInt("HeatDifference");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("Heat", heatHandler.serializeNBT());
        tag.putInt("HeatDifference", getHeatDifference());
    }

    public void onNeighborChange() {
        updateNeighbors();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateNeighbors();
        ticksElapsed = this.level.getRandom().nextInt(4); //set ticksElapsed to 0-4 on load to not calculate all generators on the same server tick. Probably useless
    }

    protected void updateNeighbors(){
        heatHandler.updateNeighbors(level, getBlockPos());
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
        // List<LazyOptional<IEnergyStorage>> energyList = Arrays.asList(energyNeighbors);
        // energyList.removeIf((pred) -> {
        //     return (pred == null || !pred.isPresent());
        // });
        for (LazyOptional<IEnergyStorage> neighbor : energyNeighbors){
            if(neighbor != null && neighbor.isPresent()){
                neighbor.ifPresent((neiEnergy) -> {
                    int maxTransfer = neiEnergy.receiveEnergy(energyStorage.getEnergyStored(), true);
                    neiEnergy.receiveEnergy(energyStorage.extractEnergy(maxTransfer, false), false);
                });
            }
        }
    }

    
    protected void generateEnergy(){
        heatDifference = 0.0d;
        for (Direction.Axis axis : Direction.Axis.values()){
            Direction dirPos = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
            Direction dirNeg = dirPos.getOpposite();
            BlockEntity tilePos = level.getBlockEntity(getBlockPos().relative(dirPos));
            BlockEntity tileNeg = level.getBlockEntity(getBlockPos().relative(dirNeg));
            if(tilePos != null && tileNeg != null){
                LazyOptional<IHeatContainer> heatNegContainer = tilePos.getCapability(DSCapabilities.HEAT, dirNeg);
                LazyOptional<IHeatContainer> heatPosContainer = tileNeg.getCapability(DSCapabilities.HEAT, dirPos);
                if(heatNegContainer.isPresent() && heatPosContainer.isPresent()){
                    heatNegContainer.ifPresent((heatNeg) -> {
                        heatPosContainer.ifPresent((heatPos) -> {
                            double heatDiff = heatNeg.getHeatStored() - heatPos.getHeatStored();
                            if(heatDiff < 0){
                                heatDiff *= -1;
                            }
                            if(heatDiff > heatDifference){
                                heatDifference = heatDiff;
                                lastAxis = axis.getName().toUpperCase().charAt(0);
                            }
                        });
                    });
                }
            }
        }
        if(heatDifference > 0){
            energyStorage.receiveEnergy((int) (energyGenerated * heatDifference / minHeatDifference) * 5, false);//times 5 since only called once every 5 Ticks
        }
    }


    public char getLastAxis() {
        return lastAxis;
    }

    public int getHeatDifference() {
        return (int) Math.round(heatDifference);
    }
    
}
