package de.bax.dysonsphere.compat.ad_astra;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.compat.IModCompat;
import earth.terrarium.adastra.api.events.AdAstraEvents;
import earth.terrarium.adastra.api.systems.GravityApi;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AdAstra implements IModCompat {
    
    @Override
    public void init() {
        DysonSphere.LOGGER.info("Ad Astra Compat Loading");
        AdAstraEvents.GravityTickEvent.register(this::onGravityTick);
    }


    //return false to prevent ad_astra gravity ticking
    protected boolean onGravityTick(Level level, LivingEntity entity, Vec3 travelVector, BlockPos movementAffectingPos){
        return entity.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).map((hookContainer) -> {
            return !hookContainer.shouldIgnoreGravityChange();
        }).orElse(true);
    }

    public static float getGravity(Entity entity){
        return GravityApi.API.getGravity(entity);
    }




}
