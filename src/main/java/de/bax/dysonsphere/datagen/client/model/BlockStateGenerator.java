package de.bax.dysonsphere.datagen.client.model;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class BlockStateGenerator extends BlockStateProvider{

    public BlockStateGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DysonSphere.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        buildCubeAll(ModBlocks.HEAT_PIPE_BLOCK);

        buildHorizontalBlock(ModBlocks.DS_MONITOR_BLOCK);
        buildHorizontalBlock(ModBlocks.LASER_PATTERN_CONTROLLER_BLOCK);

        buildModel(ModBlocks.RAILGUN_BLOCK);
        buildModel(ModBlocks.DS_ENERGY_RECEIVER_BLOCK);
        

        buildPillarBlock(ModBlocks.HEAT_EXCHANGER_BLOCK);
        buildPillarBlock(ModBlocks.HEAT_GENERATOR_BLOCK);
        
    }
    
    private void buildCubeAll(RegistryObject<Block> block){
        simpleBlock(block.get());
    }

    private void buildModel(RegistryObject<Block> block){
        ResourceLocation name = blockTexture(block.get());
        ModelFile model = new ModelFile.UncheckedModelFile(name);
        simpleBlock(block.get(), model);
    }

    private void buildHorizontalBlock(RegistryObject<Block> block){
        ResourceLocation name = blockTexture(block.get());
        ModelFile model = new ModelFile.UncheckedModelFile(name);
        horizontalBlock(block.get(), model);;
    }

    private void buildPillarBlock(RegistryObject<Block> block){
        String path = blockTexture(block.get()).getPath();
        ResourceLocation side = modLoc(path + "_side");
        ResourceLocation end = modLoc(path + "_end");
        ModelFile model = models().cubeColumn(path, side, end);
        
        simpleBlock(block.get(), model);;
    }





}
