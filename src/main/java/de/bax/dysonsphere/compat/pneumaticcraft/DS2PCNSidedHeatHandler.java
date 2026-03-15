package de.bax.dysonsphere.compat.pneumaticcraft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DSConfig;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.ISidedHeatTile;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerAdapter;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerLogic;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class DS2PCNSidedHeatHandler implements ICapabilityProvider {
    
    protected final ISidedHeatTile tile;
    protected final DS2PNCSidedHeatAdapter heatAdapter[] = new DS2PNCSidedHeatAdapter[6];
    protected final LazyOptional<IHeatExchangerLogic>[] lazyHeatAdapter = new LazyOptional[6];


    public DS2PCNSidedHeatHandler(ISidedHeatTile tile){
        this.tile = tile;
        for(int i = 0; i < 6; i++){
            heatAdapter[i] = new DS2PNCSidedHeatAdapter(Direction.values()[i]);
            int a = i;
            this.lazyHeatAdapter[i] = LazyOptional.of(() -> heatAdapter[a]);
        }
        
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(Pneumaticcraft.HEAT_HANDLER) && side != null){
            return lazyHeatAdapter[side.ordinal()].cast();
        }
        return LazyOptional.empty();
    }

    public class DS2PNCSidedHeatAdapter implements IHeatExchangerAdapter {

        Direction facing;

        public DS2PNCSidedHeatAdapter(Direction facing) {
            this.facing = facing;
        }

        @Override
        public double getTemperature() {
            return tile.getHeatContainer(facing).getHeatStored();
        }

        @Override
        public double getAmbientTemperature() {
            return HeatHandler.HEAT_AMBIENT;
        }

        @Override
        public double getThermalResistance() {
            return 1 / tile.getHeatContainer(facing).getThermalConductivity();
        }

        @Override
        public double getThermalCapacity() {
            return tile.getHeatContainer(facing).getMaxHeatStored();
        }

        @Override
        public void addHeat(double amount) {
            amount *= DSConfig.PNC_HEAT_EXCHANGE_RATE.get();
            if(amount >= 0){
                tile.getHeatContainer(facing).receiveHeat(amount, false);
            } else {
                tile.getHeatContainer(facing).extractHeat(-amount, false);
            }
        }

        @Override
        public boolean isSideConnected(Direction side) {
            return true;
        }

        

    }
}
