package de.bax.dysonsphere.compat.pneumaticcraft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerAdapter;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerLogic;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class DS2PNCHeatHandler implements ICapabilityProvider {
    
    protected final IHeatTile tile;
    protected final DS2PNCHeatAdapter heatAdapter = new DS2PNCHeatAdapter();
    protected final LazyOptional<IHeatExchangerLogic> lazyHeatAdapter;


    public DS2PNCHeatHandler(IHeatTile tile){
        this.tile = tile;
        this.lazyHeatAdapter = LazyOptional.of(() -> heatAdapter);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(Pneumaticcraft.HEAT_HANDLER)){
            return lazyHeatAdapter.cast();
        }
        return LazyOptional.empty();
    }

    public class DS2PNCHeatAdapter implements IHeatExchangerAdapter {

        @Override
        public double getTemperature() {
            return tile.getHeatContainer().getHeatStored();
        }

        @Override
        public double getAmbientTemperature() {
            return 300;
        }

        @Override
        public double getThermalResistance() {
            return tile.getHeatContainer().getThermalConductivity();
        }

        @Override
        public double getThermalCapacity() {
            return tile.getHeatContainer().getMaxHeatStored();
        }

        @Override
        public void addHeat(double amount) {
            if(amount >= 0){
                tile.getHeatContainer().receiveHeat(amount, false);
            } else {
                tile.getHeatContainer().extractHeat(-amount, false);
            }
        }

        @Override
        public boolean isSideConnected(Direction side) {
            return true;
        }

        

    }


}
