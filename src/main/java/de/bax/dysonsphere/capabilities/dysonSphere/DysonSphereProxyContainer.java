package de.bax.dysonsphere.capabilities.dysonSphere;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class DysonSphereProxyContainer implements ICapabilityProvider {

    LazyOptional<IDysonSphereContainer> lazyDysonSphere = LazyOptional.empty();

    public DysonSphereProxyContainer(Level otherLevel){
        if(!otherLevel.isClientSide){
            lazyDysonSphere = otherLevel.getServer().getLevel(Level.OVERWORLD).getCapability(DSCapabilities.DYSON_SPHERE, Direction.UP);
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.DYSON_SPHERE)){
            return lazyDysonSphere.cast();
        }
        return LazyOptional.empty();
    }
    
}
