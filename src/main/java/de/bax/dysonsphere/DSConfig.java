package de.bax.dysonsphere;

import java.util.List;

import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.entities.LaserStrikeEntity;
import de.bax.dysonsphere.items.CapsuleLaserItem;
import de.bax.dysonsphere.items.CapsuleSolarItem;
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
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;


@Mod.EventBusSubscriber(modid = DysonSphere.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DSConfig {
        //this kind of builder chaining makes me feel like I'm doing it wrong. But it works...
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        private static final Builder DYSON_SPHERE_BUILDER = BUILDER.push("dyson_sphere");
        private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DYSON_SPHERE_DIM_BLACKLIST = DYSON_SPHERE_BUILDER
                        .comment("A list of dimensions where the Dyson Sphere should not be accessible. Add dimensions in the format 'minecraft:the_end', 'ad_astra:mercury'. Default: 'minecraft:the_end', 'minecraft:the_nether")
                        .worldRestart()
                        .defineList("dimensionBlacklist", List.of("minecraft:the_end", "minecraft:the_nether"), entry -> entry instanceof String);
        private static final ForgeConfigSpec.BooleanValue DYSON_SPHERE_IS_WHITELIST = DYSON_SPHERE_BUILDER
                        .comment("If the dimensionBlacklist should be used as a whitelist instead, Default: false")
                        .worldRestart()
                        .define("dimensionIsWhitelist", false);


        private static final Builder GENERAL_HEAT_BUILDER = BUILDER.push("general_heat");
        private static final ForgeConfigSpec.DoubleValue GENERAL_HEAT_AMBIENT = GENERAL_HEAT_BUILDER
                        .comment("The ambient temperature all heat handlers get initialized with. 0.0-2000.0. Default: 300.0")
                        .worldRestart()
                        .defineInRange("generalHeatAmbientTemp", 300d, 0d, 2000d);

        private static final Builder GENERAL_LASER_BUILDER = GENERAL_HEAT_BUILDER.pop().push("general_laser");
        private static final ForgeConfigSpec.DoubleValue GENERAL_LASER_ENERGY_MULT = GENERAL_LASER_BUILDER
                        .comment("The multiplier used to determine the energy per laser (damage*blockDamage*mult=RF) 0.0-10000000.0 Default: 20000.0")
                        .defineInRange("generalLaserEnergyMult", 20_000d, 0d, 10_000_000d);


        private static final Builder DS_ENERGY_RECEIVER_BUILDER = GENERAL_HEAT_BUILDER.pop().push("ds_energy_receiver");
        private static final ForgeConfigSpec.DoubleValue DS_ENERGY_RECEIVER_MAX_HEAT = DS_ENERGY_RECEIVER_BUILDER
                        .comment("The upper limit of heat capacity of the dyson sphere energy receiver. 0-1.7976931348623157E308. Default 1700")
                        .worldRestart()
                        .defineInRange("dsEnergyReceiverMaxHeat", 1700, 0, Double.MAX_VALUE);

        private static final Builder HEAT_EXCHANGER_BUILDER = DS_ENERGY_RECEIVER_BUILDER.pop().push("heat_exchanger");
        private static final ForgeConfigSpec.DoubleValue HEAT_EXCHANGER_MAX_HEAT = HEAT_EXCHANGER_BUILDER
                        .comment("The upper limit of heat capacity of the heat exchanger. 0-1.7976931348623157E308. Default 1700")
                        .worldRestart()
                        .defineInRange("heatExchangerMaxHeat", 1700, 0, Double.MAX_VALUE);
        // private static final ForgeConfigSpec.DoubleValue HEAT_EXCHANGER_MIN_HEAT = HEAT_EXCHANGER_BUILDER
        //                 .comment("The lower limit of heat required for the heat exchanger to start working. 0-1.7976931348623157E308. Default 450")
        //                 .defineInRange("heatExchangerMinHeat", 450, 0, Double.MAX_VALUE);
        // private static final ForgeConfigSpec.IntValue HEAT_EXCHANGER_BASE_PRODUCE = HEAT_EXCHANGER_BUILDER
        //                 .comment("The base amount of water converted to steam per tick (1mB water -> 10mB steam) when working. 0-4000. Default 5")
        //                 .defineInRange("heatExchangerBaseProduce", 5, 0, 4000);
        // private static final ForgeConfigSpec.IntValue HEAT_EXCHANGER_BONUS_PRODUCE = HEAT_EXCHANGER_BUILDER
        //                 .comment("The bonus amount of water converted to steam per tick (1mB water -> 10mB steam) per bonusHeat over minHeat. 0-4000. Default 1")
        //                 .defineInRange("heatExchangerBonusProduce", 1, 0, 4000);
        // private static final ForgeConfigSpec.DoubleValue HEAT_EXCHANGER_BONUS_HEAT = HEAT_EXCHANGER_BUILDER
        //                 .comment("The bonus heat required above the minHeat per bonusProduce to be produced. 0-1.7976931348623157E308. Default 50")
        //                 .defineInRange("heatExchangerBonusHeat", 50, 0, Double.MAX_VALUE);
        private static final ForgeConfigSpec.IntValue HEAT_EXCHANGER_FLUID_CAPACITY = HEAT_EXCHANGER_BUILDER
                        .comment("The capacity of the internal fluid Tank (input and output) of the heat exchanger. 1-2147483647. Default 4000")
                        .worldRestart()
                        .defineInRange("heatExchangerFluidCapacity", 4000, 1, Integer.MAX_VALUE);
        // private static final ForgeConfigSpec.DoubleValue HEAT_EXCHANGER_HEAT_CONSUMPTION = HEAT_EXCHANGER_BUILDER
        //                 .comment("The heat consumption per 1mB of water converted to 10mB of steam. 0-1.7976931348623157E308. Default 2.5")
        //                 .defineInRange("heatExchangerHeatConsumption", 2.5, 0, Double.MAX_VALUE);

        private static final Builder HEAT_GENERATOR_BUILDER = HEAT_EXCHANGER_BUILDER.pop().push("heat_generator");
        private static final ForgeConfigSpec.DoubleValue HEAT_GENERATOR_MAX_HEAT = HEAT_GENERATOR_BUILDER
                        .comment("The upper limit of heat capacity of the heat generator. 0-1.7976931348623157E308. Default 1700")
                        .worldRestart()
                        .defineInRange("heatGeneratorMaxHeat", 1700, 0, Double.MAX_VALUE);
        private static final ForgeConfigSpec.IntValue HEAT_GENERATOR_ENERGY_CAPACITY = HEAT_GENERATOR_BUILDER
                        .comment("The energy capacity of the heat generator. 1-2147483647. Default 25000")
                        .worldRestart()
                        .defineInRange("heatGeneratorEnergyCapacity", 25000, 1, Integer.MAX_VALUE);
        private static final ForgeConfigSpec.DoubleValue HEAT_GENERATOR_MIN_HEAT_DIF = HEAT_GENERATOR_BUILDER
                        .comment("The minimum heat difference required to generate RF. Production is per min heat difference")
                        .defineInRange("heatGeneratorMinHeatDif", 25d, 0d, 10000d);
        private static final ForgeConfigSpec.IntValue HEAT_GENERATOR_ENERGY_GENERATED = HEAT_GENERATOR_BUILDER
                        .comment("The amount of RF generated per minimum heat difference between adjacent heat containers. 0-2147483647. Default 1")
                        .defineInRange("heatGeneratorEnergyGenerated", 1, 0, Integer.MAX_VALUE);

        private static final Builder HEAT_PIPE_BUILDER = HEAT_GENERATOR_BUILDER.pop().push("heat_pipe");
        private static final ForgeConfigSpec.DoubleValue HEAT_PIPE_MAX_HEAT = HEAT_PIPE_BUILDER
                        .comment("The upper limit of heat capacity of the heat pipe. 0-1.7976931348623157E308. Default 1950")
                        .worldRestart()
                        .defineInRange("heatPipeMaxHeat", 1950d, 0, Double.MAX_VALUE);
        
        private static final Builder RAILGUN_BUILDER = HEAT_PIPE_BUILDER.pop().push("railgun");
        private static final ForgeConfigSpec.IntValue RAILGUN_LAUNCH_ENERGY = RAILGUN_BUILDER
                        .comment("The energy required for the railgun to launch a single item. 0-2147483647. Default 90000")
                        .defineInRange("railgunLaunchEnergy", 90000, 0, Integer.MAX_VALUE);
        private static final ForgeConfigSpec.IntValue RAILGUN_ENERGY_CAPACITY = RAILGUN_BUILDER
                        .comment("The energy capacity of the railgun. Must be bigger then the launchEnergy for the railgun to work. 1-2147483647. Default 150000")
                        .worldRestart()
                        .defineInRange("railgunEnergyCapacity", 150000, 1, Integer.MAX_VALUE);

        private static final Builder SOLAR_CAPSULE_BUILDER = RAILGUN_BUILDER.pop().push("solar_capsule");
        private static final ForgeConfigSpec.IntValue SOLAR_CAPSULE_ENERGY_PROVIDED = SOLAR_CAPSULE_BUILDER
                        .comment("The energy a single solar capsule provides per tick once added to the dyson sphere. 0-2147483647. Default 10")
                        .defineInRange("solarCapsuleEnergyProvided", 10, 0, Integer.MAX_VALUE);
        private static final ForgeConfigSpec.DoubleValue SOLAR_CAPSULE_COMPLETION = SOLAR_CAPSULE_BUILDER
                        .comment("The dyson sphere completion a single solar capsule adds. 0.0-1.0. Default 0.00001")
                        .defineInRange("solarCapsuleCompletion", 0.00001, 0, 1);

        private static final Builder LASER_CAPSULE_BUILDER = SOLAR_CAPSULE_BUILDER.pop().push("laser_capsule");
        private static final ForgeConfigSpec.IntValue LASER_CAPSULE_ENERGY_CONSUMED = LASER_CAPSULE_BUILDER
                        .comment("The energy a single laser capsule consumes per tick once added to the dyson sphere. 0-2147483647. Default 50")
                        .defineInRange("laserCapsuleEnergyConsumed", 50, 0, Integer.MAX_VALUE);
        private static final ForgeConfigSpec.DoubleValue LASER_CAPSULE_COMPLETION = LASER_CAPSULE_BUILDER
                        .comment("The dyson sphere completion a laser solar capsule adds. 0.0-1.0. Default 0.00001")
                        .defineInRange("laserCapsuleCompletion", 0.00001, 0, 1);

        private static final Builder LASER_CONTROLLER_TILE_BUILDER = LASER_CAPSULE_BUILDER.pop().push("laser_controller_tile");
        private static final ForgeConfigSpec.IntValue LASER_CONTROLLER_TILE_ENERGY_USAGE = LASER_CONTROLLER_TILE_BUILDER
                        .comment("The energy used by the Laser Controller Tile per working tick. 0-10000. Default 50")
                        .defineInRange("laserControllerTileEnergyUsage", 50, 0, 10000);
        private static final ForgeConfigSpec.IntValue LASER_CONTROLLER_TILE_ENERGY_CAPACITY = LASER_CONTROLLER_TILE_BUILDER
                        .comment("The internal energy capacity of the Laser Controller Tile. 0-2147483647. Default 50")
                        .worldRestart()
                        .defineInRange("laserControllerTileEnergyCapacity", 50000, 0, Integer.MAX_VALUE);
        private static final ForgeConfigSpec.IntValue LASER_CONTROLLER_TILE_ENERGY_COOLDOWN = LASER_CONTROLLER_TILE_BUILDER
                        .comment("The time in ticks the Laser Controller Tile needs before it can start working again. 0-10000. Default 100")
                        .defineInRange("laserControllerTileCooldown", 100, 0, 10000);
        private static final ForgeConfigSpec.IntValue LASER_CONTROLLER_TILE_ENERGY_WORKTIME = LASER_CONTROLLER_TILE_BUILDER
                        .comment("The time in ticks the Laser Controller Tile needs to finish a work cycle. 0-10000. Default 400")
                        .defineInRange("laserControllerTileWorkTime", 400, 0, 10000);

        private static final Builder LASER_CRAFTER_BUILDER = LASER_CONTROLLER_TILE_BUILDER.pop().push("laser_crafter");
        private static final ForgeConfigSpec.IntValue LASER_CRAFTER_ENERGY_BLEED_STATIC = LASER_CRAFTER_BUILDER
                        .comment("The static energy loss of the Laser Crafter in RF per tick. 0-2147483647. Default 10000")
                        .defineInRange("laserControllerTileEnergyUsage", 10000, 0, Integer.MAX_VALUE);
        private static final ForgeConfigSpec.DoubleValue LASER_CRAFTER_ENERGY_BLEED_SCALING = LASER_CRAFTER_BUILDER
                        .comment("The scaling energy loss of the Laser Crafter. 0 is no loss, 1 is complete loss of all energy. Between scaling and static loss only the higher is applied. 0.0-1.0. Default 0.05")
                        .defineInRange("laserCrafterEnergyBleedScaling", 0.05d, 0d, 1d);
        private static final ForgeConfigSpec.DoubleValue LASER_CRAFTER_MAX_HEAT = LASER_CRAFTER_BUILDER
                        .comment("The upper limit of heat capacity of the laser crafter. 0-1.7976931348623157E308. Default 1950")
                        .worldRestart()
                        .defineInRange("laserCrafterMaxHeat", 1700d, 0, Double.MAX_VALUE);
        private static final ForgeConfigSpec.DoubleValue LASER_CRAFTER_INPUT_HEAT_RESISTANCE = LASER_CRAFTER_BUILDER
                        .comment("The resistance against new energy based on the current heat. A higher number will increase the energy received at the same heat. 0.0001-10000.0. Default 50.0")
                        .defineInRange("laserCrafterInputHeatResistance", 50d, 0.0001d, 10000d);
        private static final ForgeConfigSpec.DoubleValue LASER_CRAFTER_ENERGY_BLEED_TO_HEAT = LASER_CRAFTER_BUILDER
                        .comment("The amount of energy (rf) that needs to dissipate to increase the heat by 1°K. 0.0-10000.0. Default 50.0")
                        .defineInRange("laserCrafterEnergyBleedToHeat", 50d, 0d, 10000d);
        
        private static final Builder LASER_PATTERN_CONTROLLER_BUILDER = LASER_CRAFTER_BUILDER.pop().push("laser_pattern_controller");
        private static final ForgeConfigSpec.IntValue LASER_PATTERN_CONTROLLER_CAPACITY = LASER_PATTERN_CONTROLLER_BUILDER
                        .comment("The internal energy capacity of the Laser Pattern Controller. 0-2147483647. Default 5000")
                        .worldRestart()
                        .defineInRange("laserPatternControllerEnergyCapacity", 5000, 0, Integer.MAX_VALUE);
        private static final ForgeConfigSpec.IntValue LASER_PATTERN_CONTROLLER_USE = LASER_PATTERN_CONTROLLER_BUILDER
                        .comment("The amount of energy the Laser Pattern Controller uses per Saved pattern. 0-2147483647. Default 100")
                        .worldRestart()
                        .defineInRange("laserPatternControllerEnergyUsage", 100, 0, Integer.MAX_VALUE);

        private static final Builder LASER_CONTROLLER_ITEM_BUILDER = LASER_CRAFTER_BUILDER.pop().push("laser_controller_item");
        private static final ForgeConfigSpec.IntValue LASER_CONTROLLER_ITEM_CAPACITY = LASER_CONTROLLER_ITEM_BUILDER
                        .comment("The amount of energy that can be stored in the Laser Controller Item. 0-2147483647. Default 50000")
                        .worldRestart()
                        .defineInRange("laserControllerItemEnergyCapacity", 50000, 0, Integer.MAX_VALUE);
        private static final ForgeConfigSpec.IntValue LASER_CONTROLLER_ITEM_CHARGE_RATE = LASER_CONTROLLER_ITEM_BUILDER
                        .comment("The amount of energy that can inserted into the Laser Controller Item per tick. 0-2147483647. Default 500")
                        .worldRestart()
                        .defineInRange("laserControllerItemChargeRate", 500, 0, Integer.MAX_VALUE);
        private static final ForgeConfigSpec.IntValue LASER_CONTROLLER_ITEM_USAGE = LASER_CONTROLLER_ITEM_BUILDER
                        .comment("The amount of energy that a single use of the Laser Controller Item consumes. 0-2147483647. Default 100")
                        .worldRestart()
                        .defineInRange("laserControllerItemChargeRate", 100, 0, Integer.MAX_VALUE);

        private static final Builder COMPAT_BUILDER = LASER_CONTROLLER_ITEM_BUILDER.pop().push("compat");
        private static final Builder MEK_COMPAT_BUILDER = COMPAT_BUILDER.push("mekanism");
        public static final ForgeConfigSpec.DoubleValue MEK_HEAT_EXCHANGE_RATE = MEK_COMPAT_BUILDER
                        .comment("Multiplier applied to all heat transfer from Mekanism heat into Dysonsphere heat. 0.0-100.0. Default: 0.01")
                        .defineInRange("mekHeatExchangeRate", 0.01d, 0d, 100d);
                        public static final ForgeConfigSpec.DoubleValue MEK_HEAT_RESISTANCE = MEK_COMPAT_BUILDER
                        .comment("Multiplier to scale Dysonsphere thermal resistance to Mekanism thermal resistance. 0.0-1000000.0. Default: 1000.0")
                        .defineInRange("mekHeatResistance", 1000d, 0d, 1000000d);

        private static final Builder PNC_COMPAT_BUILDER = MEK_COMPAT_BUILDER.pop().push("pneumaticcraft");
        public static final ForgeConfigSpec.DoubleValue PNC_HEAT_EXCHAGE_RATE = PNC_COMPAT_BUILDER
                        .comment("Multiplier applied to all heat transfer from Pneumaticcraft heat into Dysonsphere heat. 0.0-1000.0. Default: 1.0")
                        .defineInRange("pncHeatExchangeRate", 1d, 0d, 1000d);


        static final ForgeConfigSpec SPEC = BUILDER.build();


        public static List<String> DYSON_SPHERE_DIM_BLACKLIST_VALUE;
        public static boolean DYSON_SPHERE_IS_WHITELIST_VALUE;


        @SubscribeEvent
        static void onLoad(final ModConfigEvent event) {
                
                DYSON_SPHERE_DIM_BLACKLIST_VALUE = ((List<String>) DYSON_SPHERE_DIM_BLACKLIST.get());
                DYSON_SPHERE_IS_WHITELIST_VALUE = DYSON_SPHERE_IS_WHITELIST.get();
                
                HeatHandler.HEAT_AMBIENT = GENERAL_HEAT_AMBIENT.get();

                LaserStrikeEntity.ENERGY_MULT = GENERAL_LASER_ENERGY_MULT.get();

                DSEnergyReceiverTile.maxHeat = DS_ENERGY_RECEIVER_MAX_HEAT.get();

                HeatExchangerTile.maxHeat = HEAT_EXCHANGER_MAX_HEAT.get();
                // HeatExchangerTile.minHeat = HEAT_EXCHANGER_MIN_HEAT.get();
                // HeatExchangerTile.baseProduce = HEAT_EXCHANGER_BASE_PRODUCE.get();
                // HeatExchangerTile.bonusProduce = HEAT_EXCHANGER_BONUS_PRODUCE.get();
                // HeatExchangerTile.bonusHeat = HEAT_EXCHANGER_BONUS_HEAT.get();
                HeatExchangerTile.fluidCapacity = HEAT_EXCHANGER_FLUID_CAPACITY.get();
                // HeatExchangerTile.heatConsumption = HEAT_EXCHANGER_HEAT_CONSUMPTION.get().floatValue();

                HeatGeneratorTile.maxHeat = HEAT_GENERATOR_MAX_HEAT.get();
                HeatGeneratorTile.energyCapacity = HEAT_GENERATOR_ENERGY_CAPACITY.get();
                HeatGeneratorTile.minHeatDifference = HEAT_GENERATOR_MIN_HEAT_DIF.get();
                HeatGeneratorTile.energyGenerated = HEAT_GENERATOR_ENERGY_GENERATED.get();

                HeatPipeTile.maxHeat = HEAT_PIPE_MAX_HEAT.get();

                RailgunTile.launchEnergy = RAILGUN_LAUNCH_ENERGY.get();
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

                CapsuleSolarItem.energyProvided = SOLAR_CAPSULE_ENERGY_PROVIDED.get();
                CapsuleSolarItem.completionProgress = SOLAR_CAPSULE_COMPLETION.get().floatValue();
                
                CapsuleLaserItem.energyProvided = -LASER_CAPSULE_ENERGY_CONSUMED.get();
                CapsuleLaserItem.completionProgress = LASER_CAPSULE_COMPLETION.get().floatValue();

                DysonSphere.LOGGER.info("Config loaded!");
        }
}
