package de.bax.dysonsphere.entities;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DysonSphere.MODID);

    public static final RegistryObject<EntityType<TargetDesignatorEntity>> TARGET_DESIGNATOR = ENTITIES.register("target_designator", () -> EntityType.Builder.of((EntityType.EntityFactory<TargetDesignatorEntity>) TargetDesignatorEntity::new, MobCategory.MISC).setTrackingRange(256).updateInterval(10).setShouldReceiveVelocityUpdates(true).sized(0.5f, 0.5f).build(""));
    public static final RegistryObject<EntityType<LaserStrikeEntity>> LASER_STRIKE = ENTITIES.register("laser_strike", () -> EntityType.Builder.of((EntityType.EntityFactory<LaserStrikeEntity>) LaserStrikeEntity::new, MobCategory.MISC).setTrackingRange(256).updateInterval(10).setShouldReceiveVelocityUpdates(true).sized(0, 0).build(""));
    public static final RegistryObject<EntityType<GrapplingHookEntity>> GRAPPLING_HOOK = ENTITIES.register("grappling_hook", () -> EntityType.Builder.of((EntityType.EntityFactory<GrapplingHookEntity>) GrapplingHookEntity::new, MobCategory.MISC).setTrackingRange(256).updateInterval(10).setShouldReceiveVelocityUpdates(true).sized(0.15f, 0.15f).build(""));

}
