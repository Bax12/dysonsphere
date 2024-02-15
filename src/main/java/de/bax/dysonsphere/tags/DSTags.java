package de.bax.dysonsphere.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class DSTags {
    

    public static final TagKey<Fluid> fluidSteam = TagKey.create(Registries.FLUID, forgeLoc("steam"));


    private static ResourceLocation forgeLoc(String name){
        return new ResourceLocation("forge", name);
    }

}
