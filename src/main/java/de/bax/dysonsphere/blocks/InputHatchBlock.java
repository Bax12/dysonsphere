package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.color.ModColors.ITintableTile;
import de.bax.dysonsphere.color.ModColors.ITintableTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class InputHatchBlock extends Block implements EntityBlock, ITintableTileBlock {

    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public static final VoxelShape ShapeNeutral = Stream.of(Block.box(1,1,1,15,15,15), Block.box(0,0,0,2,2,2), Block.box(0,14,14,2,16,16), Block.box(0,0,14,2,2,16), Block.box(0,14,0,2,16,2), Block.box(14,0,14,16,2,16), Block.box(14,14,0,16,16,2), Block.box(14,0,0,16,2,2), Block.box(14,14,14,16,16,16), Block.box(2,0,0,14,1,1), Block.box(2,15,0,14,16,1), Block.box(2,15,15,14,16,16), Block.box(2,0,15,14,1,16), Block.box(0,0,2,1,1,14), Block.box(0,15,2,1,16,14), Block.box(15,15,2,16,16,14), Block.box(15,0,2,16,1,14), Block.box(15,2,0,16,14,1), Block.box(0,2,0,1,14,1), Block.box(0,2,15,1,14,16), Block.box(15,2,15,16,14,16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_U = Stream.of(Block.box(1,1,1,15,16,15), Block.box(0,14,0,2,15.975,2), Block.box(0,0,14,2,2,16), Block.box(0,0,0,2,2,2), Block.box(0,14,14,2,15.975,16), Block.box(14,0,0,16,2,2), Block.box(14,14,14,16,15.975,16), Block.box(14,14,0,16,15.975,2), Block.box(14,0,14,16,2,16), Block.box(2,0,15,14,1,16), Block.box(2,0,0,14,1,1), Block.box(0,2,0,1,14,1), Block.box(0,2,15,1,14,16), Block.box(15,2,15,16,14,16), Block.box(15,2,0,16,14,1), Block.box(0,0,2,1,1,14), Block.box(15,0,2,16,1,14)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_N = Stream.of(Block.box(1,1,0,15,15,15), Block.box(0,0,0.025,2,2,2), Block.box(0,14,14,2,16,16), Block.box(0,0,14,2,2,16), Block.box(0,14,0.025,2,16,2), Block.box(14,0,14,16,2,16), Block.box(14,14,0.025,16,16,2), Block.box(14,0,0.025,16,2,2), Block.box(14,14,14,16,16,16), Block.box(2,15,15,14,16,16), Block.box(2,0,15,14,1,16), Block.box(0,0,2,1,1,14), Block.box(0,15,2,1,16,14), Block.box(15,15,2,16,16,14), Block.box(15,0,2,16,1,14), Block.box(0,2,15,1,14,16), Block.box(15,2,15,16,14,16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_E = Stream.of(Block.box(1,1,1,16,15,15), Block.box(14,0,0,15.975,2,2), Block.box(0,14,0,2,16,2), Block.box(0,0,0,2,2,2), Block.box(14,14,0,15.975,16,2), Block.box(0,0,14,2,2,16), Block.box(14,14,14,15.975,16,16), Block.box(14,0,14,15.975,2,16), Block.box(0,14,14,2,16,16), Block.box(0,15,2,1,16,14), Block.box(0,0,2,1,1,14), Block.box(2,0,0,14,1,1), Block.box(2,15,0,14,16,1), Block.box(2,15,15,14,16,16), Block.box(2,0,15,14,1,16), Block.box(0,2,0,1,14,1), Block.box(0,2,15,1,14,16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    
    public static final VoxelShape Shape_S = Stream.of(Block.box(1,1,1,15,15,16), Block.box(14,0,14,16,2,15.975), Block.box(14,14,0,16,16,2), Block.box(14,0,0,16,2,2), Block.box(14,14,14,16,16,15.975), Block.box(0,0,0,2,2,2), Block.box(0,14,14,2,16,15.975), Block.box(0,0,14,2,2,15.975), Block.box(0,14,0,2,16,2), Block.box(2,15,0,14,16,1), Block.box(2,0,0,14,1,1), Block.box(15,0,2,16,1,14), Block.box(15,15,2,16,16,14), Block.box(0,15,2,1,16,14), Block.box(0,0,2,1,1,14), Block.box(15,2,0,16,14,1), Block.box(0,2,0,1,14,1)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    
    public static final VoxelShape Shape_W = Stream.of(Block.box(0,1,1,15,15,15), Block.box(0.025000000000000355,0,14,2,2,16), Block.box(14,14,14,16,16,16), Block.box(14,0,14,16,2,16), Block.box(0.025000000000000355,14,14,2,16,16), Block.box(14,0,0,16,2,2), Block.box(0.025000000000000355,14,0,2,16,2), Block.box(0.025000000000000355,0,0,2,2,2), Block.box(14,14,0,16,16,2), Block.box(15,15,2,16,16,14), Block.box(15,0,2,16,1,14), Block.box(2,0,15,14,1,16), Block.box(2,15,15,14,16,16), Block.box(2,15,0,14,16,1), Block.box(2,0,0,14,1,1), Block.box(15,2,15,16,14,16), Block.box(15,2,0,16,14,1)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_D = Stream.of(Block.box(1,0,1,15,15,15), Block.box(0,0.025,14,2,2,16), Block.box(0,14,0,2,16,2), Block.box(0,14,14,2,16,16), Block.box(0,0.025,0,2,2,2), Block.box(14,14,14,16,16,16), Block.box(14,0.025,0,16,2,2), Block.box(14,0.025,14,16,2,16), Block.box(14,14,0,16,16,2), Block.box(2,15,0,14,16,1), Block.box(2,15,15,14,16,16), Block.box(0,2,15,1,14,16), Block.box(0,2,0,1,14,1), Block.box(15,2,0,16,14,1), Block.box(15,2,15,16,14,16), Block.box(0,15,2,1,16,14), Block.box(15,15,2,16,16,14)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public enum TYPE {
        SERIAL(true, false),
        PARALLEL(false, false),
        SERIAL_HEAT(true, true),
        PARALLEL_HEAT(false, true);

        public boolean isSerial;
        public boolean isHeatConducting;

        TYPE(boolean serial, boolean heatConduction){
            this.isSerial = serial;
            this.isHeatConducting = heatConduction;
        }
    }

    public final TYPE type;

    public InputHatchBlock(TYPE type) {
        super(ModBlocks.defaultMetal);
        this.type = type;
        registerDefaultState(defaultBlockState().setValue(ATTACHED, false).setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ATTACHED, FACING);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        switch (type) {
            case SERIAL:
            case SERIAL_HEAT:
                // return new foo(isHeatConduction);
                break;
            case PARALLEL:
            case PARALLEL_HEAT:
                // return new foo(isHeatConduction);
                break;
        }
        return null;
    }
    
    @Override
    public InteractionResult use(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer, @Nonnull InteractionHand pHand, @Nonnull BlockHitResult pHit) {
        if(!pLevel.isClientSide){
            pState = pState.cycle(ATTACHED);
            pLevel.setBlock(pPos, pState, 3);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        
    }

    @Override
    public void onRemove(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pNewState, boolean pMovedByPiston) {
        
    }

    @Override
    public boolean hasAnalogOutputSignal(@Nonnull BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos) {
        return 0;
    }

    @Override
    public int getTintColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if(level != null && level.getBlockEntity(pos) instanceof ITintableTile tile){
            return tile.getTintColor(tintIndex);
        }
        return type.isHeatConducting ? 0x00b4b4b4 :  0x00252525;
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
        if(pState.getValue(ATTACHED)){
            switch (pState.getValue(FACING)) {
                case DOWN: return Shape_D;
                case EAST: return Shape_E;
                case NORTH: return Shape_N;
                case SOUTH: return Shape_S;
                case UP: return Shape_U;
                case WEST: return Shape_W;
            }
        }
        return ShapeNeutral;
    }
}
