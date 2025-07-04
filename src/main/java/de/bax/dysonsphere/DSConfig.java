package de.bax.dysonsphere;

import java.util.List;

import de.bax.dysonsphere.capabilities.grapplingHook.GrapplingHookChainRope;
import de.bax.dysonsphere.capabilities.grapplingHook.GrapplingHookStringRope;
import de.bax.dysonsphere.capabilities.grapplingHook.GrapplingHookTripWireHook;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.entities.LaserStrikeEntity;
import de.bax.dysonsphere.items.CapsuleItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookBlazeHookItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookElectricEngineItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookEnderRopeItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookHookItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookManualEngineItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookSlimeHookItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookSteamEngineItem;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookWoodHookItem;
import de.bax.dysonsphere.items.laser.LaserControllerItem;
import de.bax.dysonsphere.tileentities.DSEnergyReceiverTile;
import de.bax.dysonsphere.tileentities.HeatExchangerTile;
import de.bax.dysonsphere.tileentities.HeatGeneratorTile;
import de.bax.dysonsphere.tileentities.HeatPipeTile;
import de.bax.dysonsphere.tileentities.LaserControllerTile;
import de.bax.dysonsphere.tileentities.LaserCrafterTile;
import de.bax.dysonsphere.tileentities.LaserPatternControllerTile;
import de.bax.dysonsphere.tileentities.RailgunTile;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;


@Mod.EventBusSubscriber(modid = DysonSphere.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DSConfig {

        //####COMMON####

        //Dysonsphere
        private static ForgeConfigSpec.ConfigValue<List<? extends String>> DYSON_SPHERE_DIM_BLACKLIST;
        private static ForgeConfigSpec.BooleanValue DYSON_SPHERE_IS_WHITELIST;

        //General
        private static ForgeConfigSpec.DoubleValue GENERAL_HEAT_AMBIENT;
        private static ForgeConfigSpec.DoubleValue GENERAL_LASER_ENERGY_MULT;

        //Machines
        private static ForgeConfigSpec.DoubleValue DS_ENERGY_RECEIVER_MAX_HEAT;
                        
        private static ForgeConfigSpec.DoubleValue HEAT_EXCHANGER_MAX_HEAT;
        private static ForgeConfigSpec.IntValue HEAT_EXCHANGER_FLUID_CAPACITY;

        private static ForgeConfigSpec.DoubleValue HEAT_GENERATOR_MAX_HEAT;
        private static ForgeConfigSpec.IntValue HEAT_GENERATOR_ENERGY_CAPACITY;
        private static ForgeConfigSpec.DoubleValue HEAT_GENERATOR_MIN_HEAT_DIF;
        private static ForgeConfigSpec.IntValue HEAT_GENERATOR_ENERGY_GENERATED;
                        
        private static ForgeConfigSpec.DoubleValue HEAT_PIPE_MAX_HEAT;
                        
        private static ForgeConfigSpec.IntValue RAILGUN_LAUNCH_ENERGY;
        private static ForgeConfigSpec.IntValue RAILGUN_ENERGY_CAPACITY;
                        
        private static ForgeConfigSpec.IntValue LASER_CONTROLLER_TILE_ENERGY_USAGE;
        private static ForgeConfigSpec.IntValue LASER_CONTROLLER_TILE_ENERGY_CAPACITY;
        private static ForgeConfigSpec.IntValue LASER_CONTROLLER_TILE_ENERGY_COOLDOWN;
        private static ForgeConfigSpec.IntValue LASER_CONTROLLER_TILE_ENERGY_WORKTIME;
        
        private static ForgeConfigSpec.IntValue LASER_CRAFTER_ENERGY_BLEED_STATIC;   
        private static ForgeConfigSpec.DoubleValue LASER_CRAFTER_ENERGY_BLEED_SCALING;
        private static ForgeConfigSpec.DoubleValue LASER_CRAFTER_MAX_HEAT;
        private static ForgeConfigSpec.DoubleValue LASER_CRAFTER_INPUT_HEAT_RESISTANCE;                
        private static ForgeConfigSpec.DoubleValue LASER_CRAFTER_ENERGY_BLEED_TO_HEAT;
                        
        private static ForgeConfigSpec.IntValue LASER_PATTERN_CONTROLLER_CAPACITY;                
        private static ForgeConfigSpec.IntValue LASER_PATTERN_CONTROLLER_USE;

        //DS Capsules
        private static ForgeConfigSpec.IntValue SOLAR_CAPSULE_ENERGY_PROVIDED;                        
        private static ForgeConfigSpec.DoubleValue SOLAR_CAPSULE_COMPLETION;
                        
        private static ForgeConfigSpec.IntValue LASER_CAPSULE_ENERGY_CONSUMED;           
        private static ForgeConfigSpec.DoubleValue LASER_CAPSULE_COMPLETION;
        
        //Tools
        private static ForgeConfigSpec.IntValue LASER_CONTROLLER_ITEM_CAPACITY;        
        private static ForgeConfigSpec.IntValue LASER_CONTROLLER_ITEM_CHARGE_RATE;   
        private static ForgeConfigSpec.IntValue LASER_CONTROLLER_ITEM_USAGE;

        //Grappling Hooks
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_HOOK_SMART_ALLOY_COUNT;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_HOOK_SMART_ALLOY_GRAVITY;

        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_HOOK_BLAZE_COUNT_NORMAL;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_HOOK_BLAZE_GRAVITY_NORMAL;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_HOOK_BLAZE_COUNT_NETHER;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_HOOK_BLAZE_GRAVITY_NETHER;

        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_HOOK_WOOD_COUNT;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_HOOK_WOOD_GRAVITY;

        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_HOOK_SLIME_COUNT;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_HOOK_SLIME_GRAVITY;

        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_HOOK_TRIPWIRE_COUNT;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_HOOK_TRIPWIRE_GRAVITY;

        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_STEAM_CAPACITY;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_STEAM_LAUNCH_USAGE;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_STEAM_WINCH_USAGE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ENGINE_STEAM_LAUNCH_FORCE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ENGINE_STEAM_WINCH_FORCE;

        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_ELECTRIC1_CAPACITY;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_ELECTRIC1_TRANSFER;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_ELECTRIC1_LAUNCH_USAGE;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_ELECTRIC1_WINCH_USAGE;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_ELECTRIC1_WINCH_RECUPERATION;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ENGINE_ELECTRIC1_LAUNCH_FORCE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ENGINE_ELECTRIC1_WINCH_FORCE;

        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_ELECTRIC2_CAPACITY;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_ELECTRIC2_TRANSFER;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_ELECTRIC2_LAUNCH_USAGE;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_ELECTRIC2_WINCH_USAGE;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_ELECTRIC2_WINCH_RECUPERATION;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ENGINE_ELECTRIC2_LAUNCH_FORCE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ENGINE_ELECTRIC2_WINCH_FORCE;

        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_MECHANICAL_CAPACITY;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_MECHANICAL_TRANSFER;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_MECHANICAL_LAUNCH_USAGE;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_MECHANICAL_WINCH_USAGE;
        public static ForgeConfigSpec.IntValue GRAPPLING_HOOK_ENGINE_MECHANICAL_WINCH_RECUPERATION;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ENGINE_MECHANICAL_LAUNCH_FORCE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ENGINE_MECHANICAL_WINCH_FORCE;

        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ENGINE_MANUAL_LAUNCH_USAGE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ENGINE_MANUAL_WINCH_USAGE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ENGINE_MANUAL_RAPPEL_USAGE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ENGINE_MANUAL_LAUNCH_FORCE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ENGINE_MANUAL_WINCH_FORCE;

        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ROPE_ENDER_MAX_DISTANCE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ROPE_ENDER_LAUNCH_FORCE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ROPE_ENDER_WINCH_FORCE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ROPE_ENDER_GRAVITY;

        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ROPE_STRING_MAX_DISTANCE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ROPE_STRING_LAUNCH_FORCE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ROPE_STRING_WINCH_FORCE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ROPE_STRING_GRAVITY;

        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ROPE_CHAIN_MAX_DISTANCE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ROPE_CHAIN_LAUNCH_FORCE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ROPE_CHAIN_WINCH_FORCE;
        public static ForgeConfigSpec.DoubleValue GRAPPLING_HOOK_ROPE_CHAIN_GRAVITY;
                
        //Compat
        public static ForgeConfigSpec.DoubleValue MEK_HEAT_EXCHANGE_RATE;
        public static ForgeConfigSpec.DoubleValue MEK_HEAT_RESISTANCE;
                        

        public static ForgeConfigSpec.DoubleValue PNC_HEAT_EXCHANGE_RATE;
                        
        public static ForgeConfigSpec.IntValue PNC_GRAPPLING_HOOK_ENGINE_CAP;    
        public static ForgeConfigSpec.DoubleValue PNC_GRAPPLING_HOOK_ENGINE_MAX_PRESSURE;
        public static ForgeConfigSpec.IntValue PNC_GRAPPLING_HOOK_ENGINE_LAUNCH_USAGE;
        public static ForgeConfigSpec.IntValue PNC_GRAPPLING_HOOK_ENGINE_WINCH_USAGE;
        public static ForgeConfigSpec.DoubleValue PNC_GRAPPLING_HOOK_ENGINE_LAUNCH_FORCE;   
        public static ForgeConfigSpec.DoubleValue PNC_GRAPPLING_HOOK_ENGINE_WINCH_FORCE;
                        

        //####CLIENT####

        public static ForgeConfigSpec.BooleanValue GUI_GRAPPLING_HOOK_ENABLED;
        public static ForgeConfigSpec.BooleanValue GUI_HEAT_OVERLAY_ENABLED;
        public static ForgeConfigSpec.BooleanValue GUI_ORBITAL_LASER_ENABLED;


        private static ForgeConfigSpec SPEC_COMMON;
        private static ForgeConfigSpec SPEC_CLIENT;

        public static ForgeConfigSpec getCommonConfigSpec(){
                if(SPEC_COMMON == null){
                        SPEC_COMMON = generateCommonConfig();
                }
                return SPEC_COMMON;
        }

        public static ForgeConfigSpec getClientConfigSpec(){
                if(SPEC_CLIENT == null){
                        SPEC_CLIENT = generateClientConfig();
                }
                return SPEC_CLIENT;
        }

        protected static ForgeConfigSpec generateCommonConfig(){
                ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

                builder.push("dyson_sphere");
                DYSON_SPHERE_DIM_BLACKLIST = builder
                        .comment("A list of dimensions where the Dyson Sphere should not be accessible. Add dimensions in the format 'minecraft:the_end', 'ad_astra:mercury'. Default: 'minecraft:the_end', 'minecraft:the_nether")
                        .worldRestart()
                        .defineList("dimensionBlacklist", List.of("minecraft:the_end", "minecraft:the_nether"), entry -> entry instanceof String);
                DYSON_SPHERE_IS_WHITELIST = builder
                        .comment("If the dimensionBlacklist should be used as a whitelist instead, Default: false")
                        .worldRestart()
                        .define("dimensionIsWhitelist", false);
                //#####General Start#####
                builder.pop().push("general.heat");
                GENERAL_HEAT_AMBIENT = builder
                        .comment("The ambient temperature all heat handlers get initialized with. 0.0-2000.0. Default: 300.0")
                        .worldRestart()
                        .defineInRange("generalHeatAmbientTemp", 300d, 0d, 2000d);

                builder.pop().push("laser");
                GENERAL_LASER_ENERGY_MULT = builder
                        .comment("The multiplier used to determine the energy per laser (damage*blockDamage*mult=RF) 0.0-10000000.0 Default: 20000.0")
                        .defineInRange("generalLaserEnergyMult", 20_000d, 0d, 10_000_000d);

                //#####Machines Start#####
                builder.pop(2).push("machines.ds_energy_receiver");
                DS_ENERGY_RECEIVER_MAX_HEAT = builder
                        .comment("The upper limit of heat capacity of the dyson sphere energy receiver. 0-1.7976931348623157E308. Default 1700")
                        .worldRestart()
                        .defineInRange("dsEnergyReceiverMaxHeat", 1700, 0, Double.MAX_VALUE);

                builder.pop().push("heat_exchanger");
                HEAT_EXCHANGER_MAX_HEAT = builder
                        .comment("The upper limit of heat capacity of the heat exchanger. 0-1.7976931348623157E308. Default 1700")
                        .worldRestart()
                        .defineInRange("heatExchangerMaxHeat", 1700, 0, Double.MAX_VALUE);
                HEAT_EXCHANGER_FLUID_CAPACITY = builder
                        .comment("The capacity of the internal fluid Tank (input and output) of the heat exchanger. 1-2147483647. Default 4000")
                        .worldRestart()
                        .defineInRange("heatExchangerFluidCapacity", 4000, 1, Integer.MAX_VALUE);

                builder.pop().push("heat_generator");
                HEAT_GENERATOR_MAX_HEAT = builder
                        .comment("The upper limit of heat capacity of the heat generator. 0-1.7976931348623157E308. Default 1700")
                        .worldRestart()
                        .defineInRange("heatGeneratorMaxHeat", 1700, 0, Double.MAX_VALUE);
                HEAT_GENERATOR_ENERGY_CAPACITY = builder
                        .comment("The energy capacity of the heat generator. 1-2147483647. Default 25000")
                        .worldRestart()
                        .defineInRange("heatGeneratorEnergyCapacity", 25000, 1, Integer.MAX_VALUE);
                HEAT_GENERATOR_MIN_HEAT_DIF = builder
                        .comment("The minimum heat difference required to generate RF. Production is per min heat difference")
                        .defineInRange("heatGeneratorMinHeatDif", 25d, 0d, 10000d);
                HEAT_GENERATOR_ENERGY_GENERATED = builder
                        .comment("The amount of RF generated per minimum heat difference between adjacent heat containers. 0-2147483647. Default 1")
                        .defineInRange("heatGeneratorEnergyGenerated", 1, 0, Integer.MAX_VALUE);

                builder.pop().push("heat_pipe");
                HEAT_PIPE_MAX_HEAT = builder
                        .comment("The upper limit of heat capacity of the heat pipe. 0-1.7976931348623157E308. Default 1950")
                        .worldRestart()
                        .defineInRange("heatPipeMaxHeat", 1950d, 0, Double.MAX_VALUE);

                builder.pop().push("railgun");
                RAILGUN_LAUNCH_ENERGY = builder
                        .comment("The base energy required for the railgun to launch a single item from earth. (Changes based on gravity and sun distance with ad astra) 0-2147483647. Default 90000")
                        .defineInRange("railgunLaunchEnergy", 90000, 0, Integer.MAX_VALUE);
                RAILGUN_ENERGY_CAPACITY = builder
                        .comment("The energy capacity of the railgun. Must be bigger then the launchEnergy for the railgun to work. 1-2147483647. Default 150000")
                        .worldRestart()
                        .defineInRange("railgunEnergyCapacity", 150000, 1, Integer.MAX_VALUE);
                
                builder.pop().push("laser_controller_tile");
                LASER_CONTROLLER_TILE_ENERGY_USAGE = builder
                        .comment("The energy used by the Laser Controller Tile per working tick. 0-10000. Default 50")
                        .defineInRange("laserControllerTileEnergyUsage", 50, 0, 10000);
                LASER_CONTROLLER_TILE_ENERGY_CAPACITY = builder
                        .comment("The internal energy capacity of the Laser Controller Tile. 0-2147483647. Default 50")
                        .worldRestart()
                        .defineInRange("laserControllerTileEnergyCapacity", 50000, 0, Integer.MAX_VALUE);
                LASER_CONTROLLER_TILE_ENERGY_COOLDOWN = builder
                        .comment("The time in ticks the Laser Controller Tile needs before it can start working again. 0-10000. Default 100")
                        .defineInRange("laserControllerTileCooldown", 100, 0, 10000);
                LASER_CONTROLLER_TILE_ENERGY_WORKTIME = builder
                        .comment("The time in ticks the Laser Controller Tile needs to finish a work cycle. 0-10000. Default 400")
                        .defineInRange("laserControllerTileWorkTime", 400, 0, 10000);

                builder.pop().push("laser_crafter");
                LASER_CRAFTER_ENERGY_BLEED_STATIC = builder
                        .comment("The static energy loss of the Laser Crafter in RF per tick. 0-2147483647. Default 10000")
                        .defineInRange("laserControllerTileEnergyUsage", 10000, 0, Integer.MAX_VALUE);
                LASER_CRAFTER_ENERGY_BLEED_SCALING = builder
                        .comment("The scaling energy loss of the Laser Crafter. 0 is no loss, 1 is complete loss of all energy. Between scaling and static loss only the higher is applied. 0.0-1.0. Default 0.05")
                        .defineInRange("laserCrafterEnergyBleedScaling", 0.05d, 0d, 1d);
                LASER_CRAFTER_MAX_HEAT = builder
                        .comment("The upper limit of heat capacity of the laser crafter. 0-1.7976931348623157E308. Default 1950")
                        .worldRestart()
                        .defineInRange("laserCrafterMaxHeat", 1700d, 0, Double.MAX_VALUE);
                LASER_CRAFTER_INPUT_HEAT_RESISTANCE = builder
                        .comment("The resistance against new energy based on the current heat. A higher number will increase the energy received at the same heat. 0.0001-10000.0. Default 50.0")
                        .defineInRange("laserCrafterInputHeatResistance", 50d, 0.0001d, 10000d);
                LASER_CRAFTER_ENERGY_BLEED_TO_HEAT = builder
                        .comment("The amount of energy (rf) that needs to dissipate to increase the heat by 1°K. 0.0-10000.0. Default 50.0")
                        .defineInRange("laserCrafterEnergyBleedToHeat", 50d, 0d, 10000d);

                builder.pop().push("laser_pattern_controller");
                LASER_PATTERN_CONTROLLER_CAPACITY = builder
                        .comment("The internal energy capacity of the Laser Pattern Controller. 0-2147483647. Default 5000")
                        .worldRestart()
                        .defineInRange("laserPatternControllerEnergyCapacity", 5000, 0, Integer.MAX_VALUE);
                LASER_PATTERN_CONTROLLER_USE = builder
                        .comment("The amount of energy the Laser Pattern Controller uses per Saved pattern. 0-2147483647. Default 100")
                        .worldRestart()
                        .defineInRange("laserPatternControllerEnergyUsage", 100, 0, Integer.MAX_VALUE);

                //#####DS Capsules Start#####
                builder.pop(2).push("space_capsules.solar");
                SOLAR_CAPSULE_ENERGY_PROVIDED = builder
                        .comment("The energy a single solar capsule provides per tick once added to the dyson sphere. 0-2147483647. Default 10")
                        .defineInRange("solarCapsuleEnergyProvided", 10, 0, Integer.MAX_VALUE);
                SOLAR_CAPSULE_COMPLETION = builder
                        .comment("The dyson sphere completion a single solar capsule adds. 0.0-1.0. Default 0.00001")
                        .defineInRange("solarCapsuleCompletion", 0.00001, 0, 1);

                builder.pop().push("laser");
                LASER_CAPSULE_ENERGY_CONSUMED = builder
                        .comment("The energy a single laser capsule consumes per tick once added to the dyson sphere. 0-2147483647. Default 50")
                        .defineInRange("laserCapsuleEnergyConsumed", 50, 0, Integer.MAX_VALUE);
                LASER_CAPSULE_COMPLETION = builder
                        .comment("The dyson sphere completion a laser solar capsule adds. 0.0-1.0. Default 0.00001")
                        .defineInRange("laserCapsuleCompletion", 0.00001, 0, 1);

                //#####Tools Start#####
                builder.pop(2).push("tools.laser_controller_item");
                LASER_CONTROLLER_ITEM_CAPACITY = builder
                        .comment("The amount of energy that can be stored in the Laser Controller Item. 0-2147483647. Default 50000")
                        .worldRestart()
                        .defineInRange("laserControllerItemEnergyCapacity", 50000, 0, Integer.MAX_VALUE);
                LASER_CONTROLLER_ITEM_CHARGE_RATE = builder
                        .comment("The amount of energy that can inserted into the Laser Controller Item per tick. 0-2147483647. Default 500")
                        .worldRestart()
                        .defineInRange("laserControllerItemChargeRate", 500, 0, Integer.MAX_VALUE);
                LASER_CONTROLLER_ITEM_USAGE = builder
                        .comment("The amount of energy that a single use of the Laser Controller Item consumes. 0-2147483647. Default 100")
                        .worldRestart()
                        .defineInRange("laserControllerItemChargeRate", 100, 0, Integer.MAX_VALUE);

                //#####Grappling Hooks Start#####
                builder.pop(2).push("grappling_hooks.hooks.smart_alloy");
                GRAPPLING_HOOK_HOOK_SMART_ALLOY_COUNT = builder
                        .comment("The amount of hooks that can be deployed at the same time 0-500. Default 3")
                        .worldRestart()
                        .defineInRange("grapplingHookHookSmartAlloyCount", 3, 0, 500);
                GRAPPLING_HOOK_HOOK_SMART_ALLOY_GRAVITY = builder
                        .comment("The gravity (downward acceleration) of the hook. -10.0-10.0. Default: 0.02")
                        .worldRestart()
                        .defineInRange("grapplingHookHookSmartAlloyGravity", 0.02d, -10d, 10d);

                builder.pop().push("blaze");
                GRAPPLING_HOOK_HOOK_BLAZE_COUNT_NORMAL = builder
                        .comment("The amount of hooks that can be deployed at the same time outside the nether 0-500. Default 2")
                        .worldRestart()
                        .defineInRange("grapplingHookHookBlazeCountNormal", 2, 0, 500);
                GRAPPLING_HOOK_HOOK_BLAZE_GRAVITY_NORMAL = builder
                        .comment("The gravity (downward acceleration) of the hook outside the nether. -10.0-10.0. Default: 0.01")
                        .worldRestart()
                        .defineInRange("grapplingHookHookBlazeGravityNormal", 0.01d, -10d, 10d);
                GRAPPLING_HOOK_HOOK_BLAZE_COUNT_NETHER = builder
                        .comment("The amount of hooks that can be deployed at the same time in the nether 0-500. Default 4")
                        .worldRestart()
                        .defineInRange("grapplingHookHookBlazeCountNether", 4, 0, 500);
                GRAPPLING_HOOK_HOOK_BLAZE_GRAVITY_NETHER = builder
                        .comment("The gravity (downward acceleration) of the hook in the nether. -10.0-10.0. Default: 0.0")
                        .worldRestart()
                        .defineInRange("grapplingHookHookBlazeGravityNether", 0.0d, -10d, 10d);

                builder.pop().push("wood");
                GRAPPLING_HOOK_HOOK_WOOD_COUNT = builder
                        .comment("The amount of hooks that can be deployed at the same time outside the nether 0-500. Default 1")
                        .worldRestart()
                        .defineInRange("grapplingHookHookWoodCount", 1, 0, 500);
                GRAPPLING_HOOK_HOOK_WOOD_GRAVITY = builder
                        .comment("The gravity (downward acceleration) of the hook. -10.0-10.0. Default: 0.05")
                        .worldRestart()
                        .defineInRange("grapplingHookHookWoodGravity", 0.05d, -10d, 10d);

                builder.pop().push("slime");
                GRAPPLING_HOOK_HOOK_SLIME_COUNT = builder
                        .comment("The amount of hooks that can be deployed at the same time 0-500. Default 2")
                        .worldRestart()
                        .defineInRange("grapplingHookHookSlimeCount", 2, 0, 500);
                GRAPPLING_HOOK_HOOK_SLIME_GRAVITY = builder
                        .comment("The gravity (downward acceleration) of the hook. -10.0-10.0. Default: 0.05")
                        .worldRestart()
                        .defineInRange("grapplingHookHookSlimeGravity", 0.05d, -10d, 10d);

                builder.pop().push("tripwire");
                GRAPPLING_HOOK_HOOK_TRIPWIRE_COUNT = builder
                        .comment("The amount of hooks that can be deployed at the same time outside the nether 0-500. Default 2")
                        .worldRestart()
                        .defineInRange("grapplingHookHookTripwireCount", 2, 0, 500);
                GRAPPLING_HOOK_HOOK_TRIPWIRE_GRAVITY = builder
                        .comment("The gravity (downward acceleration) of the hook. -10.0-10.0. Default: 0.04")
                        .worldRestart()
                        .defineInRange("grapplingHookHookTripwireGravity", 0.04d, -10d, 10d);

                builder.pop(2).push("engine.steam");
                GRAPPLING_HOOK_ENGINE_STEAM_CAPACITY = builder
                        .comment("The internal storage of the engine in mB. 0-2147483647. Default: 32000")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineSteamCapacity", 32000, 0, Integer.MAX_VALUE);
                GRAPPLING_HOOK_ENGINE_STEAM_LAUNCH_USAGE = builder
                        .comment("The steam usage on hook launch in mB. 0-2147483647. Default: 100")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineSteamLaunchUsage", 100, 0, Integer.MAX_VALUE);
                GRAPPLING_HOOK_ENGINE_STEAM_WINCH_USAGE = builder
                        .comment("The steam usage on active pulling tick in mB. 0-10000. Default: 10")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineSteamWinchUsage", 10, 0, 10000);
                GRAPPLING_HOOK_ENGINE_STEAM_LAUNCH_FORCE = builder
                        .comment("The launch force of the hook. 0.0-20.0. Default: 2.0")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineSteamLaunchForce", 2d, 0d, 20d);
                GRAPPLING_HOOK_ENGINE_STEAM_WINCH_FORCE = builder
                        .comment("The winch force of the hook. 0.0-20.0. Default: 3.5")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineSteamWinchForce", 3.5d, 0d, 20d);

                builder.pop().push("electric1");
                GRAPPLING_HOOK_ENGINE_ELECTRIC1_CAPACITY = builder
                        .comment("The internal RF capacity of the engine. 0-2147483647. Default: 15000")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineElectric1Capacity", 15000, 0, Integer.MAX_VALUE);
                GRAPPLING_HOOK_ENGINE_ELECTRIC1_TRANSFER = builder
                        .comment("The RF transfer/charge rate of the engine. 0-2147483647. Default: 500")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineElectric1ChargeRate", 500, 0, Integer.MAX_VALUE);
                GRAPPLING_HOOK_ENGINE_ELECTRIC1_LAUNCH_USAGE = builder
                        .comment("The RF usage on hook launch. 0-2147483647. Default: 50")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineElectric1LaunchUsage", 50, 0, Integer.MAX_VALUE);
                GRAPPLING_HOOK_ENGINE_ELECTRIC1_WINCH_USAGE = builder
                        .comment("The RF usage on active pulling tick. 0-10000. Default: 5")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineElectric1WinchUsage", 5, 0, 10000);
                GRAPPLING_HOOK_ENGINE_ELECTRIC1_WINCH_RECUPERATION = builder
                        .comment("The RF generated on active rappling tick. 0-10000. Default: 1")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineElectric1WinchRecuperation", 1, 0, 10000);
                GRAPPLING_HOOK_ENGINE_ELECTRIC1_LAUNCH_FORCE = builder
                        .comment("The launch force of the hook. 0.0-20.0. Default: 2.5")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineElectric1LaunchForce", 2.5d, 0d, 20d);
                GRAPPLING_HOOK_ENGINE_ELECTRIC1_WINCH_FORCE = builder
                        .comment("The winch force of the hook. 0.0-20.0. Default: 3.2")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineElectric1WinchForce", 3.2d, 0d, 20d);

                builder.pop().push("electric2");
                GRAPPLING_HOOK_ENGINE_ELECTRIC2_CAPACITY = builder
                        .comment("The internal RF capacity of the engine. 0-2147483647. Default: 1000000")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineElectric2Capacity", 1_000_000, 0, Integer.MAX_VALUE);
                GRAPPLING_HOOK_ENGINE_ELECTRIC2_TRANSFER = builder
                        .comment("The RF transfer/charge rate of the engine. 0-2147483647. Default: 5000")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineElectric2ChargeRate", 5000, 0, Integer.MAX_VALUE);
                GRAPPLING_HOOK_ENGINE_ELECTRIC2_LAUNCH_USAGE = builder
                        .comment("The RF usage on hook launch. 0-2147483647. Default: 50")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineElectric2LaunchUsage", 50, 0, Integer.MAX_VALUE);
                GRAPPLING_HOOK_ENGINE_ELECTRIC2_WINCH_USAGE = builder
                        .comment("The RF usage on active pulling tick. 0-10000. Default: 10")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineElectric2WinchUsage", 10, 0, 10000);
                GRAPPLING_HOOK_ENGINE_ELECTRIC2_WINCH_RECUPERATION = builder
                        .comment("The RF generated on active rappling tick. 0-10000. Default: 5")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineElectric2WinchRecuperation", 5, 0, 10000);
                GRAPPLING_HOOK_ENGINE_ELECTRIC2_LAUNCH_FORCE = builder
                        .comment("The launch force of the hook. 0.0-20.0. Default: 5.0")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineElectric2LaunchForce", 5d, 0d, 20d);
                GRAPPLING_HOOK_ENGINE_ELECTRIC2_WINCH_FORCE = builder
                        .comment("The winch force of the hook. 0.0-20.0. Default: 4.4")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineElectric2WinchForce", 4.4d, 0d, 20d);

                builder.pop().push("mechanical");
                GRAPPLING_HOOK_ENGINE_MECHANICAL_CAPACITY = builder
                        .comment("The internal RF capacity of the engine. 0-2147483647. Default: 20000")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineMechanicalCapacity", 20000, 0, Integer.MAX_VALUE);
                GRAPPLING_HOOK_ENGINE_MECHANICAL_TRANSFER = builder
                        .comment("The RF transfer/charge rate of the engine. 0-2147483647. Default: 5000")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineMechanicalChargeRate", 5000, 0, Integer.MAX_VALUE);
                GRAPPLING_HOOK_ENGINE_MECHANICAL_LAUNCH_USAGE = builder
                        .comment("The RF usage on hook launch. 0-2147483647. Default: 75")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineMechanicalLaunchUsage", 75, 0, Integer.MAX_VALUE);
                GRAPPLING_HOOK_ENGINE_MECHANICAL_WINCH_USAGE = builder
                        .comment("The RF usage on active pulling tick. 0-10000. Default: 1")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineMechanicalWinchUsage", 10, 0, 10000);
                GRAPPLING_HOOK_ENGINE_MECHANICAL_WINCH_RECUPERATION = builder
                        .comment("The RF generated on active rappling tick. 0-10000. Default: 5")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineMechanicalWinchRecuperation", 5, 0, 10000);
                GRAPPLING_HOOK_ENGINE_MECHANICAL_LAUNCH_FORCE = builder
                        .comment("The launch force of the hook. 0.0-20.0. Default: 2.8")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineMechanicalLaunchForce", 2.8d, 0d, 20d);
                GRAPPLING_HOOK_ENGINE_MECHANICAL_WINCH_FORCE = builder
                        .comment("The winch force of the hook. 0.0-20.0. Default: 3.0")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineMechanicalWinchForce", 3d, 0d, 20d);

                builder.pop().push("manual");
                GRAPPLING_HOOK_ENGINE_MANUAL_LAUNCH_USAGE = builder
                        .comment("The exhaustion added on hook launch. 0.0-10. Default: 0.1")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineManualLaunchUsage", 0.1d, 0, 10);
                GRAPPLING_HOOK_ENGINE_MANUAL_WINCH_USAGE = builder
                        .comment("The exhaustion added on active pulling tick. 0.0-10.0 Default: 0.2")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineManualWinchUsage", 0.2d, 0, 10);
                GRAPPLING_HOOK_ENGINE_MANUAL_RAPPEL_USAGE = builder
                        .comment("The exhaustion added on active rappling tick. 0.0-10.0. Default: 0.05")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineManualRappelUsage", 0.05d, 0, 10);
                GRAPPLING_HOOK_ENGINE_MANUAL_LAUNCH_FORCE = builder
                        .comment("The launch force of the hook. 0.0-20.0. Default: 2.0")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineManualLaunchForce", 2.0d, 0d, 20d);
                GRAPPLING_HOOK_ENGINE_MANUAL_WINCH_FORCE = builder
                        .comment("The winch force of the hook. 0.0-20.0. Default: 2.4")
                        .worldRestart()
                        .defineInRange("grapplingHookEngineManualWinchForce", 2.4d, 0d, 20d);

                builder.pop(2).push("rope.ender");
                GRAPPLING_HOOK_ROPE_ENDER_MAX_DISTANCE = builder
                        .comment("The maximum length of the rope in blocks. 0.0-1024.0 Default: 256.0")
                        .worldRestart()
                        .defineInRange("grapplingHookRopeEnderMaxDistance", 256d, 0d, 1024d);
                GRAPPLING_HOOK_ROPE_ENDER_LAUNCH_FORCE = builder
                        .comment("The launch force multiplier of the rope. 0.0-10.0 Default: 0.1")
                        .worldRestart()
                        .defineInRange("grapplingHookRopeEnderLaunchForce", 0.1d, 0d, 10d);
                GRAPPLING_HOOK_ROPE_ENDER_WINCH_FORCE = builder
                        .comment("The winch force multiplier of the rope. 0.0-10.0 Default: 1.0")
                        .worldRestart()
                        .defineInRange("grapplingHookRopeEnderWinchForce", 1d, 0d, 10d);
                GRAPPLING_HOOK_ROPE_ENDER_GRAVITY = builder
                        .comment("The gravity multiplier of the rope. 0.0-10.0 Default: 0.2")
                        .worldRestart()
                        .defineInRange("grapplingHookRopeEnderLaunchForce", 0.2d, 0d, 10d);

                builder.pop().push("string");
                GRAPPLING_HOOK_ROPE_STRING_MAX_DISTANCE = builder
                        .comment("The maximum length of the rope in blocks. 0.0-1024.0 Default: 16.0")
                        .worldRestart()
                        .defineInRange("grapplingHookRopeStringMaxDistance", 16d, 0d, 1024d);
                GRAPPLING_HOOK_ROPE_STRING_LAUNCH_FORCE = builder
                        .comment("The launch force multiplier of the rope. 0.0-10.0 Default: 1.0")
                        .worldRestart()
                        .defineInRange("grapplingHookRopeStringLaunchForce", 1d, 0d, 10d);
                GRAPPLING_HOOK_ROPE_STRING_WINCH_FORCE = builder
                        .comment("The winch force multiplier of the rope. 0.0-10.0 Default: 0.8")
                        .worldRestart()
                        .defineInRange("grapplingHookRopeStringWinchForce", 0.8d, 0d, 10d);
                GRAPPLING_HOOK_ROPE_STRING_GRAVITY = builder
                        .comment("The gravity multiplier of the rope. 0.0-10.0 Default: 1.0")
                        .worldRestart()
                        .defineInRange("grapplingHookRopeStringLaunchForce", 1d, 0d, 10d);

                builder.pop().push("chain");
                GRAPPLING_HOOK_ROPE_CHAIN_MAX_DISTANCE = builder
                        .comment("The maximum length of the rope in blocks. 0.0-1024.0 Default: 32.0")
                        .worldRestart()
                        .defineInRange("grapplingHookRopeChainMaxDistance", 32d, 0d, 1024d);
                GRAPPLING_HOOK_ROPE_CHAIN_LAUNCH_FORCE = builder
                        .comment("The launch force multiplier of the rope. 0.0-10.0 Default: 0.6")
                        .worldRestart()
                        .defineInRange("grapplingHookRopeChainLaunchForce", 0.6d, 0d, 10d);
                GRAPPLING_HOOK_ROPE_CHAIN_WINCH_FORCE = builder
                        .comment("The winch force multiplier of the rope. 0.0-10.0 Default: 1.2")
                        .worldRestart()
                        .defineInRange("grapplingHookRopeChainWinchForce", 1.2d, 0d, 10d);
                GRAPPLING_HOOK_ROPE_CHAIN_GRAVITY = builder
                        .comment("The gravity multiplier of the rope. 0.0-10.0 Default: 1.2")
                        .worldRestart()
                        .defineInRange("grapplingHookRopeChainLaunchForce", 1.2d, 0d, 10d);

                //#####Compat Start#####
                builder.pop(3).push("compat.mekanism");
                MEK_HEAT_EXCHANGE_RATE = builder
                        .comment("Multiplier applied to all heat transfer from Mekanism heat into Dysonsphere heat. 0.0-100.0. Default: 0.01")
                        .defineInRange("mekHeatExchangeRate", 0.01d, 0d, 100d);
                MEK_HEAT_RESISTANCE = builder
                        .comment("Multiplier to scale Dysonsphere thermal resistance to Mekanism thermal resistance. 0.0-1000000.0. Default: 1000.0")
                        .defineInRange("mekHeatResistance", 1000d, 0d, 1000000d);

                builder.pop().push("pneumaticcraft");
                PNC_HEAT_EXCHANGE_RATE = builder
                        .comment("Multiplier applied to all heat transfer from Pneumaticcraft heat into Dysonsphere heat. 0.0-1000.0. Default: 1.0")
                        .defineInRange("pncHeatExchangeRate", 1d, 0d, 1000d);
                
                builder.push("grappling_hook_engine");//still pnc
                PNC_GRAPPLING_HOOK_ENGINE_CAP = builder
                        .comment("The air capacity of the Pneumatic Engine. 0-10000000. Default: 4000")
                        .worldRestart()
                        .defineInRange("pncGrapplingHookEngineAirCapacity", 4000, 0, 10000000);
                PNC_GRAPPLING_HOOK_ENGINE_MAX_PRESSURE = builder
                        .comment("The maximum pressure that can be in the Pneumatic Engine. 1.0-50.0. Default: 10.0")
                        .worldRestart()
                        .defineInRange("pncGrapplingHookEngineMaxPressure", 10d, 1d, 50d);
                PNC_GRAPPLING_HOOK_ENGINE_LAUNCH_USAGE = builder
                        .comment("The amount of air used by the Pneumatic Engine to launch a hook. 10-5000. Default: 60")
                        .worldRestart()
                        .defineInRange("pncGrapplingHookEngineLaunchUse", 60, 10, 5000);
                PNC_GRAPPLING_HOOK_ENGINE_WINCH_USAGE = builder
                        .comment("The amount of air used by the Pneumatic Engine per active 'pulling' tick. 1-1000. Default: 15")
                        .worldRestart()
                        .defineInRange("pncGrapplingHookEngineWinchUse", 15, 1, 1000);
                PNC_GRAPPLING_HOOK_ENGINE_LAUNCH_FORCE = builder
                        .comment("The base launch force of the Pneumatic Engine. 0.0-50.0. Default: 2.5")
                        .worldRestart()
                        .defineInRange("pncGrapplingHookEngineLaunchForce", 2.5d, 0d, 50d);
                PNC_GRAPPLING_HOOK_ENGINE_WINCH_FORCE = builder
                        .comment("The base winch force of the Pneumatic Engine. 0.0-50.0. Default: 3.8")
                        .worldRestart()
                        .defineInRange("pncGrapplingHookEngineWinchForce", 3.8d, 0d, 50d);

                builder.pop();

                return builder.build();
        }

        protected static ForgeConfigSpec generateClientConfig(){
                ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

                builder.push("guis");
                GUI_GRAPPLING_HOOK_ENABLED = builder
                        .comment("Wether the Grappling Hook Gui is visible when wearing a Grappling Hook Harness. Default: true")
                        .define("guiGrapplingHookEnabled", true);
                GUI_HEAT_OVERLAY_ENABLED = builder
                        .comment("Wether the Heat Overly Gui is visible when looking at a heated tile entity. Default: true")
                        .define("guiHeatOverlayEnabled", true);
                GUI_ORBITAL_LASER_ENABLED = builder
                        .comment("Wether the Orbital Laser Gui is visible when wearing a Orbital Laser Controller. Note the Controller is unusable without. Default: true")
                        .define("guiOrbitalLaserEnabled", true);

                return builder.build();
        }

        public static List<String> DYSON_SPHERE_DIM_BLACKLIST_VALUE;
        public static boolean DYSON_SPHERE_IS_WHITELIST_VALUE;

        public static boolean GUI_GRAPPLING_HOOK_ENABLED_VALUE;
        public static boolean GUI_HEAT_OVERLAY_ENABLED_VALUE;
        public static boolean GUI_ORBITAL_LASER_ENABLED_VALUE;


        @SubscribeEvent
        static void onLoad(final ModConfigEvent event) {

                
                if(event.getConfig().getType().equals(Type.COMMON)){
                        DYSON_SPHERE_DIM_BLACKLIST_VALUE = ((List<String>) DYSON_SPHERE_DIM_BLACKLIST.get());
                        DYSON_SPHERE_IS_WHITELIST_VALUE = DYSON_SPHERE_IS_WHITELIST.get();
                        
                        HeatHandler.HEAT_AMBIENT = GENERAL_HEAT_AMBIENT.get();

                        LaserStrikeEntity.ENERGY_MULT = GENERAL_LASER_ENERGY_MULT.get();

                        DSEnergyReceiverTile.maxHeat = DS_ENERGY_RECEIVER_MAX_HEAT.get();

                        HeatExchangerTile.maxHeat = HEAT_EXCHANGER_MAX_HEAT.get();
                        HeatExchangerTile.fluidCapacity = HEAT_EXCHANGER_FLUID_CAPACITY.get();

                        HeatGeneratorTile.maxHeat = HEAT_GENERATOR_MAX_HEAT.get();
                        HeatGeneratorTile.energyCapacity = HEAT_GENERATOR_ENERGY_CAPACITY.get();
                        HeatGeneratorTile.minHeatDifference = HEAT_GENERATOR_MIN_HEAT_DIF.get();
                        HeatGeneratorTile.energyGenerated = HEAT_GENERATOR_ENERGY_GENERATED.get();

                        HeatPipeTile.maxHeat = HEAT_PIPE_MAX_HEAT.get();

                        RailgunTile.baseLaunchEnergy = RAILGUN_LAUNCH_ENERGY.get();
                        RailgunTile.energyCapacity = RAILGUN_ENERGY_CAPACITY.get();

                        LaserControllerTile.energyUsage = LASER_CONTROLLER_TILE_ENERGY_USAGE.get();
                        LaserControllerTile.energyCapacity = LASER_CONTROLLER_TILE_ENERGY_CAPACITY.get();
                        LaserControllerTile.cooldownTime = LASER_CONTROLLER_TILE_ENERGY_COOLDOWN.get();
                        LaserControllerTile.workTime = LASER_CONTROLLER_TILE_ENERGY_WORKTIME.get();

                        LaserCrafterTile.ENERGY_BLEED_STATIC = LASER_CRAFTER_ENERGY_BLEED_STATIC.get();
                        LaserCrafterTile.ENERGY_BLEED_SCALING = LASER_CRAFTER_ENERGY_BLEED_SCALING.get();
                        LaserCrafterTile.MAX_HEAT = LASER_CRAFTER_MAX_HEAT.get();
                        LaserCrafterTile.ENERGY_INPUT_HEAT_RESISTANCE = LASER_CRAFTER_INPUT_HEAT_RESISTANCE.get();
                        LaserCrafterTile.ENERGY_BLEED_TO_HEAT = LASER_CRAFTER_ENERGY_BLEED_TO_HEAT.get();

                        LaserPatternControllerTile.energyCapacity = LASER_PATTERN_CONTROLLER_CAPACITY.get();
                        LaserPatternControllerTile.encodeEnergyUsage = LASER_PATTERN_CONTROLLER_USE.get();

                        LaserControllerItem.capacity = LASER_CONTROLLER_ITEM_CAPACITY.get();
                        LaserControllerItem.maxInput = LASER_CONTROLLER_ITEM_CHARGE_RATE.get();
                        LaserControllerItem.usage = LASER_CONTROLLER_ITEM_USAGE.get();

                        GrapplingHookHookItem.TYPE.SMART_ALLOY.count = GRAPPLING_HOOK_HOOK_SMART_ALLOY_COUNT.get();
                        GrapplingHookHookItem.TYPE.SMART_ALLOY.gravity = GRAPPLING_HOOK_HOOK_SMART_ALLOY_GRAVITY.get().floatValue();

                        GrapplingHookBlazeHookItem.countNormal = GRAPPLING_HOOK_HOOK_BLAZE_COUNT_NORMAL.get();
                        GrapplingHookBlazeHookItem.gravityNormal = GRAPPLING_HOOK_HOOK_BLAZE_GRAVITY_NORMAL.get().floatValue();
                        GrapplingHookBlazeHookItem.countNether = GRAPPLING_HOOK_HOOK_BLAZE_COUNT_NETHER.get();
                        GrapplingHookBlazeHookItem.gravityNether = GRAPPLING_HOOK_HOOK_BLAZE_GRAVITY_NETHER.get().floatValue();

                        GrapplingHookWoodHookItem.count = GRAPPLING_HOOK_HOOK_WOOD_COUNT.get();
                        GrapplingHookWoodHookItem.gravity = GRAPPLING_HOOK_HOOK_WOOD_GRAVITY.get().floatValue();

                        GrapplingHookSlimeHookItem.count = GRAPPLING_HOOK_HOOK_SLIME_COUNT.get();
                        GrapplingHookSlimeHookItem.gravity = GRAPPLING_HOOK_HOOK_SLIME_GRAVITY.get().floatValue();

                        GrapplingHookTripWireHook.count = GRAPPLING_HOOK_HOOK_TRIPWIRE_COUNT.get();
                        GrapplingHookTripWireHook.gravity = GRAPPLING_HOOK_HOOK_TRIPWIRE_GRAVITY.get().floatValue();

                        GrapplingHookSteamEngineItem.CAPACITY = GRAPPLING_HOOK_ENGINE_STEAM_CAPACITY.get();
                        GrapplingHookSteamEngineItem.LAUNCH_USAGE = GRAPPLING_HOOK_ENGINE_STEAM_LAUNCH_USAGE.get();
                        GrapplingHookSteamEngineItem.WINCH_USAGE = GRAPPLING_HOOK_ENGINE_STEAM_WINCH_USAGE.get();
                        GrapplingHookSteamEngineItem.LAUNCH_FORCE = GRAPPLING_HOOK_ENGINE_STEAM_LAUNCH_FORCE.get().floatValue();
                        GrapplingHookSteamEngineItem.WINCH_FORCE = GRAPPLING_HOOK_ENGINE_STEAM_WINCH_FORCE.get().floatValue();

                        GrapplingHookElectricEngineItem.TYPE.E1.CAPACITY = GRAPPLING_HOOK_ENGINE_ELECTRIC1_CAPACITY.get();
                        GrapplingHookElectricEngineItem.TYPE.E1.MAX_TRANSFER = GRAPPLING_HOOK_ENGINE_ELECTRIC1_TRANSFER.get();
                        GrapplingHookElectricEngineItem.TYPE.E1.LAUNCH_USAGE = GRAPPLING_HOOK_ENGINE_ELECTRIC1_LAUNCH_USAGE.get();
                        GrapplingHookElectricEngineItem.TYPE.E1.WINCH_USAGE = GRAPPLING_HOOK_ENGINE_ELECTRIC1_WINCH_USAGE.get();
                        GrapplingHookElectricEngineItem.TYPE.E1.WINCH_RECUPERATION = GRAPPLING_HOOK_ENGINE_ELECTRIC1_WINCH_RECUPERATION.get();
                        GrapplingHookElectricEngineItem.TYPE.E1.LAUNCH_FORCE = GRAPPLING_HOOK_ENGINE_ELECTRIC1_LAUNCH_FORCE.get().floatValue();
                        GrapplingHookElectricEngineItem.TYPE.E1.WINCH_FORCE = GRAPPLING_HOOK_ENGINE_ELECTRIC1_WINCH_FORCE.get().floatValue();

                        GrapplingHookElectricEngineItem.TYPE.E2.CAPACITY = GRAPPLING_HOOK_ENGINE_ELECTRIC2_CAPACITY.get();
                        GrapplingHookElectricEngineItem.TYPE.E2.MAX_TRANSFER = GRAPPLING_HOOK_ENGINE_ELECTRIC2_TRANSFER.get();
                        GrapplingHookElectricEngineItem.TYPE.E2.LAUNCH_USAGE = GRAPPLING_HOOK_ENGINE_ELECTRIC2_LAUNCH_USAGE.get();
                        GrapplingHookElectricEngineItem.TYPE.E2.WINCH_USAGE = GRAPPLING_HOOK_ENGINE_ELECTRIC2_WINCH_USAGE.get();
                        GrapplingHookElectricEngineItem.TYPE.E2.WINCH_RECUPERATION = GRAPPLING_HOOK_ENGINE_ELECTRIC2_WINCH_RECUPERATION.get();
                        GrapplingHookElectricEngineItem.TYPE.E2.LAUNCH_FORCE = GRAPPLING_HOOK_ENGINE_ELECTRIC2_LAUNCH_FORCE.get().floatValue();
                        GrapplingHookElectricEngineItem.TYPE.E2.WINCH_FORCE = GRAPPLING_HOOK_ENGINE_ELECTRIC2_WINCH_FORCE.get().floatValue();

                        GrapplingHookElectricEngineItem.TYPE.MECHANICAL.CAPACITY = GRAPPLING_HOOK_ENGINE_MECHANICAL_CAPACITY.get();
                        GrapplingHookElectricEngineItem.TYPE.MECHANICAL.MAX_TRANSFER = GRAPPLING_HOOK_ENGINE_MECHANICAL_TRANSFER.get();
                        GrapplingHookElectricEngineItem.TYPE.MECHANICAL.LAUNCH_USAGE = GRAPPLING_HOOK_ENGINE_MECHANICAL_LAUNCH_USAGE.get();
                        GrapplingHookElectricEngineItem.TYPE.MECHANICAL.WINCH_USAGE = GRAPPLING_HOOK_ENGINE_MECHANICAL_WINCH_USAGE.get();
                        GrapplingHookElectricEngineItem.TYPE.MECHANICAL.WINCH_RECUPERATION = GRAPPLING_HOOK_ENGINE_MECHANICAL_WINCH_RECUPERATION.get();
                        GrapplingHookElectricEngineItem.TYPE.MECHANICAL.LAUNCH_FORCE = GRAPPLING_HOOK_ENGINE_MECHANICAL_LAUNCH_FORCE.get().floatValue();
                        GrapplingHookElectricEngineItem.TYPE.MECHANICAL.WINCH_FORCE = GRAPPLING_HOOK_ENGINE_MECHANICAL_WINCH_FORCE.get().floatValue();

                        GrapplingHookManualEngineItem.LAUNCH_USAGE = GRAPPLING_HOOK_ENGINE_MANUAL_LAUNCH_USAGE.get().floatValue();
                        GrapplingHookManualEngineItem.WINCH_USAGE = GRAPPLING_HOOK_ENGINE_MANUAL_WINCH_USAGE.get().floatValue();
                        GrapplingHookManualEngineItem.RAPPEL_USAGE = GRAPPLING_HOOK_ENGINE_MANUAL_RAPPEL_USAGE.get().floatValue();
                        GrapplingHookManualEngineItem.LAUNCH_FORCE = GRAPPLING_HOOK_ENGINE_MANUAL_LAUNCH_FORCE.get().floatValue();
                        GrapplingHookManualEngineItem.WINCH_FORCE = GRAPPLING_HOOK_ENGINE_MANUAL_WINCH_FORCE.get().floatValue();

                        GrapplingHookEnderRopeItem.MAX_DISTANCE = GRAPPLING_HOOK_ROPE_ENDER_MAX_DISTANCE.get().floatValue();
                        GrapplingHookEnderRopeItem.LAUNCH_FORCE = GRAPPLING_HOOK_ROPE_ENDER_LAUNCH_FORCE.get().floatValue();
                        GrapplingHookEnderRopeItem.WINCH_FORCE = GRAPPLING_HOOK_ROPE_ENDER_WINCH_FORCE.get().floatValue();
                        GrapplingHookEnderRopeItem.GRAVITY = GRAPPLING_HOOK_ROPE_ENDER_GRAVITY.get().floatValue();

                        GrapplingHookStringRope.MAX_DISTANCE = GRAPPLING_HOOK_ROPE_STRING_MAX_DISTANCE.get().floatValue();
                        GrapplingHookStringRope.LAUNCH_FORCE = GRAPPLING_HOOK_ROPE_STRING_LAUNCH_FORCE.get().floatValue();
                        GrapplingHookStringRope.WINCH_FORCE = GRAPPLING_HOOK_ROPE_STRING_WINCH_FORCE.get().floatValue();
                        GrapplingHookStringRope.GRAVITY = GRAPPLING_HOOK_ROPE_STRING_GRAVITY.get().floatValue();

                        GrapplingHookChainRope.LAUNCH_FORCE = GRAPPLING_HOOK_ROPE_CHAIN_LAUNCH_FORCE.get().floatValue();
                        GrapplingHookChainRope.MAX_DISTANCE = GRAPPLING_HOOK_ROPE_CHAIN_MAX_DISTANCE.get().floatValue();
                        GrapplingHookChainRope.WINCH_FORCE = GRAPPLING_HOOK_ROPE_CHAIN_WINCH_FORCE.get().floatValue();
                        GrapplingHookChainRope.GRAVITY = GRAPPLING_HOOK_ROPE_CHAIN_GRAVITY.get().floatValue();

                        CapsuleItem.TYPE.SOLAR_0.energyProvided = SOLAR_CAPSULE_ENERGY_PROVIDED.get();
                        CapsuleItem.TYPE.SOLAR_0.completionProgress = SOLAR_CAPSULE_COMPLETION.get().floatValue();
                        
                        CapsuleItem.TYPE.LASER_0.energyProvided = -LASER_CAPSULE_ENERGY_CONSUMED.get();
                        CapsuleItem.TYPE.LASER_0.completionProgress = LASER_CAPSULE_COMPLETION.get().floatValue();

                        DysonSphere.LOGGER.info("Common Config loaded!");
                }

                if(event.getConfig().getType().equals(Type.CLIENT)){
                        GUI_GRAPPLING_HOOK_ENABLED_VALUE = GUI_GRAPPLING_HOOK_ENABLED.get();
                        GUI_HEAT_OVERLAY_ENABLED_VALUE = GUI_HEAT_OVERLAY_ENABLED.get();
                        GUI_ORBITAL_LASER_ENABLED_VALUE = GUI_ORBITAL_LASER_ENABLED.get();
                        DysonSphere.LOGGER.info("Client Config loaded!");
                }
                
        }


}
