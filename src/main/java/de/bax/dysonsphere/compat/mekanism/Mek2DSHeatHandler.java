package de.bax.dysonsphere.compat.mekanism;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DSConfig;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import mekanism.api.heat.IHeatHandler;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class Mek2DSHeatHandler implements ICapabilityProvider {
    
    private final IHeatHandler mekHeat;
    protected final IHeatContainer heatContainer;
    protected final LazyOptional<IHeatContainer> lazyHeat;

    public Mek2DSHeatHandler(IHeatHandler heatHandler){
        this.mekHeat = heatHandler;

        heatContainer = new Mek2DSHeatAdapter();
        lazyHeat = LazyOptional.of(() -> heatContainer);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.HEAT)){
            return lazyHeat.cast();
        }
        
        return LazyOptional.empty();
    }

    public class Mek2DSHeatAdapter implements IHeatContainer {

        @Override
        public double receiveHeat(double maxReceive, boolean simulate) {
            if(!simulate){
                double oldHeat = mekHeat.getTotalTemperature();
                mekHeat.handleHeat(maxReceive / DSConfig.MEK_HEAT_EXCHANGE_RATE.get()); //mekanism just takes the heat. so we always assume max is possible
                return (mekHeat.getTotalTemperature() - oldHeat) * DSConfig.MEK_HEAT_EXCHANGE_RATE.get();
            }
            return maxReceive;
        }

        @Override
        public double extractHeat(double maxExtract, boolean simulate) {
            if(!simulate){
                double oldHeat = mekHeat.getTotalTemperature();
                mekHeat.handleHeat(-maxExtract / DSConfig.MEK_HEAT_EXCHANGE_RATE.get()); //mekanism just takes the heat. so we always assume max is possible
                return (oldHeat - mekHeat.getTotalTemperature())* DSConfig.MEK_HEAT_EXCHANGE_RATE.get();
            }
            return maxExtract;
        }

        @Override
        public double getHeatStored() {
            return mekHeat.getTemperature(0);
        }

        @Override
        public double getMaxHeatStored() {
            return mekHeat.getHeatCapacity(0);
        }

        @Override
        public double getThermalConductivity() {
            return (((mekHeat.getInverseConduction(0) / DSConfig.MEK_HEAT_RESISTANCE.get()) -3) / -2);
        }

        
    }
}
