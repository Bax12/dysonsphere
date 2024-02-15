package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RailgunBlock extends Block {

    public static final VoxelShape Shape = Stream.of(Block.box(0,0,0,16,15,16), Block.box(4,15,4,12,30,12), Block.box(-6,0.05,-6,4,8,4), Block.box(-9,0,-9,0,4,0), Block.box(-5,0.05,12,4,8,21), Block.box(-9,0,16,0,4,25), Block.box(12,0.05,12,21,8,21), Block.box(16,0,16,25,4,25), Block.box(12,0.1,-5,21,8,4), Block.box(16,0,-9,25,4,0)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public RailgunBlock() {
        super(ModBlocks.defaultMetal);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext col) {
        return Shape;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 1;
    }
    
}
