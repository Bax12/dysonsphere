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
    public static final TagKey<Item> itemIngotSmartAlloy = TagKey.create(Registries.ITEM, forgeLoc("ingots/nitinol"));

    public static final TagKey<Item> itemIngotNickel = TagKey.create(Registries.ITEM, forgeLoc("ingots/nickel"));
    public static final TagKey<Item> itemIngotTitanium = TagKey.create(Registries.ITEM, forgeLoc("ingots/titanium"));
    public static final TagKey<Item> itemIngotTungsten = TagKey.create(Registries.ITEM, forgeLoc("ingots/tungsten"));
    public static final TagKey<Item> itemIngotCompressedIron = TagKey.create(Registries.ITEM, forgeLoc("ingots/compressed_iron"));
    public static final TagKey<Item> itemIngotSignalum = TagKey.create(Registries.ITEM, forgeLoc("ingots/signalum"));
    public static final TagKey<Item> itemIngotLumium = TagKey.create(Registries.ITEM, forgeLoc("ingots/lumium"));

    public static final TagKey<Item> itemCircuit = TagKey.create(Registries.ITEM, forgeLoc("circuits"));
    public static final TagKey<Item> itemGear = TagKey.create(Registries.ITEM, forgeLoc("gears"));
    public static final TagKey<Item> itemWireCopper = TagKey.create(Registries.ITEM, forgeLoc("wires/copper"));
    public static final TagKey<Item> itemWireIron = TagKey.create(Registries.ITEM, forgeLoc("wires/iron"));

    public static final TagKey<Item> itemCapsule = TagKey.create(Registries.ITEM, modLoc("space_capsules"));
    public static final TagKey<Item> itemCapsuleEmpty = TagKey.create(Registries.ITEM, modLoc("space_capsules/empty"));
    public static final TagKey<Item> itemCapsuleSolar = TagKey.create(Registries.ITEM, modLoc("space_capsules/solar"));
    public static final TagKey<Item> itemCapsuleLaser = TagKey.create(Registries.ITEM, modLoc("space_capsules/laser"));

    public static final TagKey<Item> itemGrapplingHookComponent = TagKey.create(Registries.ITEM, modLoc("grappling_hook_components"));
    public static final TagKey<Item> itemGrapplingHookHook = TagKey.create(Registries.ITEM, modLoc("grappling_hook_components/hook"));
    public static final TagKey<Item> itemGrapplingHookRope = TagKey.create(Registries.ITEM, modLoc("grappling_hook_components/rope)"));
    public static final TagKey<Item> itemGrapplingHookEngine = TagKey.create(Registries.ITEM, modLoc("grappling_hook_components/engine"));
    


    private static ResourceLocation forgeLoc(String name){
        return new ResourceLocation("forge", name);
    }

    private static ResourceLocation modLoc(String name){
        return new ResourceLocation(DysonSphere.MODID, name);
    }

}
