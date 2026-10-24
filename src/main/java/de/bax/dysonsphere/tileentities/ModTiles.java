package de.bax.dysonsphere.tileentities;

import java.util.Set;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("null")
public class ModTiles {
    
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DysonSphere.MODID);

    public static final RegistryObject<BlockEntityType<RailgunTile>> RAILGUN = TILES.register("railgun", () -> new BlockEntityType<RailgunTile>(RailgunTile::new, Set.of(ModBlocks.RAILGUN_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<DSEnergyReceiverTile>> DS_ENERGY_RECEIVER = TILES.register("ds_energy_receiver", () -> new BlockEntityType<DSEnergyReceiverTile>(DSEnergyReceiverTile::new, Set.of(ModBlocks.DS_ENERGY_RECEIVER_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<DSMonitorTile>> DS_MONITOR = TILES.register("ds_monitor", () -> new BlockEntityType<DSMonitorTile>(DSMonitorTile::new, Set.of(ModBlocks.DS_MONITOR_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<DSControllerTile>> DS_CONTROLLER = TILES.register("ds_controller", () -> new BlockEntityType<DSControllerTile>(DSControllerTile::new, Set.of(ModBlocks.DS_CONTROLLER_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<CargoReceiverTile>> CARGO_RECEIVER = TILES.register("cargo_receiver", () -> new BlockEntityType<CargoReceiverTile>(CargoReceiverTile::new, Set.of(ModBlocks.CARGO_RECEIVER_BLOCK.get()), null));
    
    public static final RegistryObject<BlockEntityType<ListenerTile>> LISTENER = TILES.register("listener", () -> new BlockEntityType<ListenerTile>(ListenerTile::new, Set.of(ModBlocks.LISTENER_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<EnergyConverterTile>> ENERGY_CONVERTER = TILES.register("energy_converter", () -> new BlockEntityType<EnergyConverterTile>(EnergyConverterTile::new, Set.of(ModBlocks.ENERGY_CONVERTER_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<OreSpireTile>> ORE_SPIRE = TILES.register("ore_spire", () -> new BlockEntityType<OreSpireTile>(OreSpireTile::new, Set.of(ModBlocks.ORE_SPIRE_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<MelterTile.Induction>> INDUCTION_MELTER = TILES.register("induction_melter", () -> new BlockEntityType<MelterTile.Induction>(MelterTile.Induction::new, Set.of(ModBlocks.INDUCTION_MELTER_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<SiliconCrystallizerTile>> SILICON_CRYSTALLIZER = TILES.register("silicon_crystallizer", () -> new BlockEntityType<SiliconCrystallizerTile>(SiliconCrystallizerTile::new, Set.of(ModBlocks.SILICON_CRYSTALLIZER_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<SchematicWorkbenchTile>> SCHEMATIC_WORKBENCH = TILES.register("schematic_workbench", () -> new BlockEntityType<SchematicWorkbenchTile>(SchematicWorkbenchTile::new, Set.of(ModBlocks.SCHEMATIC_WORKBENCH_BLOCK.get()), null));

    public static final RegistryObject<BlockEntityType<HeatPipeTile>> HEAT_PIPE = TILES.register("heat_pipe", () -> new BlockEntityType<HeatPipeTile>(HeatPipeTile::new, Set.of(ModBlocks.HEAT_PIPE_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<HeatGeneratorTile>> HEAT_GENERATOR = TILES.register("heat_generator", () -> new BlockEntityType<HeatGeneratorTile>(HeatGeneratorTile::new, Set.of(ModBlocks.HEAT_GENERATOR_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<HeatExchangerTile>> HEAT_EXCHANGER = TILES.register("heat_exchanger", () -> new BlockEntityType<HeatExchangerTile>(HeatExchangerTile::new, Set.of(ModBlocks.HEAT_EXCHANGER_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<HeatConverterTile>> HEAT_CONVERTER = TILES.register("heat_converter", () -> new BlockEntityType<HeatConverterTile>(HeatConverterTile::new, Set.of(ModBlocks.HEAT_CONVERTER_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<MelterTile>> HEAT_MELTER = TILES.register("heat_melter", () -> new BlockEntityType<MelterTile>(MelterTile::new, Set.of(ModBlocks.HEAT_MELTER_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<HeatingChamberTile>> HEATING_CHAMBER = TILES.register("heating_chamber", () -> new BlockEntityType<HeatingChamberTile>(HeatingChamberTile::new, Set.of(ModBlocks.HEATING_CHAMBER_BLOCK.get()), null));

    public static final RegistryObject<BlockEntityType<LaserPatternControllerTile>> LASER_PATTERN_CONTROLLER = TILES.register("laser_pattern_controller", () -> new BlockEntityType<LaserPatternControllerTile>(LaserPatternControllerTile::new, Set.of(ModBlocks.LASER_PATTERN_CONTROLLER_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<LaserControllerTile>> LASER_CONTROLLER = TILES.register("laser_controller", () -> new BlockEntityType<LaserControllerTile>(LaserControllerTile::new, Set.of(ModBlocks.LASER_CONTROLLER_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<LaserCrafterTile>> LASER_CRAFTER = TILES.register("laser_crafter", () -> new BlockEntityType<LaserCrafterTile>(LaserCrafterTile::new, Set.of(ModBlocks.LASER_CRAFTER_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<LaserRedirectorTile>> LASER_REDIRECTOR = TILES.register("laser_redirector", () -> new BlockEntityType<LaserRedirectorTile>(LaserRedirectorTile::new, Set.of(ModBlocks.LASER_REDIRECTOR_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<LaserRedirectorTile>> LASER_REDIRECTOR_TOGGLE = TILES.register("laser_redirector_toggle", () -> new BlockEntityType<LaserRedirectorTile>(LaserRedirectorTile.Toggle::new, Set.of(ModBlocks.LASER_REDIRECTOR_TOGGLE_BLOCK.get()), null));

    public static final RegistryObject<BlockEntityType<InputHatchTile.Serial>> INPUT_HATCH_SERIAL = TILES.register("input_hatch_serial", () -> new BlockEntityType<InputHatchTile.Serial>((pos, state) -> new InputHatchTile.Serial(pos, state), Set.of(ModBlocks.INPUT_HATCH_SERIAL.get()), null));
    public static final RegistryObject<BlockEntityType<InputHatchTile.Parallel>> INPUT_HATCH_PARALLEL = TILES.register("input_hatch_parallel", () -> new BlockEntityType<InputHatchTile.Parallel>((pos, state) -> new InputHatchTile.Parallel(pos, state), Set.of(ModBlocks.INPUT_HATCH_PARALLEL.get()), null));
    public static final RegistryObject<BlockEntityType<InputHatchTile.SerialHeat>> INPUT_HATCH_SERIAL_HEAT = TILES.register("input_hatch_serial_heat", () -> new BlockEntityType<InputHatchTile.SerialHeat>((pos, state) -> new InputHatchTile.SerialHeat(pos, state), Set.of(ModBlocks.INPUT_HATCH_SERIAL_HEAT.get()), null));
    public static final RegistryObject<BlockEntityType<InputHatchTile.ParallelHeat>> INPUT_HATCH_PARALLEL_HEAT = TILES.register("input_hatch_parallel_heat", () -> new BlockEntityType<InputHatchTile.ParallelHeat>((pos, state) -> new InputHatchTile.ParallelHeat(pos, state), Set.of(ModBlocks.INPUT_HATCH_PARALLEL_HEAT.get()), null));
    public static final RegistryObject<BlockEntityType<InputHatchTile.Proxy>> INPUT_HATCH_PROXY = TILES.register("input_hatch_proxy", () -> new BlockEntityType<InputHatchTile.Proxy>((pos, state) -> new InputHatchTile.Proxy(pos, state), Set.of(ModBlocks.INPUT_HATCH_PROXY.get()), null));
    public static final RegistryObject<BlockEntityType<InputHatchTile.ProxyHeat>> INPUT_HATCH_PROXY_HEAT = TILES.register("input_hatch_proxy_heat", () -> new BlockEntityType<InputHatchTile.ProxyHeat>((pos, state) -> new InputHatchTile.ProxyHeat(pos, state), Set.of(ModBlocks.INPUT_HATCH_PROXY_HEAT.get()), null));
    public static final RegistryObject<BlockEntityType<InputHatchTile.Energy>> INPUT_HATCH_ENERGY = TILES.register("input_hatch_energy", () -> new BlockEntityType<InputHatchTile.Energy>((pos, state) -> new InputHatchTile.Energy(pos, state), Set.of(ModBlocks.INPUT_HATCH_ENERGY.get()), null));
    public static final RegistryObject<BlockEntityType<InputHatchTile.EnergyHeat>> INPUT_HATCH_ENERGY_HEAT = TILES.register("input_hatch_energy_heat", () -> new BlockEntityType<InputHatchTile.EnergyHeat>((pos, state) -> new InputHatchTile.EnergyHeat(pos, state), Set.of(ModBlocks.INPUT_HATCH_ENERGY_HEAT.get()), null));
    public static final RegistryObject<BlockEntityType<InputHatchTile.Fluid>> INPUT_HATCH_FLUID = TILES.register("input_hatch_fluid", () -> new BlockEntityType<InputHatchTile.Fluid>((pos, state) -> new InputHatchTile.Fluid(pos, state), Set.of(ModBlocks.INPUT_HATCH_FLUID.get()), null));
    public static final RegistryObject<BlockEntityType<InputHatchTile.FluidHeat>> INPUT_HATCH_FLUID_HEAT = TILES.register("input_hatch_fluid_heat", () -> new BlockEntityType<InputHatchTile.FluidHeat>((pos, state) -> new InputHatchTile.FluidHeat(pos, state), Set.of(ModBlocks.INPUT_HATCH_FLUID_HEAT.get()), null));
    public static final RegistryObject<BlockEntityType<InputHatchTile.Schematic>> INPUT_HATCH_SCHEMATIC = TILES.register("input_hatch_schematic", () -> new BlockEntityType<InputHatchTile.Schematic>((pos, state) -> new InputHatchTile.Schematic(pos, state), Set.of(ModBlocks.INPUT_HATCH_SCHEMATIC.get()), null));
    public static final RegistryObject<BlockEntityType<InputHatchTile.SchematicHeat>> INPUT_HATCH_SCHEMATIC_HEAT = TILES.register("input_hatch_schematic_heat", () -> new BlockEntityType<InputHatchTile.SchematicHeat>((pos, state) -> new InputHatchTile.SchematicHeat(pos, state), Set.of(ModBlocks.INPUT_HATCH_SCHEMATIC_HEAT.get()), null));
}
