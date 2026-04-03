package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.containers.DSControllerContainer;
import de.bax.dysonsphere.tileentities.DSControllerTile;
import de.bax.dysonsphere.tileentities.ModTiles;
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
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class DSControllerBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final VoxelShape Shape_N = Stream.of(Block.box(0,0,0,16,1,16), Block.box(3,1,5,13,4,15), Block.box(4,4,11,12,11,13)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_E = Stream.of(Block.box(0,0,0,16,1,16), Block.box(1,1,3,11,4,13), Block.box(3,4,4,5,11,12)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_S = Stream.of(Block.box(0,0,0,16,1,16), Block.box(3,1,1,13,4,11), Block.box(4,4,3,12,11,5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_W = Stream.of(Block.box(0,0,0,16,1,16), Block.box(5,1,3,15,4,13), Block.box(11,4,4,13,11,12)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public DSControllerBlock() {
        super(ModBlocks.defaultMetal);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext col) {
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
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        return new DSControllerTile(pPos, pState);
    }

    @Override
    public InteractionResult use(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer, @Nonnull InteractionHand pHand, @Nonnull BlockHitResult pHit) {
        if(!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer){
            BlockEntity tile = pLevel.getBlockEntity(pPos);
            if(tile != null && tile.getType().equals(ModTiles.DS_CONTROLLER.get())){
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                new DSControllerContainer(containerId, playerInventory, (DSControllerTile) tile), 
                Component.translatable("container.dysonsphere.ds_controller")), pPos);
            }

            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType.equals(ModTiles.DS_CONTROLLER.get()) ? (teLevel, pos, teState, tile) -> {
            ((DSControllerTile) tile).tick();
        } : null;
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }
    
}
