package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.tileentities.LaserRedirectorTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LaserRedirectorBlock extends DirectionalBlock implements EntityBlock {
    
    protected final boolean toggle;

    public static final VoxelShape Shape = Stream.of(Block.box(2,0,0,4,2,2), Block.box(0,0,0,2,2,2), Block.box(0,2,0,2,4,2), Block.box(0,0,2,2,2,4), Block.box(2,14,14,4,16,16), Block.box(0,14,14,2,16,16), Block.box(0,12,14,2,14,16), Block.box(0,14,12,2,16,14), Block.box(0,0,12,2,2,14), Block.box(0,0,14,2,2,16), Block.box(0,2,14,2,4,16), Block.box(2,0,14,4,2,16), Block.box(0,14,2,2,16,4), Block.box(0,14,0,2,16,2), Block.box(0,12,0,2,14,2), Block.box(2,14,0,4,16,2), Block.box(12,0,14,14,2,16), Block.box(14,0,14,16,2,16), Block.box(14,2,14,16,4,16), Block.box(14,0,12,16,2,14), Block.box(12,14,0,14,16,2), Block.box(14,14,0,16,16,2), Block.box(14,12,0,16,14,2), Block.box(14,14,2,16,16,4), Block.box(14,0,2,16,2,4), Block.box(14,0,0,16,2,2), Block.box(14,2,0,16,4,2), Block.box(12,0,0,14,2,2), Block.box(14,14,12,16,16,14), Block.box(14,14,14,16,16,16), Block.box(14,12,14,16,14,16), Block.box(12,14,14,14,16,16), Block.box(4,1,4,12,2,12), Block.box(12,2,4,13,3,12), Block.box(4,2,3,12,3,4), Block.box(3,2,4,4,3,12), Block.box(4,2,12,12,3,13), Block.box(4,4,1,12,12,2), Block.box(12,4,2,13,12,3), Block.box(4,12,2,12,13,3), Block.box(3,4,2,4,12,3), Block.box(4,3,2,12,4,3), Block.box(1,4,4,2,12,12), Block.box(2,4,3,3,12,4), Block.box(2,12,4,3,13,12), Block.box(2,4,12,3,12,13), Block.box(2,3,4,3,4,12), Block.box(4,4,14,12,12,15), Block.box(3,4,13,4,12,14), Block.box(4,12,13,12,13,14), Block.box(12,4,13,13,12,14), Block.box(4,3,13,12,4,14), Block.box(14,4,4,15,12,12), Block.box(13,4,12,14,12,13), Block.box(13,12,4,14,13,12), Block.box(13,4,3,14,12,4), Block.box(13,3,4,14,4,12), Block.box(4,14,4,12,15,12), Block.box(4,13,12,12,14,13), Block.box(3,13,4,4,14,12), Block.box(4,13,3,12,14,4), Block.box(12,13,4,13,14,12), Block.box(3,3,3,4,4,4), Block.box(3,12,3,4,13,4), Block.box(12,3,3,13,4,4), Block.box(12,12,3,13,13,4), Block.box(3,3,12,4,4,13), Block.box(3,12,12,4,13,13), Block.box(12,12,12,13,13,13), Block.box(12,3,12,13,4,13), Block.box(0.5,0.5,4,1.5,1.5,12), Block.box(0.5,14.5,4,1.5,15.5,12), Block.box(14.5,0.5,4,15.5,1.5,12), Block.box(14.5,14.5,4,15.5,15.5,12), Block.box(4,14.5,0.5,12,15.5,1.5), Block.box(4,0.5,0.5,12,1.5,1.5), Block.box(4,14.5,14.5,12,15.5,15.5), Block.box(4,0.5,14.5,12,1.5,15.5), Block.box(14.5,4,14.5,15.5,12,15.5), Block.box(0.5,4,14.5,1.5,12,15.5), Block.box(14.5,4,0.5,15.5,12,1.5), Block.box(0.5,4,0.5,1.5,12,1.5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public LaserRedirectorBlock(boolean toggle) {
        super(ModBlocks.defaultMetal);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP));
        this.toggle = toggle;
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {    
        return Shape;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        
        return pBlockEntityType == ModTiles.LASER_REDIRECTOR.get() || pBlockEntityType == ModTiles.LASER_REDIRECTOR_TOGGLE.get() ?  (level, pos, state, tile) -> {
            ((LaserRedirectorTile) tile).tick();
        } : null;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        if(toggle) return new LaserRedirectorTile.Toggle(pPos, pState);
        return new LaserRedirectorTile(pPos, pState);
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @org.jetbrains.annotations.Nullable Direction direction) {
        return toggle;
    }

    @Override
    public void neighborChanged(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Block pNeighborBlock, @Nonnull BlockPos pNeighborPos, boolean pMovedByPiston) {
        
        if(pLevel.getBlockEntity(pPos) instanceof LaserRedirectorTile.Toggle tile){
            tile.setToggled(pLevel.hasNeighborSignal(pPos));
        }
            
    }

}
