package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.color.ModColors.ITintableTileBlock;
import de.bax.dysonsphere.tileentities.EnergyConverterTile;
import de.bax.dysonsphere.tileentities.LaserCrafterTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnergyConverterBlock extends Block implements EntityBlock, ITintableTileBlock {

    public static final VoxelShape Shape = Stream.of(Block.box(0.25,1,6,2.35,16,7), Block.box(0.25,1,9,2.35,16,10), Block.box(0.25,1,7,1.25,2,9), Block.box(0.25,3,7,1.25,4,9), Block.box(0.25,5,7,1.25,6,9), Block.box(0.25,7,7,1.25,8,9), Block.box(0.25,9,7,1.25,10,9), Block.box(0.25,11,7,1.25,12,9), Block.box(0.25,13,7,1.25,14,9), Block.box(1.25,2,7,2.25,15,9), Block.box(0.25,15,7,2.35,16,9), Block.box(6,1,13.65,7,16,15.75), Block.box(9,1,13.65,10,16,15.75), Block.box(7,1,14.75,9,2,15.75), Block.box(7,3,14.75,9,4,15.75), Block.box(7,5,14.75,9,6,15.75), Block.box(7,7,14.75,9,8,15.75), Block.box(7,9,14.75,9,10,15.75), Block.box(7,11,14.75,9,12,15.75), Block.box(7,13,14.75,9,14,15.75), Block.box(7,2,13.75,9,15,14.75), Block.box(7,15,13.65,9,16,15.75), Block.box(13.65,1,9,15.75,16,10), Block.box(13.65,1,6,15.75,16,7), Block.box(14.75,1,7,15.75,2,9), Block.box(14.75,3,7,15.75,4,9), Block.box(14.75,5,7,15.75,6,9), Block.box(14.75,7,7,15.75,8,9), Block.box(14.75,9,7,15.75,10,9), Block.box(14.75,11,7,15.75,12,9), Block.box(14.75,13,7,15.75,14,9), Block.box(13.75,2,7,14.75,15,9), Block.box(13.65,15,7,15.75,16,9), Block.box(9,1,0.25,10,16,2.35), Block.box(6,1,0.25,7,16,2.35), Block.box(7,1,0.25,9,2,1.25), Block.box(7,3,0.25,9,4,1.25), Block.box(7,5,0.25,9,6,1.25), Block.box(7,7,0.25,9,8,1.25), Block.box(7,9,0.25,9,10,1.25), Block.box(7,11,0.25,9,12,1.25), Block.box(7,13,0.25,9,14,1.25), Block.box(7,2,1.25,9,15,2.25), Block.box(7,15,0.25,9,16,2.35), Block.box(3,0,0,6,16,1.6), Block.box(0,0,3,1.6,16,6), Block.box(1.6,0,1.6,5,16,5), Block.box(11,0,1.6,14.4,16,5), Block.box(14.4,0,3,16,16,6), Block.box(10,0,0,13,16,1.6), Block.box(11,0,11,14.4,16,14.4), Block.box(14.4,0,10,16,16,13), Block.box(10,0,14.4,13,16,16), Block.box(3,0,14.4,6,16,16), Block.box(0,0,10,1.6,16,13), Block.box(1.6,0,11,5,16,14.4), Block.box(5,0,0.25,11,1,15.75), Block.box(0.25,0,5,15.75,1,11), Block.box(2.25,15.75,4,4,16,12), Block.box(4,15.75,12,12,16,13.75), Block.box(12,15.75,4,13.75,16,12), Block.box(4,15.75,2.25,12,16,4), Block.box(4,15.25,4,12,15.75,12)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public EnergyConverterBlock() {
        super(ModBlocks.defaultMetal);
        
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
        return Shape;
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 1;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        return new EnergyConverterTile(pPos, pState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModTiles.ENERGY_CONVERTER.get() ? (teLevel, pos, teState, tile) -> {
            ((EnergyConverterTile)tile).tick();
        } : null;
    }

    @Override
    public void onRemove(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pNewState, boolean pMovedByPiston) {
        if(!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof EnergyConverterTile tile){
            tile.onRemove();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    // @Override
    // public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
    //     if(!level.isClientSide() && level.getBlockEntity(pos) instanceof EnergyConverterTile tile && Math.abs(pos.subtract(neighbor).getY()) == 1){
    //         tile.onNeighborChange();
    //     }
    // }

    //we need block changes as well, so onNeighborChange() doesn't cut it...
    @Override
    public void neighborChanged(@Nonnull BlockState pState, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block pNeighborBlock, @Nonnull BlockPos neighbor, boolean pMovedByPiston) {
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof EnergyConverterTile tile && Math.abs(pos.subtract(neighbor).getY()) == 1){
            tile.onNeighborChange();
        }
    }

    
    
}
