package de.bax.dysonsphere.tags;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public class DSTags {
    

    public static final TagKey<Fluid> fluidSteam = TagKey.create(Registries.FLUID, forgeLoc("steam"));

    public static final TagKey<Item> itemOrbitCapsule = TagKey.create(Registries.ITEM, modLoc("orbit_capsule"));


    private static ResourceLocation forgeLoc(String name){
        return new ResourceLocation("forge", name);
    }

    private static ResourceLocation modLoc(String name){
        return new ResourceLocation(DysonSphere.MODID, name);
    }

}
