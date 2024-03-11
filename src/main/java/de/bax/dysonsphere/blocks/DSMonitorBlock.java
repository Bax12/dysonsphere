package de.bax.dysonsphere.blocks;

import java.util.Locale;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.tileentities.DSMonitorTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

public class DSMonitorBlock extends HorizontalDirectionalBlock implements EntityBlock {

public static final VoxelShape Shape_N = Stream.of(Block.box(1,0,2,15,15,9), Block.box(1,0,9,15,9,15), Block.box(1,0,1,2,15,2), Block.box(14,0,1,15,15,2), Block.box(2,0,1,14,1,2), Block.box(2,14,1,14,15,2), Block.box(11,9,12,11.2,13,12.2), Block.box(10.5,13,11.5,11.7,14.2,12.7)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

public static final VoxelShape Shape_E = Stream.of(Block.box(7,0,1,14,15,15), Block.box(1,0,1,7,9,15), Block.box(14,0,1,15,15,2), Block.box(14,0,14,15,15,15), Block.box(14,0,2,15,1,14), Block.box(14,14,2,15,15,14), Block.box(3.8000000000000007,9,11,4,13,11.2), Block.box(3.3000000000000007,13,10.5,4.5,14.2,11.7)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

public static final VoxelShape Shape_S = Stream.of(Block.box(1,0,7,15,15,14), Block.box(1,0,1,15,9,7), Block.box(14,0,14,15,15,15), Block.box(1,0,14,2,15,15), Block.box(2,0,14,14,1,15), Block.box(2,14,14,14,15,15), Block.box(4.800000000000001,9,3.8000000000000007,5,13,4), Block.box(4.300000000000001,13,3.3000000000000007,5.5,14.2,4.5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

public static final VoxelShape Shape_W = Stream.of(Block.box(2,0,1,9,15,15), Block.box(9,0,1,15,9,15), Block.box(1,0,14,2,15,15), Block.box(1,0,1,2,15,2), Block.box(1,0,2,2,1,14), Block.box(1,14,2,2,15,14), Block.box(12,9,4.800000000000001,12.2,13,5), Block.box(11.5,13,4.300000000000001,12.7,14.2,5.5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public DSMonitorBlock() {
        super(ModBlocks.defaultMetal);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(!level.isClientSide){
            level.getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((ds) -> {
                DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                df.setMaximumFractionDigits(30);
                DysonSphere.LOGGER.info("DysonSphere Completion: {}%", df.format(ds.getCompletionPercentage()));
                DysonSphere.LOGGER.info("DysonSphere Energy available: {}", ds.getDysonSphereEnergy());
                DysonSphere.LOGGER.info("DysonSphere unique Part Count: {}", ds.getDysonSphereParts().size());
                // DysonSphere.LOGGER.info("DysonSphere a Part Count: {}", ds.getDysonSphereParts().entrySet().iterator().next().getValue());
            });
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new DSMonitorTile(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type.equals(ModTiles.DS_MONITOR.get()) ? (teLevel, pos, teState, tile) -> {
            ((DSMonitorTile) tile).tick();
        } : null;
    }



}
