package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.color.ModColors.ITintableTileBlock;
import de.bax.dysonsphere.tileentities.HeatingChamberTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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

public class HeatingChamberBlock extends HorizontalDirectionalBlock implements EntityBlock, ITintableTileBlock {
    
    public static final VoxelShape Shape_N = Stream.of(Block.box(0,0,0,16,1,16), Block.box(1,1,1,15,2,15), Block.box(0,15,0,16,16,16), Block.box(1,14,1,15,15,15), Block.box(2,2,2,4,14,14), Block.box(12,2,2,14,14,14), Block.box(4,2,12,12,14,14), Block.box(4,5,4,12,5,12), Block.box(4,10,4,12,10,12)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_E = Stream.of(Block.box(0,0,0,16,1,16), Block.box(1,1,1,15,2,15), Block.box(0,15,0,16,16,16), Block.box(1,14,1,15,15,15), Block.box(2,2,2,14,14,4), Block.box(2,2,12,14,14,14), Block.box(2,2,4,4,14,12), Block.box(4,5,4,12,5,12), Block.box(4,10,4,12,10,12)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_S = Stream.of(Block.box(0,0,0,16,1,16), Block.box(1,1,1,15,2,15), Block.box(0,15,0,16,16,16), Block.box(1,14,1,15,15,15), Block.box(12,2,2,14,14,14), Block.box(2,2,2,4,14,14), Block.box(4,2,2,12,14,4), Block.box(4,5,4,12,5,12), Block.box(4,10,4,12,10,12)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_W = Stream.of(Block.box(0,0,0,16,1,16), Block.box(1,1,1,15,2,15), Block.box(0,15,0,16,16,16), Block.box(1,14,1,15,15,15), Block.box(2,2,12,14,14,14), Block.box(2,2,2,14,14,4), Block.box(12,2,4,14,14,12), Block.box(4,5,4,12,5,12), Block.box(4,10,4,12,10,12)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public HeatingChamberBlock() {
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
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(!pLevel.isClientSide){
            if(pLevel.getBlockEntity(pPos) instanceof HeatingChamberTile tile){
                ItemStack playerStack = pPlayer.getMainHandItem();
                ItemStack outputStack = tile.output.extractItem(Integer.MAX_VALUE, false);
                //output if output isn't empty
                if(!outputStack.isEmpty()){
                    if(playerStack.isEmpty()){
                        pPlayer.setItemInHand(InteractionHand.MAIN_HAND, outputStack);
                    } else {
                        if(!pPlayer.getInventory().add(outputStack)){
                            ItemEntity entity = new ItemEntity(pLevel, tile.getBlockPos().getX(), tile.getBlockPos().getY() + 1, tile.getBlockPos().getZ(), outputStack);
                            pLevel.addFreshEntity(entity);
                        }
                    }
                } else {
                    //remove input item on shift+R-click
                    ItemStack inputStack = tile.input.extractItem(64, true);
                    if(pPlayer.isShiftKeyDown() && !inputStack.isEmpty()){
                        if(playerStack.isEmpty()){
                            pPlayer.setItemInHand(InteractionHand.MAIN_HAND, inputStack);
                        } else {
                            if(!pPlayer.getInventory().add(inputStack)){
                                ItemEntity entity = new ItemEntity(pLevel, tile.getBlockPos().getX(), tile.getBlockPos().getY() + 1, tile.getBlockPos().getZ(), inputStack);
                                pLevel.addFreshEntity(entity);
                            }
                        }
                        tile.input.extractItem(inputStack.getCount(), false);
                    } else {
                        //Add held item to input
                        if(!playerStack.isEmpty()){
                            pPlayer.setItemInHand(InteractionHand.MAIN_HAND, tile.input.insertItem(playerStack.copy(), false));
                        }
                    }
                }

            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        return new HeatingChamberTile(pPos, pState);
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModTiles.HEATING_CHAMBER.get() ? (teLevel, pos, teState, tile) -> {
            ((HeatingChamberTile) tile).tick();
        } : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pNewState, boolean pIsMoving) {
        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof HeatingChamberTile tile) {
            tile.onRemove();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if(level.getBlockEntity(pos) instanceof HeatingChamberTile tile){
            tile.onNeighborChange();
        }
    }
}
