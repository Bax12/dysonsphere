package de.bax.dysonsphere.items;

import java.awt.Color;

import de.bax.dysonsphere.DSConfig;
import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.fluids.ModFluids;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookBlazeHookItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookControllerItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookElectricEngineItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookEnderRopeItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookHarnessItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookHookItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookManualEngineItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookMechanicalEngineItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookPressureEngine;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookSlimeHookItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookSteamEngineItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookWoodHookItem;
import de.bax.dysonsphere.items.laser.LaserControllerItem;
import de.bax.dysonsphere.items.laser.LaserPatternItem;
import de.bax.dysonsphere.items.laser.TargetDesignatorItem;
import de.bax.dysonsphere.items.tools.WrenchItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
        
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DysonSphere.MODID);
    

    public static final RegistryObject<Item> COIL_COPPER = registerItem("coil_copper");
    public static final RegistryObject<Item> COIL_IRON = registerItem("coil_iron");
    public static final RegistryObject<Item> THERMOPILE = registerItem("thermopile");
    public static final RegistryObject<Item> SOLAR_FOIL = registerItem("solar_foil");
    public static final RegistryObject<Item> CAPSULE_EMPTY = registerItem("capsule_empty");
    public static final RegistryObject<Item> CAPSULE_SOLAR = ITEMS.register("capsule_solar", () -> new CapsuleSolarItem());
    public static final RegistryObject<Item> CAPSULE_LASER = ITEMS.register("capsule_laser", () -> new CapsuleLaserItem());
    public static final RegistryObject<Item> HEAT_SHIELDING = registerItem("heat_shielding");
    public static final RegistryObject<Item> RAILGUN = registerItem("railgun");
    public static final RegistryObject<Item> STEAM_BUCKET = ITEMS.register("bucket_steam", () -> new BucketItem(ModFluids.STEAM, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
    public static final RegistryObject<Item> HELIUM_BUCKET = ITEMS.register("bucket_helium", () -> new BucketItem(ModFluids.HELIUM, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
    public static final RegistryObject<Item> LASER_CONTROLLER = ITEMS.register("laser_controller", () -> new LaserControllerItem());
    public static final RegistryObject<Item> TARGET_DESIGNATOR = ITEMS.register("target_designator", () -> new TargetDesignatorItem());
    public static final RegistryObject<Item> LASER_PATTERN = ITEMS.register("laser_pattern", () -> new LaserPatternItem());
    public static final RegistryObject<Item> UNIVERSE_WHISPER = registerItem("universe_whisper");
    public static final RegistryObject<Item> INGOT_SMART_ALLOY = registerItem("ingot_smart_alloy");
    public static final RegistryObject<Item> COMPONENT_SMART_ALLOY = registerItem("component_smart_alloy");
    public static final RegistryObject<Item> CONSTRUCT_ENDER = registerItem("construct_ender");
    public static final RegistryObject<Item> GRAPPLING_HOOK_HARNESS = ITEMS.register("grappling_hook_harness", () -> new GrapplingHookHarnessItem());
    // public static final RegistryObject<Item> GRAPPLING_HOOK_HARNESS = registerItem("grappling_hook_harness");
    public static final RegistryObject<Item> GRAPPLING_HOOK_CONTROLLER = ITEMS.register("grappling_hook_controller", () -> new GrapplingHookControllerItem());
    public static final RegistryObject<Item> GRAPPLING_HOOK_HOOK_SMART_ALLOY = ITEMS.register("grappling_hook_hook_smart_alloy", () -> new GrapplingHookHookItem(0));
    public static final RegistryObject<Item> GRAPPLING_HOOK_HOOK_BLAZE = ITEMS.register("grappling_hook_hook_blaze", () -> new GrapplingHookBlazeHookItem());
    public static final RegistryObject<Item> GRAPPLING_HOOK_HOOK_WOOD = ITEMS.register("grappling_hook_hook_wood", () -> new GrapplingHookWoodHookItem());
    public static final RegistryObject<Item> GRAPPLING_HOOK_HOOK_SLIME = ITEMS.register("grappling_hook_hook_slime", () -> new GrapplingHookSlimeHookItem());
    public static final RegistryObject<Item> GRAPPLING_HOOK_ENGINE_STEAM = ITEMS.register("grappling_hook_engine_steam", () -> new GrapplingHookSteamEngineItem());
    public static final RegistryObject<Item> GRAPPLING_HOOK_ENGINE_ELECTRIC = ITEMS.register("grappling_hook_engine_electric", () -> new GrapplingHookElectricEngineItem(0));
    public static final RegistryObject<Item> GRAPPLING_HOOK_ENGINE_ELECTRIC_2 = ITEMS.register("grappling_hook_engine_electric2", () -> new GrapplingHookElectricEngineItem(1));
    public static final RegistryObject<Item> GRAPPLING_HOOK_ENGINE_MECHANICAL = ITEMS.register("grappling_hook_engine_mechanical", () -> new GrapplingHookMechanicalEngineItem(2));
    public static final RegistryObject<Item> GRAPPLING_HOOK_ENGINE_MANUAL = ITEMS.register("grappling_hook_engine_manual", () -> new GrapplingHookManualEngineItem());
    public static final RegistryObject<Item> GRAPPLING_HOOK_ROPE_ENDER = ITEMS.register("grappling_hook_rope_ender", () -> new GrapplingHookEnderRopeItem());
    public static final RegistryObject<Item> GRAPPLING_HOOK_ENGINE_PRESSURE = ITEMS.register("grappling_hook_engine_pressure", () -> new GrapplingHookPressureEngine());
    public static final RegistryObject<Item> WRENCH = ITEMS.register("wrench", () -> new WrenchItem()); //not really needed?
    

    public static RegistryObject<Item> registerItem(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

}
