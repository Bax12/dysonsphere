package de.bax.dysonsphere;

import de.bax.dysonsphere.items.CapsuleSolarItem;
import de.bax.dysonsphere.tileentities.DSEnergyReceiverTile;
import de.bax.dysonsphere.tileentities.HeatExchangerTile;
import de.bax.dysonsphere.tileentities.HeatGeneratorTile;
import de.bax.dysonsphere.tileentities.HeatPipeTile;
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


        private static final Builder DS_ENERGY_RECEIVER_BUILDER = BUILDER.push("ds_energy_receiver");
        private static final ForgeConfigSpec.DoubleValue DS_ENERGY_RECEIVER_MAX_HEAT = DS_ENERGY_RECEIVER_BUILDER
                        .comment("The upper limit of heat capacity of the dyson sphere energy receiver. 0-1.7976931348623157E308. Default 1700")
                        .worldRestart()
                        .defineInRange("dsEnergyReceiverMaxHeat", 1700, 0, Double.MAX_VALUE);

        private static final Builder HEAT_EXCHANGER_BUILDER = DS_ENERGY_RECEIVER_BUILDER.pop().push("heat_exchanger");
        private static final ForgeConfigSpec.DoubleValue HEAT_EXCHANGER_MAX_HEAT = HEAT_EXCHANGER_BUILDER
                        .comment("The upper limit of heat capacity of the heat exchanger. 0-1.7976931348623157E308. Default 1700")
                        .worldRestart()
                        .defineInRange("heatExchangerMaxHeat", 1700, 0, Double.MAX_VALUE);
        private static final ForgeConfigSpec.DoubleValue HEAT_EXCHANGER_MIN_HEAT = HEAT_EXCHANGER_BUILDER
                        .comment("The lower limit of heat required for the heat exchanger to start working. 0-1.7976931348623157E308. Default 450")
                        .defineInRange("heatExchangerMinHeat", 450, 0, Double.MAX_VALUE);
        private static final ForgeConfigSpec.IntValue HEAT_EXCHANGER_BASE_PRODUCE = HEAT_EXCHANGER_BUILDER
                        .comment("The base amount of water converted to steam per tick (1mB water -> 10mB steam) when working. 0-4000. Default 5")
                        .defineInRange("heatExchangerBaseProduce", 5, 0, 4000);
        private static final ForgeConfigSpec.IntValue HEAT_EXCHANGER_BONUS_PRODUCE = HEAT_EXCHANGER_BUILDER
                        .comment("The bonus amount of water converted to steam per tick (1mB water -> 10mB steam) per bonusHeat over minHeat. 0-4000. Default 1")
                        .defineInRange("heatExchangerBonusProduce", 1, 0, 4000);
        private static final ForgeConfigSpec.DoubleValue HEAT_EXCHANGER_BONUS_HEAT = HEAT_EXCHANGER_BUILDER
                        .comment("The bonus heat required above the minHeat per bonusProduce to be produced. 0-1.7976931348623157E308. Default 50")
                        .defineInRange("heatExchangerBonusHeat", 50, 0, Double.MAX_VALUE);
        private static final ForgeConfigSpec.IntValue HEAT_EXCHANGER_FLUID_CAPACITY = HEAT_EXCHANGER_BUILDER
                        .comment("The capacity of the internal fluid Tank (input and output) of the heat exchanger. 1-2147483647. Default 4000")
                        .worldRestart()
                        .defineInRange("heatExchangerFluidCapacity", 4000, 1, Integer.MAX_VALUE);
        private static final ForgeConfigSpec.DoubleValue HEAT_EXCHANGER_HEAT_CONSUMPTION = HEAT_EXCHANGER_BUILDER
                        .comment("The heat consumption per 1mB of water converted to 10mB of steam. 0-1.7976931348623157E308. Default 2.5")
                        .defineInRange("heatExchangerHeatConsumption", 2.5, 0, Double.MAX_VALUE);

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
                        .defineInRange("", 10, 0, Integer.MAX_VALUE);
        private static final ForgeConfigSpec.DoubleValue SOLAR_CAPSULE_COMPLETION = SOLAR_CAPSULE_BUILDER
                        .comment("The dyson sphere completion a single solar capsule adds. 0.0-1.0. Default 0.00001")
                        .defineInRange("", 0.00001, 0, 1);


        static final ForgeConfigSpec SPEC = BUILDER.build();



        @SubscribeEvent
        static void onLoad(final ModConfigEvent event) {
                DSEnergyReceiverTile.maxHeat = DS_ENERGY_RECEIVER_MAX_HEAT.get();

                HeatExchangerTile.maxHeat = HEAT_EXCHANGER_MAX_HEAT.get();
                HeatExchangerTile.minHeat = HEAT_EXCHANGER_MIN_HEAT.get();
                HeatExchangerTile.baseProduce = HEAT_EXCHANGER_BASE_PRODUCE.get();
                HeatExchangerTile.bonusProduce = HEAT_EXCHANGER_BONUS_PRODUCE.get();
                HeatExchangerTile.bonusHeat = HEAT_EXCHANGER_BONUS_HEAT.get();
                HeatExchangerTile.fluidCapacity = HEAT_EXCHANGER_FLUID_CAPACITY.get();
                HeatExchangerTile.heatConsumption = HEAT_EXCHANGER_HEAT_CONSUMPTION.get().floatValue();

                HeatGeneratorTile.maxHeat = HEAT_GENERATOR_MAX_HEAT.get();
                HeatGeneratorTile.energyCapacity = HEAT_GENERATOR_ENERGY_CAPACITY.get();
                HeatGeneratorTile.minHeatDifference = HEAT_GENERATOR_MIN_HEAT_DIF.get();
                HeatGeneratorTile.energyCapacity = HEAT_GENERATOR_ENERGY_GENERATED.get();

                HeatPipeTile.maxHeat = HEAT_PIPE_MAX_HEAT.get();

                RailgunTile.launchEnergy = RAILGUN_LAUNCH_ENERGY.get();
                RailgunTile.energyCapacity = RAILGUN_ENERGY_CAPACITY.get();

                CapsuleSolarItem.energyProvided = SOLAR_CAPSULE_ENERGY_PROVIDED.get();
                CapsuleSolarItem.completionProgress = SOLAR_CAPSULE_COMPLETION.get().floatValue();
        }
}
