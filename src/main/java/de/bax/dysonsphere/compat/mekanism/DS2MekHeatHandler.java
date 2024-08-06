package de.bax.dysonsphere.compat.mekanism;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DSConfig;
import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import mekanism.api.heat.IHeatHandler;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class DS2MekHeatHandler implements ICapabilityProvider {

    protected final IHeatTile tile;
    protected final IHeatHandler heatAdapter = new DS2MekHeatAdapter();
    protected final LazyOptional<IHeatHandler> lazyHeatAdapter = LazyOptional.of(() -> heatAdapter);
    

    public DS2MekHeatHandler(IHeatTile tile){
        this.tile = tile;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(Mekanism.HEAT_HANDLER)){
            return lazyHeatAdapter.cast();
        }
        
        return LazyOptional.empty();
    }

    public class DS2MekHeatAdapter implements IHeatHandler {

        @Override
        public int getHeatCapacitorCount() {
            return 1;
        }

        @Override
        public double getTemperature(int capacitor) {
            return tile.getHeat();
        }

        @Override
        public double getInverseConduction(int capacitor) {
            return ((tile.getHeatContainer().getThermalConductivity() * -2) +3) * DSConfig.MEK_HEAT_RESISTANCE.get();
        }

        @Override
        public double getHeatCapacity(int capacitor) {
            return tile.getHeatContainer().getMaxHeatStored();
        }

        @Override
        public void handleHeat(int capacitor, double transfer) {
            transfer *= DSConfig.MEK_HEAT_EXCHANGE_RATE.get();
            if(transfer >= 0){
                tile.getHeatContainer().receiveHeat(transfer, false);
            } else {
                tile.getHeatContainer().extractHeat(-transfer, false);
            }
        }

        
        
    }
    
}
