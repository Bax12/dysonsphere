package de.bax.dysonsphere.datagen.server;

import java.util.Collections;
import java.util.List;

import de.bax.dysonsphere.blocks.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class BlockLootGenerator extends LootTableProvider {


    public BlockLootGenerator(PackOutput output) {
        super(output, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(() -> new BlockLootSubGen(), LootContextParamSets.BLOCK)));
    }


    public static class BlockLootSubGen extends BlockLootSubProvider {

        public BlockLootSubGen() {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        public void generate() {
            ModBlocks.BLOCKS.getEntries().stream().forEach(b -> map.put(b.get().getLootTable(), createSingleItemTable(b.get().asItem())));
            
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ModBlocks.BLOCKS.getEntries().stream().flatMap(b -> b.stream())::iterator;
        }

        
    }
    
    
    
}
