package de.bax.dysonsphere.capabilities.grapplingHook;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class GrapplingHookChainRope implements IGrapplingHookRope, ICapabilityProvider {

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.GRAPPLING_HOOK_ROPE)){
            return LazyOptional.of(() -> this).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public float getMaxDistance(Level level, Player player) {
        return 32f;
    }

    @Override
    public float getLaunchForceMultiplier(Level level, Player player) {
        return 0.6f;
    }

    @Override
    public float getWinchForceMultiplier(Level level, Player player) {
        return 1.2f;
    }

    @Override
    public float getHookGravityMultiplier(Level level, Player player) {
        return 1.2f;
    }

    @Override
    public void onHookLaunch(Level level, Player player, GrapplingHookEntity hook) {
        
    }

    @Override
    public void onHookDeploy(Level level, Player player, GrapplingHookEntity hook) {
        
    }

    @Override
    public void onActiveWinchTick(Level level, Player player) {
        if(player.tickCount % 5 == 0){
            level.playSound(null, player, SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 0.5f, 1.2f);
        }
    }

    @Override
    public void onRappelTick(Level level, Player player) {
        if(player.tickCount % 10 == 0){
            level.playSound(null, player, SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 0.5f, 1.2f);
        }
    }

    @Override
    public int getColor() {
        return 0x575959;
    }
    
}
