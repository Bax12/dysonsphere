package de.bax.dysonsphere.compat.mekanism;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.orbitalLaser.ILaserReceiver;
import mekanism.api.lasers.ILaserReceptor;
import mekanism.api.math.FloatingLong;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class DS2MekLaserReceiver implements ICapabilityProvider {
    

    protected DS2MekLaserAdapter laserReceiver = new DS2MekLaserAdapter();
    protected LazyOptional<ILaserReceptor> lazyLaser = LazyOptional.of(() -> laserReceiver);

    protected final ILaserReceiver tile;

    public DS2MekLaserReceiver(ILaserReceiver tile){
        this.tile = tile;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap.equals(Mekanism.LASER_RECEPTOR) ? lazyLaser.cast() : LazyOptional.empty();
    }

    public class DS2MekLaserAdapter implements ILaserReceptor {

        @Override
        public void receiveLaserEnergy(@NotNull FloatingLong energy) {
            tile.receiveLaserEnergy(energy.doubleValue());
        }

        @Override
        public boolean canLasersDig() {
            return false; //always false when a DS Tile can receive the laser energy, which is always the case when we get to this point.
        }

    }
}
