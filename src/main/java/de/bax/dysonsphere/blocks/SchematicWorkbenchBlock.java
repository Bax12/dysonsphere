package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.containers.SchematicWorkbenchInventoryContainer;
import de.bax.dysonsphere.containers.SchematicWorkbenchTableContainer;
import de.bax.dysonsphere.tileentities.SchematicWorkbenchTile;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class SchematicWorkbenchBlock extends HorizontalDirectionalBlock implements EntityBlock {
    
    public static final VoxelShape Shape_N = Stream.of(Block.box(0,0,0,16,1,16), Block.box(1,1,8,15,14,16), Block.box(15,1,0,16,14,16), Block.box(0,1,0,1,14,16), Block.box(1,7,1,15,8,8), Block.box(0,14,0,16,15,16), Block.box(1,15,1.5,15,15.05,5.5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_E = Stream.of(Block.box(0,0,0,16,1,16), Block.box(0,1,1,8,14,15), Block.box(0,1,15,16,14,16), Block.box(0,1,0,16,14,1), Block.box(8,7,1,15,8,15), Block.box(0,14,0,16,15,16), Block.box(10.5,15,1,14.5,15.05,15)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_S = Stream.of(Block.box(0,0,0,16,1,16), Block.box(1,1,0,15,14,8), Block.box(0,1,0,1,14,16), Block.box(15,1,0,16,14,16), Block.box(1,7,8,15,8,15), Block.box(0,14,0,16,15,16), Block.box(1,15,10.5,15,15.05,14.5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_W = Stream.of(Block.box(0,0,0,16,1,16), Block.box(8,1,1,16,14,15), Block.box(0,1,0,16,14,1), Block.box(0,1,15,16,14,16), Block.box(1,7,1,8,8,15), Block.box(0,14,0,16,15,16), Block.box(1.5,15,1,5.5,15.05,15)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();



    public SchematicWorkbenchBlock() {
        super(ModBlocks.defaultMetal);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        switch (pState.getValue(FACING)) {
            case NORTH: return Shape_N;
            case EAST: return Shape_E;
            case SOUTH: return Shape_S;
            case WEST: return Shape_W;
            default: return Shape_N;
        }
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        return new SchematicWorkbenchTile(pPos, pState);
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pNewState, boolean pIsMoving) {
        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof SchematicWorkbenchTile tile) {
            tile.onRemove();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer, @Nonnull InteractionHand pHand, @Nonnull BlockHitResult pHit) {
        if(!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer){
            if(pLevel.getBlockEntity(pPos) instanceof SchematicWorkbenchTile tile){
                
                // if(pHit.getDirection().equals(Direction.UP)){
                if(pHit.getLocation().subtract(pPos.getCenter()).y() >= 0.4){ //open side inventory when clicking the top of the "shelf" and the top inventory on the side of the paper.
                    NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                    new SchematicWorkbenchTableContainer(containerId, playerInventory, tile), Component.translatable("container.dysonsphere.schematic_workbench")), pPos);
                } else {
                    NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                    new SchematicWorkbenchInventoryContainer(containerId, playerInventory, tile), Component.translatable("container.dysonsphere.schematic_workbench")), pPos);
                }
                return InteractionResult.CONSUME;
            }
        }
        
        return InteractionResult.SUCCESS;
    }
}
