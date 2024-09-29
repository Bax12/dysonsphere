package de.bax.dysonsphere.compat.ad_astra;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.compat.IModCompat;
import earth.terrarium.adastra.api.events.AdAstraEvents;
import earth.terrarium.adastra.api.planets.PlanetApi;
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

    public static float getOrbitalLaunchMult(Level level, BlockPos pos){
        float gravity = GravityApi.API.getGravity(level, pos);  //earth is 1. Lower means less energy needed, higher needs more energy.
        float solar = PlanetApi.API.getSolarPower(level) / 16f; //earth is 16. higher means more sunlight => less obstructions to the sun. At least thats how I will use it.
        return (float) Math.pow(Math.E, gravity - 1f) / Math.max(0.0001f, solar);
    }




}
