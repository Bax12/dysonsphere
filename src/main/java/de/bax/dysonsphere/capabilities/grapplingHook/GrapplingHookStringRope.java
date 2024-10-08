package de.bax.dysonsphere.capabilities.grapplingHook;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class GrapplingHookStringRope implements IGrapplingHookRope, ICapabilityProvider {

    public static float MAX_DISTANCE = 16f;
    public static float LAUNCH_FORCE = 1.0f;
    public static float WINCH_FORCE = 0.8f;
    public static float GRAVITY = 1.0f;

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.GRAPPLING_HOOK_ROPE)){
            return LazyOptional.of(() -> this).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public float getMaxDistance(Level level, Player player) {
        return MAX_DISTANCE;
    }

    @Override
    public float getLaunchForceMultiplier(Level level, Player player) {
        return LAUNCH_FORCE;
    }

    @Override
    public float getWinchForceMultiplier(Level level, Player player) {
        return WINCH_FORCE;
    }

    @Override
    public float getHookGravityMultiplier(Level level, Player player) {
        return GRAVITY;
    }

    @Override
    public void onHookLaunch(Level level, Player player, GrapplingHookEntity hook) {
    }

    @Override
    public void onHookDeploy(Level level, Player player, GrapplingHookEntity hook) {
    }

    @Override
    public void onActiveWinchTick(Level level, Player player) {
    }

    @Override
    public void onRappelTick(Level level, Player player) {
    }

    @Override
    public int getColor() {
        return 0xCCCCCC;
    }
    
}
