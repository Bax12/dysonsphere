package de.bax.dysonsphere.capabilities.heat;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class HeatHandler implements IHeatContainer, INBTSerializable<CompoundTag>{

    /**
     * The default ambient temperature, used to initialize heatHandlers
     */
    public static double HEAT_AMBIENT = 300d;

    protected double heat;
    protected double maxHeat;

    protected LazyOptional<IHeatContainer>[] neighborList = new LazyOptional[6];

    public HeatHandler(){
        this(100_000d);
    }

    public HeatHandler(double maxHeat){
        this(HEAT_AMBIENT, maxHeat);
    }


    public HeatHandler(double heat, double maxHeat){
        this.heat = heat;
        this.maxHeat = maxHeat;
    }

    @Override
    public double receiveHeat(double maxReceive, boolean simulate) {
        maxReceive = Math.min(maxReceive, maxHeat - heat) * getThermalConductivity();
        if(!simulate){
            heat += maxReceive;
        }
        return maxReceive;
    }

    @Override
    public double extractHeat(double maxExtract, boolean simulate) {
        maxExtract = Math.min(maxExtract, 0 + heat) * getThermalConductivity();
        if(!simulate){
            heat -= maxExtract;
        }
        return maxExtract;
    }

    @Override
    public double getHeatStored() {
        return heat;
    }

    @Override
    public double getMaxHeatStored() {
        return maxHeat;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("heat", heat);
        tag.putDouble("maxHeat", maxHeat);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("heat")){
            heat = nbt.getDouble("heat");
        }
        if(nbt.contains("maxHeat")){
            maxHeat = nbt.getDouble("maxHeat");
        }
    }

    public LazyOptional<IHeatContainer>[] getNeighborList() {
        return neighborList;
    }

    public void updateNeighbors(Level level, BlockPos pos){
        for(Direction dir : Direction.values()){
            BlockPos neiPos = pos.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neiPos);
            if(neighbor != null){
                LazyOptional<IHeatContainer> neighborHandler = neighbor.getCapability(DSCapabilities.HEAT, dir.getOpposite());
                if (neighborHandler.isPresent()){
                    neighborList[dir.ordinal()] = neighborHandler;
                }
            } else if(level.getBlockState(neiPos).isAir()){
                neighborList[dir.ordinal()] = LazyOptional.of(() -> new AirHeatHandler());
            } else {
                neighborList[dir.ordinal()] = LazyOptional.empty();
            }
        }
        
    }

    public double getThermalConductivity(){
        return 1;
    }

    public void splitShare(){
        for (LazyOptional<IHeatContainer> neighbor : neighborList){
            if(neighbor != null && neighbor.isPresent()){
                neighbor.ifPresent((neiHeat) -> {
                    double diff = (neiHeat.getHeatStored() - this.getHeatStored()) / 2d;
                    if(diff == 0){ //skip transfer logic when equal
                        return;
                    } else if(diff > 0){ //tranfer from neighbor to self when neighbor is greater
                        double transfer = Math.min(neiHeat.extractHeat(diff, true), this.receiveHeat(diff, true));
                        neiHeat.extractHeat(transfer, false);
                        this.receiveHeat(transfer, false);
                    } else { //transfer from self to neighbor if neighbor is lesser
                        double transfer = Math.min(neiHeat.receiveHeat(-diff, true), this.extractHeat(-diff, true));
                        neiHeat.receiveHeat(transfer, false);
                        this.extractHeat(transfer, false);
                    }
                });
            }
        }
    }

    
}
