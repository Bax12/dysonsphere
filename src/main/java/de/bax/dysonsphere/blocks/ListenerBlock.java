package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.containers.ListenerContainer;
import de.bax.dysonsphere.tileentities.ListenerTile;
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

public class ListenerBlock extends Block implements EntityBlock {
    
    public static final VoxelShape Shape = Stream.of(Block.box(0,0,0,2,2,2), Block.box(14,0,0,16,2,2), Block.box(14,0,14,16,2,16), Block.box(0,0,14,2,2,16), Block.box(14,2,0.5,15.5,2.5,2), Block.box(0.5,2,0.5,2,2.5,2), Block.box(0.5,2,14,2,2.5,15.5), Block.box(14,2,14,15.5,2.5,15.5), Block.box(1,2,1,15,3.75,15), Block.box(1.5,3.75,1.5,14.5,5,14.5), Block.box(2,5,2,3,8.75,3), Block.box(2,5,13,3,8.75,14), Block.box(13,5,2,14,8.75,3), Block.box(13,5,13,14,8.75,14), Block.box(6.25,5,6.25,9.75,11,9.75), Block.box(6,5,6,10,5.25,10)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();


    public ListenerBlock(){
        super(ModBlocks.defaultMetal);
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
        return Shape;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        return new ListenerTile(pPos, pState);
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModTiles.LISTENER.get() ? (teLevel, pos, teState, tile) -> {
            ((ListenerTile) tile).tick();
        } : null;
    }

    @Override
    public void onRemove(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pNewState, boolean pMovedByPiston) {
        if(pLevel.getBlockEntity(pPos) instanceof ListenerTile tile){
            tile.onRemove();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public InteractionResult use(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer, @Nonnull InteractionHand pHand, @Nonnull BlockHitResult pHit) {
        if(!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer){
            if(pLevel.getBlockEntity(pPos) instanceof ListenerTile tile){
                                
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                new ListenerContainer(containerId, playerInventory, (ListenerTile) tile), Component.translatable("container.dysonsphere.listener")), pPos);

                return InteractionResult.CONSUME;
            }
        }
        
        return InteractionResult.SUCCESS;
    }

    // @Override
    // public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
    //     if(level.getBlockEntity(pos) instanceof ListenerTile tile){
            
    //     }
    // }

    
}
