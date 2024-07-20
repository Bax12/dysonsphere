package de.bax.dysonsphere.tileentities;

import java.util.Set;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTiles {
    
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DysonSphere.MODID);

    public static final RegistryObject<BlockEntityType<RailgunTile>> RAILGUN = TILES.register("railgun", () -> new BlockEntityType<RailgunTile>(RailgunTile::new, Set.of(ModBlocks.RAILGUN_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<DSEnergyReceiverTile>> DS_ENERGY_RECEIVER = TILES.register("ds_energy_receiver", () -> new BlockEntityType<DSEnergyReceiverTile>(DSEnergyReceiverTile::new, Set.of(ModBlocks.DS_ENERGY_RECEIVER_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<DSMonitorTile>> DS_MONITOR = TILES.register("ds_monitor", () -> new BlockEntityType<DSMonitorTile>(DSMonitorTile::new, Set.of(ModBlocks.DS_MONITOR_BLOCK.get()), null));

    public static final RegistryObject<BlockEntityType<HeatPipeTile>> HEAT_PIPE = TILES.register("heat_pipe", () -> new BlockEntityType<HeatPipeTile>(HeatPipeTile::new, Set.of(ModBlocks.HEAT_PIPE_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<HeatGeneratorTile>> HEAT_GENERATOR = TILES.register("heat_generator", () -> new BlockEntityType<HeatGeneratorTile>(HeatGeneratorTile::new, Set.of(ModBlocks.HEAT_GENERATOR_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<HeatExchangerTile>> HEAT_EXCHANGER = TILES.register("heat_exchanger", () -> new BlockEntityType<HeatExchangerTile>(HeatExchangerTile::new, Set.of(ModBlocks.HEAT_EXCHANGER_BLOCK.get()), null));

    public static final RegistryObject<BlockEntityType<LaserPatternControllerTile>> LASER_PATTERN_CONTROLLER = TILES.register("laser_pattern_controller", () -> new BlockEntityType<LaserPatternControllerTile>(LaserPatternControllerTile::new, Set.of(ModBlocks.LASER_PATTERN_CONTROLLER_BLOCK.get()), null));
    public static final RegistryObject<BlockEntityType<LaserControllerTile>> LASER_CONTROLLER = TILES.register("laser_controller", () -> new BlockEntityType<LaserControllerTile>(LaserControllerTile::new, Set.of(ModBlocks.LASER_CONTROLLER_BLOCK.get()), null));

}
