package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.color.ModColors.ITintableTileBlock;
import de.bax.dysonsphere.containers.MelterContainer;
import de.bax.dysonsphere.tileentities.MelterTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class HeatMelterBlock extends Block implements EntityBlock, ITintableTileBlock {
    
    public static final VoxelShape Shape = Stream.of(Block.box(0,0,0,16,1,16), Block.box(1,1,1,15,2,15), Block.box(0,15,0,16,16,16), Block.box(1,14,1,15,15,15), Block.box(3,2,4,4,14,12), Block.box(12,2,4,13,14,12), Block.box(4,2,12,12,14,13), Block.box(4,2,3,12,14,4), Block.box(2,2,2,4,14,4), Block.box(2,2,12,4,14,14), Block.box(12,2,2,14,14,4), Block.box(12,2,12,14,14,14), Block.box(4,6,4,12,6,12)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public HeatMelterBlock() {
        super(ModBlocks.defaultMetal.noOcclusion());
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {    
        return Shape;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        return new MelterTile(pPos, pState);
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModTiles.HEAT_MELTER.get() ? (teLevel, pos, teState, tile) -> {
            ((MelterTile) tile).tick();
        } : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pNewState, boolean pIsMoving) {
        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof MelterTile tile) {
            tile.onRemove();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer, @Nonnull InteractionHand pHand, @Nonnull BlockHitResult pHit) {
        if(!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer){
            if(pLevel.getBlockEntity(pPos) instanceof MelterTile tile){
                                
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                new MelterContainer(containerId, playerInventory, tile), Component.translatable("container.dysonsphere.heat_melter")), pPos);

                return InteractionResult.CONSUME;
            }
        }
        
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if(level.getBlockEntity(pos) instanceof MelterTile tile){
            tile.onNeighborChange();
        }
    }
}
