package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import de.bax.dysonsphere.containers.LaserPatternControllerContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class LaserPatternControllerBlock extends HorizontalDirectionalBlock {
    
    public static final VoxelShape Shape_N = Stream.of(Block.box(0,0,0,16,1,16), Block.box(3,1,3,13,4,13), Block.box(5,4,8,11,11,10)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_E = Stream.of(Block.box(0,0,0,16,1,16), Block.box(3,1,3,13,4,13), Block.box(6,4,5,8,11,11)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    
    public static final VoxelShape Shape_S = Stream.of(Block.box(0,0,0,16,1,16), Block.box(3,1,3,13,4,13), Block.box(5,4,6,11,11,8)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    
    public static final VoxelShape Shape_W = Stream.of(Block.box(0,0,0,16,1,16), Block.box(3,1,3,13,4,13), Block.box(8,4,5,10,11,11)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
        
    public LaserPatternControllerBlock() {
        super(ModBlocks.defaultMetal);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!level.isClientSide && player instanceof ServerPlayer serverPlayer){
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                new LaserPatternControllerContainer(containerId, playerInventory), Component.translatable("container.dysonsphere.laser_pattern_controller")), buf -> {
                    buf.writeBlockPos(pos);
                });

            return InteractionResult.CONSUME;
        }
        
        
        return InteractionResult.SUCCESS;
    }

        @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext col) {
        if(state.hasProperty(FACING)){
            switch (state.getValue(FACING)){
                case EAST: return Shape_E;
                case NORTH: return Shape_N;
                case SOUTH: return Shape_S;
                case WEST: return Shape_W;
                default:
                    break;
            }
        }
        return Shape_N;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 1;
    }

}
