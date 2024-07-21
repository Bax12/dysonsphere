package de.bax.dysonsphere.compat.mekanism;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.ILaserReceiver;
import mekanism.api.lasers.ILaserReceptor;
import mekanism.api.math.FloatingLong;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class Mek2DSLaserReceiver implements ICapabilityProvider {

    protected Mek2DSLaserAdapter laserReceiver = new Mek2DSLaserAdapter();
    protected LazyOptional<ILaserReceiver> lazyLaser = LazyOptional.of(() -> laserReceiver);

    protected final ILaserReceptor tile;

    public Mek2DSLaserReceiver(ILaserReceptor tile){
        this.tile = tile;
    }


    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap.equals(DSCapabilities.LASER_RECEIVER) ? lazyLaser.cast() : LazyOptional.empty();
    }

    public class Mek2DSLaserAdapter implements ILaserReceiver {

        @Override
        public void receiveLaserEnergy(double energy) {
            tile.receiveLaserEnergy(FloatingLong.create(energy));
        }

    }
    
}
