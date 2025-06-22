package de.bax.dysonsphere.datagen.client.model;

import java.util.function.Function;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.InputHatchBlock;
import de.bax.dysonsphere.blocks.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class BlockStateGenerator extends BlockStateProvider{

    public BlockStateGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DysonSphere.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        buildCubeAll(ModBlocks.SMART_ALLOY_BLOCK);
        buildCubeAll(ModBlocks.HEAT_PIPE_BLOCK);

        buildHorizontalBlock(ModBlocks.DS_MONITOR_BLOCK);
        buildHorizontalBlock(ModBlocks.LASER_PATTERN_CONTROLLER_BLOCK);

        buildModel(ModBlocks.RAILGUN_BLOCK);
        buildModel(ModBlocks.DS_ENERGY_RECEIVER_BLOCK);
        buildModel(ModBlocks.LASER_CONTROLLER_BLOCK);
        buildModel(ModBlocks.LASER_CRAFTER_BLOCK);

        buildInputHatchBlock(ModBlocks.INPUT_HATCH_SERIAL);
        buildInputHatchBlock(ModBlocks.INPUT_HATCH_PARALLEL);
        buildInputHatchBlock(ModBlocks.INPUT_HATCH_PROXY);
        buildInputHatchBlock(ModBlocks.INPUT_HATCH_ENERGY);

        buildInputHatchBlock(ModBlocks.INPUT_HATCH_SERIAL_HEAT, blockTexture(ModBlocks.INPUT_HATCH_SERIAL.get()));
        buildInputHatchBlock(ModBlocks.INPUT_HATCH_PARALLEL_HEAT, blockTexture(ModBlocks.INPUT_HATCH_PARALLEL.get()));
        buildInputHatchBlock(ModBlocks.INPUT_HATCH_PROXY_HEAT, blockTexture(ModBlocks.INPUT_HATCH_PROXY.get()));
        buildInputHatchBlock(ModBlocks.INPUT_HATCH_ENERGY_HEAT, blockTexture(ModBlocks.INPUT_HATCH_ENERGY.get()));

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
        horizontalBlock(block.get(), model);
    }

    private void buildPillarBlock(RegistryObject<Block> block){
        String path = blockTexture(block.get()).getPath();
        ResourceLocation side = modLoc(path + "_side");
        ResourceLocation end = modLoc(path + "_end");
        ModelFile model = models().cubeColumn(path, side, end);
        
        simpleBlock(block.get(), model);
    }

    // private void buildDirectionalBlock(RegistryObject<Block> block){
    //     ResourceLocation name = blockTexture(block.get());
    //     ModelFile model = new ModelFile.UncheckedModelFile(name);
    //     directionalBlock(block.get(), model);
    // }

    // private void buildDirectionalBlock(RegistryObject<Block> block, ResourceLocation name){
    //     ModelFile model = new ModelFile.UncheckedModelFile(name);
    //     directionalBlock(block.get(), model);
    // }

    private void buildInputHatchBlock(RegistryObject<Block> block){
        buildInputHatchBlock(block, blockTexture(block.get()), blockTexture(block.get()).withSuffix("_facing"));
    }

    private void buildInputHatchBlock(RegistryObject<Block> block, ResourceLocation name){
        buildInputHatchBlock(block, name, name.withSuffix("_facing"));
    }

    private void buildInputHatchBlock(RegistryObject<Block> block, ResourceLocation nameNeutral, ResourceLocation nameAttached){
        ModelFile model = new ModelFile.UncheckedModelFile(nameNeutral);
        Function<BlockState, ModelFile> modelFunc = $ -> model;
        ModelFile modelAttached = new ModelFile.UncheckedModelFile(nameAttached);
        Function<BlockState, ModelFile> modelAttachedFunc = $ -> modelAttached;
        getVariantBuilder(block.get()).forAllStates(state -> {
            Direction dir = state.getValue(InputHatchBlock.FACING);
            boolean attached = state.getValue(InputHatchBlock.ATTACHED);
            return attached ? 
                ConfiguredModel.builder()
                    .modelFile((modelAttachedFunc).apply(state))
                    .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
                    .rotationY(dir.getAxis().isVertical() ? 0 : ((((int) dir.toYRot())) % 360) - 180)
                    .build()
                : 
                ConfiguredModel.builder()
                    .modelFile((modelFunc).apply(state))
                    .build();
        });

    }





}
