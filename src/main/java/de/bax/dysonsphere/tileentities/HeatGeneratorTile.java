package de.bax.dysonsphere.tileentities;

import java.util.Arrays;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.energy.ExternalEnergyWrapper;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import de.bax.dysonsphere.capabilities.heat.ISidedHeatTile;
import de.bax.dysonsphere.capabilities.heat.SidedMultiHeatHandler;
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

public class HeatGeneratorTile extends BaseTile implements ISidedHeatTile {

    public static double maxHeat = 1700;
    public static final double maxHeatTransfer = 25;
    public static int energyCapacity = 25000;

    public static double minHeatDifference = 25;
    public static int energyGenerated = 1;

    public HeatHandler internalHeat = new HeatHandler(maxHeat){//only changes with side handlers, so no extra change check needed
        @Override
        public void splitShare() {
            for(IHeatContainer side : sideHeat){
                double diff = (side.getHeatStored() - this.getHeatStored()) / 2d;
                if(diff == 0){ //skip transfer logic when equal
                    continue;
                } else if(diff > 0){ //tranfer from neighbor to self when neighbor is greater
                    double transfer = Math.min(side.extractHeat(diff, true), this.receiveHeat(diff, true));
                    side.extractHeat(transfer, false);
                    this.receiveHeat(transfer, false);
                } else { //transfer from self to neighbor if neighbor is lesser
                    double transfer = Math.min(side.receiveHeat(-diff, true), this.extractHeat(-diff, true));
                    side.receiveHeat(transfer, false);
                    this.extractHeat(transfer, false);
                }
            }
        }

        @Override
        public double getThermalConductivity(){
            return 0.025d;
        }
    };
    public SidedMultiHeatHandler[] sideHeat = new SidedMultiHeatHandler[6];
    public EnergyStorage energyStorage = new EnergyStorage(energyCapacity);

    protected IEnergyStorage externalEnergy = new ExternalEnergyWrapper(energyStorage) {
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        };
        public boolean canReceive() {
            return false;
        };
    };

    protected LazyOptional<IHeatContainer> lazyHeatContainer[] = new LazyOptional[6];
    protected LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> externalEnergy);

    protected int ticksElapsed = 0;
    protected double lastHeat = 0;
    protected int lastEnergy = 0;

    protected double heatDifference = 0.0d;
    protected int axis = -1;

    boolean dirty = false;

    protected LazyOptional<IEnergyStorage>[] energyNeighbors = new LazyOptional[6];

    public HeatGeneratorTile(BlockPos pos, BlockState state) {
        super(ModTiles.HEAT_GENERATOR.get(), pos, state);


        for(int i = 0; i < sideHeat.length; i++){
            sideHeat[i] = new SidedMultiHeatHandler(maxHeat){
                @Override
                public double receiveHeat(double maxReceive, boolean simulate) {
                    if(!simulate){
                        setChanged();
                    }
                    return super.receiveHeat(maxReceive, simulate);
                };

                @Override
                public double extractHeat(double maxExtract, boolean simulate) {
                    if(!simulate){
                        setChanged();
                    }
                    return super.extractHeat(maxExtract, simulate);
                };

                @Override
                public double getThermalConductivity() {
                    return 1d;
                }
            };
            int a = i; //making the supplier happy
            lazyHeatContainer[i] = LazyOptional.of(() -> sideHeat[a]);
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.HEAT)){
            return lazyHeatContainer[side.ordinal()].cast();
        } else if (cap.equals(ForgeCapabilities.ENERGY)){
            return lazyEnergyStorage.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        Arrays.stream(lazyHeatContainer).forEach(LazyOptional::invalidate);
        lazyEnergyStorage.invalidate();
    }

    public void tick(){
        if(!level.isClientSide){
            if(ticksElapsed++ % 5 == 0){
                Arrays.stream(sideHeat).forEach(SidedMultiHeatHandler::splitShare);
                generateEnergy();
                internalHeat.splitShare();
                splitShareEnergy();
                // boolean shouldSync = false;
                // if(lastHeat != heatHandler.getHeatStored()){
                //     lastHeat = heatHandler.getHeatStored();
                //     shouldSync = true;
                // }
                // if(lastEnergy != energyStorage.getEnergyStored()){
                //     lastEnergy = energyStorage.getMaxEnergyStored();
                //     shouldSync = true;
                // }
                if(dirty){
                    dirty = false;
                    sendSyncPackageToNearbyPlayers();
                }
            }
            
        }
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        if(tag.contains("Energy")){
            energyStorage.deserializeNBT(tag.get("Energy"));
        } 
        if(tag.contains("Heat")) {
            internalHeat.deserializeNBT(tag.getCompound("Heat"));
        }
        heatDifference = tag.getInt("HeatDifference");
        for(int i = 0; i < sideHeat.length; i++){
            if(tag.contains("Heat"+i)) {
                sideHeat[i].deserializeNBT(tag.getCompound("Heat"+i));
            }
        }
        if(tag.contains("Axis")) {
            axis = tag.getByte("Axis");
        }
    }

    @Override
    protected void saveAdditional( @Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("Heat", internalHeat.serializeNBT());
        tag.putInt("HeatDifference", getHeatDifference());
        for(int i = 0; i < sideHeat.length; i++){
            tag.put("Heat"+i, sideHeat[i].serializeNBT());
        }
        tag.putByte("Axis", (byte) axis);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.dirty = true;
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
        for(Direction dir : Direction.values()){
            BlockEntity neighbor = level.getBlockEntity(getBlockPos().relative(dir));
            if(neighbor != null){
                LazyOptional<IEnergyStorage> neighborHandler = neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite());
                if (neighborHandler.isPresent()){
                    energyNeighbors[dir.ordinal()] = neighborHandler;
                }
            }
            sideHeat[dir.ordinal()].updateNeighbors(level, worldPosition, dir);
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
        // for (Direction.Axis axis : Direction.Axis.values()){
        //     Direction dirPos = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        //     Direction dirNeg = dirPos.getOpposite();
        //     BlockEntity tilePos = level.getBlockEntity(getBlockPos().relative(dirPos));
        //     BlockEntity tileNeg = level.getBlockEntity(getBlockPos().relative(dirNeg));
        //     if(tilePos != null && tileNeg != null){
        //         LazyOptional<IHeatContainer> heatNegContainer = tilePos.getCapability(DSCapabilities.HEAT, dirNeg);
        //         LazyOptional<IHeatContainer> heatPosContainer = tileNeg.getCapability(DSCapabilities.HEAT, dirPos);
        //         if(heatNegContainer.isPresent() && heatPosContainer.isPresent()){
        //             heatNegContainer.ifPresent((heatNeg) -> {
        //                 heatPosContainer.ifPresent((heatPos) -> {
        //                     double heatDiff = heatNeg.getHeatStored() - heatPos.getHeatStored();
        //                     if(heatDiff < 0){
        //                         heatDiff *= -1;
        //                     }
        //                     if(heatDiff > heatDifference){
        //                         heatDifference = heatDiff;
        //                         lastAxis = axis.getName().toUpperCase().charAt(0);
        //                     }
        //                 });
        //             });
        //         }
        //     }
        // }

        for (int i = 0; i < 6; i += 2){
            double heatDiff = Math.abs(sideHeat[i].getHeatStored() - sideHeat[i+1].getHeatStored());
            if(heatDiff > heatDifference){
                heatDifference = heatDiff;
                axis = i/2;
            }
        }

        if(heatDifference > 0){
            energyStorage.receiveEnergy((int) (energyGenerated * heatDifference / minHeatDifference) * 5, false);//times 5 since only called once every 5 Ticks
        }
    }


    public int getAxis() {
        return axis;
    }

    public int getHeatDifference() {
        return (int) Math.round(heatDifference);
    }

    @Override
    public IHeatContainer getHeatContainer() {
        return internalHeat;
    }

    @Override
    public IHeatContainer getHeatContainer(Direction facing) {
        return sideHeat[facing.ordinal()];
    }
    
}
