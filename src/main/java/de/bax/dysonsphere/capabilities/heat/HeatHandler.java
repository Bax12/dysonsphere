package de.bax.dysonsphere.capabilities.heat;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public class HeatHandler implements IHeatContainer, INBTSerializable<Tag>{

    protected double heat;
    protected double maxHeat;

    public HeatHandler(double heat){
        this(heat, 1000000);
    }


    public HeatHandler(double heat, double maxHeat){
        this.heat = heat;
        this.maxHeat = maxHeat;
    }

    @Override
    public double receiveHeat(double maxReceive, boolean simulate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'receiveHeat'");
    }

    @Override
    public double extractHeat(double maxExtract, boolean simulate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'extractHeat'");
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
    public Tag serializeNBT() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'serializeNBT'");
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deserializeNBT'");
    }


    
}
