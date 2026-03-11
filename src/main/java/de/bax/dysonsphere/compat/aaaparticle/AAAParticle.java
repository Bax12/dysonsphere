package de.bax.dysonsphere.compat.aaaparticle;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.compat.IModCompat;
import de.bax.dysonsphere.entities.LaserStrikeEntity;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class AAAParticle implements IModCompat {

    public static final ParticleEmitterInfo PLASMA = new ParticleEmitterInfo(new ResourceLocation(DysonSphere.MODID, "plasma"));
    public static final ParticleEmitterInfo LASER = new ParticleEmitterInfo(new ResourceLocation(DysonSphere.MODID, "laser"));
    public static final ParticleEmitterInfo ORBITAL_LAUNCH = new ParticleEmitterInfo(new ResourceLocation(DysonSphere.MODID, "orbital_launch"));

    public static void bindLaserEmitter(LaserStrikeEntity entity){
        ParticleEmitterInfo laser = LASER.clone();
        
        laser.bindOnEntity(entity);
        laser.entitySpaceRelativePosition(0, 0, 0);
        laser.scale(entity.getSize()/10f, 10f, entity.getSize()/10f); //x and z /10 for size like the render. technically to small...
        laser.parameter(0, entity.getStrikeTime()*3f);//Why *3? No idea but feels correct. //parameter 0 is the lifetime of this effek.
        
        AAALevel.addParticle(entity.level(), true, laser);

    }

    public static void bindPlasmaEmitter(Entity entity){
        ParticleEmitterInfo plasma = PLASMA.clone();

        plasma.bindOnEntity(entity);
        plasma.entitySpaceRelativePosition(0, 0, 0);
        // plasma.rotation(-(float) Math.PI / 2f, 0, 0); //rotation is in rads  
        
        plasma.scale(0.2f);

        AAALevel.addParticle(entity.level(), plasma);
    }

    public static void spawnOrbitalLaunchEmitter(Level level, float x, float y, float z, float rotX, float rotY, float rotZ){
        ParticleEmitterInfo launch = ORBITAL_LAUNCH.clone();
        launch.scale(0.2f);
        launch.position(x, y, z);
        launch.rotation(rotX, rotY, rotZ);

        AAALevel.addParticle(level, true, launch);
    }


}
