package de.bax.dysonsphere.tags;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public class DSTags {
    

    public static final TagKey<Fluid> fluidSteam = TagKey.create(Registries.FLUID, forgeLoc("steam"));

    public static final TagKey<Item> itemCoil = TagKey.create(Registries.ITEM, forgeLoc("coils"));
    public static final TagKey<Item> itemCoilCopper = TagKey.create(Registries.ITEM, forgeLoc("coils/copper"));
    public static final TagKey<Item> itemCoilIron = TagKey.create(Registries.ITEM, forgeLoc("coils/iron"));

    public static final TagKey<Item> itemIngot = TagKey.create(Registries.ITEM, forgeLoc("ingots"));
    public static final TagKey<Item> itemIngotSmartAlloy = TagKey.create(Registries.ITEM, forgeLoc("ingots/smart_alloy"));

    public static final TagKey<Item> itemCapsule = TagKey.create(Registries.ITEM, modLoc("space_capsules"));
    public static final TagKey<Item> itemCapsuleEmpty = TagKey.create(Registries.ITEM, modLoc("space_capsules/empty"));
    public static final TagKey<Item> itemCapsuleSolar = TagKey.create(Registries.ITEM, modLoc("space_capsules/solar"));
    public static final TagKey<Item> itemCapsuleLaser = TagKey.create(Registries.ITEM, modLoc("space_capsules/laser"));


    private static ResourceLocation forgeLoc(String name){
        return new ResourceLocation("forge", name);
    }

    private static ResourceLocation modLoc(String name){
        return new ResourceLocation(DysonSphere.MODID, name);
    }

}
