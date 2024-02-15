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

    public static final RegistryObject<BlockEntityType<?>> HEAT_PIPE = TILES.register("heat_pipe", () -> new BlockEntityType<HeatPipeTile>(HeatPipeTile::new, Set.of(ModBlocks.HEAT_PIPE_BLOCK.get()), null));

}
