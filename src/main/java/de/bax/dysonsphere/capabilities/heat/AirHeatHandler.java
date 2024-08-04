package de.bax.dysonsphere.capabilities.heat;

public class AirHeatHandler implements IHeatContainer{

    public static final double maxTransfer = 0.25d;

    @Override
    public double receiveHeat(double maxReceive, boolean simulate) {
        return Math.min(maxReceive, maxTransfer);
    }

    @Override
    public double extractHeat(double maxExtract, boolean simulate) {
        return Math.min(maxExtract, maxTransfer);
    }

    @Override
    public double getHeatStored() {
        return 300;
    }

    @Override
    public double getMaxHeatStored() {
        return 300;
    }

    @Override
    public double getThermalResistance() {
        return 0.01;
    }
    
}
