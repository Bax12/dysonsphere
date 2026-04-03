package de.bax.dysonsphere.blocks;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.tileentities.OreSpireTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OreSpireBlock extends Block implements EntityBlock{

    public static BooleanProperty HAS_BASE = BlockStateProperties.ATTACHED;

    public OreSpireBlock() {
        super(ModBlocks.defaultMetal);
        registerDefaultState(defaultBlockState().setValue(HAS_BASE, false));
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        return new OreSpireTile(pPos, pState);
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HAS_BASE);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext pContext) {

        BlockGetter blockgetter = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();

        return defaultBlockState().setValue(HAS_BASE, !blockgetter.getBlockState(blockpos.below()).isAir());
    }

    @Override
    public void onRemove(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pNewState, boolean pMovedByPiston) {
        if(!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof OreSpireTile tile){
            tile.dropContent();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        return false;
    }

    //without a tile access not possible
    // @Override
    // public List<ItemStack> getDrops(@Nonnull BlockState pState, @Nonnull net.minecraft.world.level.storage.loot.LootParams.Builder pParams) {
    //     // pParams.getLevel().getBlockEntity()
        

    //     return List.of();
    // }

    @Override
    public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
        return Block.box(3, 0, 3, 13, 16, 13);
    }

    // @Override
    // public void onPlace(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pOldState, boolean pMovedByPiston) {
    //     pState.setValue(HAS_CONVERTER_BASE,pLevel.getBlockState(pPos.below()).is(ModBlocks.ENERGY_CONVERTER_BLOCK.get()));
    // }

    // @Override
    // public void neighborChanged(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Block pNeighborBlock, @Nonnull BlockPos pNeighborPos, boolean pMovedByPiston) {
    //     pState.setValue(HAS_CONVERTER_BASE,pLevel.getBlockState(pPos.below()).is(ModBlocks.ENERGY_CONVERTER_BLOCK.get()));
    // }

    @Override
    public BlockState updateShape(@Nonnull BlockState pState, @Nonnull Direction pDirection, @Nonnull BlockState pNeighborState, @Nonnull LevelAccessor pLevel, @Nonnull BlockPos pPos, @Nonnull BlockPos pNeighborPos) {
        if(pDirection == Direction.DOWN){
            pState.setValue(HAS_BASE, !pNeighborState.isAir());
        }
        return pState;
    }
    
}
