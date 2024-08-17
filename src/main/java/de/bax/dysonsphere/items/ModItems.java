package de.bax.dysonsphere.items;

import java.awt.Color;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.fluids.ModFluids;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookBlazeHookItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookControllerItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookElectricEngineItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookHarnessItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookHookItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookManualEngineItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookSlimeHookItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookSteamEngineItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookWoodHookItem;
import de.bax.dysonsphere.items.laser.LaserControllerItem;
import de.bax.dysonsphere.items.laser.LaserPatternItem;
import de.bax.dysonsphere.items.laser.TargetDesignatorItem;
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
    public static final RegistryObject<Item> LASER_CONTROLLER = ITEMS.register("laser_controller", () -> new LaserControllerItem());
    public static final RegistryObject<Item> TARGET_DESIGNATOR = ITEMS.register("target_designator", () -> new TargetDesignatorItem());
    public static final RegistryObject<Item> LASER_PATTERN = ITEMS.register("laser_pattern", () -> new LaserPatternItem());
    public static final RegistryObject<Item> UNIVERSE_WHISPER = registerItem("universe_whisper");
    public static final RegistryObject<Item> INGOT_SMART_ALLOY = registerItem("ingot_smart_alloy");
    public static final RegistryObject<Item> COMPONENT_SMART_ALLOY = registerItem("component_smart_alloy");
    public static final RegistryObject<Item> GRAPPLING_HOOK_HARNESS = ITEMS.register("grappling_hook_harness", () -> new GrapplingHookHarnessItem());
    public static final RegistryObject<Item> GRAPPLING_HOOK_CONTROLLER = ITEMS.register("grappling_hook_controller", () -> new GrapplingHookControllerItem());
    public static final RegistryObject<Item> GRAPPLING_HOOK_HOOK_SMART_ALLOY = ITEMS.register("grappling_hook_hook_smart_alloy", () -> new GrapplingHookHookItem(3, 0.02f, 0x383738));
    public static final RegistryObject<Item> GRAPPLING_HOOK_BLAZE_HOOK = ITEMS.register("grappling_hook_hook_blaze", () -> new GrapplingHookBlazeHookItem());
    public static final RegistryObject<Item> GRAPPLING_HOOK_WOOD_HOOK = ITEMS.register("grappling_hook_hook_wood", () -> new GrapplingHookWoodHookItem());
    public static final RegistryObject<Item> GRAPPLING_HOOK_SLIME_HOOK = ITEMS.register("grappling_hook_hook_slime", () -> new GrapplingHookSlimeHookItem());
    public static final RegistryObject<Item> GRAPPLING_HOOK_STEAM_ENGINE = ITEMS.register("grappling_hook_engine_steam", () -> new GrapplingHookSteamEngineItem());
    public static final RegistryObject<Item> GRAPPLING_HOOK_ELECTRIC_ENGINE = ITEMS.register("grappling_hook_engine_electric", () -> new GrapplingHookElectricEngineItem(15000, 500, 50, 5, 1, 2.5f, 3.2f, Color.GRAY.getRGB()));//TODO change to config values?
    public static final RegistryObject<Item> GRAPPLING_HOOK_ELECTRIC_ENGINE_2 = ITEMS.register("grappling_hook_engine_electric2", () -> new GrapplingHookElectricEngineItem(1_000_000, 5000, 50, 10, 5, 5.0f, 4.4f, 0x353535));//make these config values as well
    public static final RegistryObject<Item> GRAPPLING_HOOK_MANUAL_ENGINE = ITEMS.register("grappling_hook_engine_manual", () -> new GrapplingHookManualEngineItem());
    

    public static RegistryObject<Item> registerItem(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

}
