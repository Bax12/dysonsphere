package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.containers.SiliconCrystallizerContainer;
import de.bax.dysonsphere.tileentities.ModTiles;
import de.bax.dysonsphere.tileentities.SiliconCrystallizerTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.network.NetworkHooks;

public class SiliconCrystallizerBlock extends HorizontalDirectionalBlock implements EntityBlock {
    
    public static final VoxelShape Shape_N = Stream.of(Block.box(0,0,0,16,2,16), Block.box(0.25,2,0.25,10.25,3,10.25), Block.box(0.25,3,9.25,10.25,13,10.25), Block.box(0.25,3,0.25,10.25,13,1.25), Block.box(9.25,3,1.25,10.25,13,9.25), Block.box(0.25,3,1.25,1.25,13,9.25), Block.box(1.25,2,12,9.25,16,15), Block.box(3.25,15,4,7.25,16,12), Block.box(11,2,0.25,15,7,15.75), Block.box(10.25,3,1,11,6,8)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_E = Stream.of(Block.box(0,0,0,16,2,16), Block.box(5.75,2,0.25,15.75,3,10.25), Block.box(5.75,3,0.25,6.75,13,10.25), Block.box(14.75,3,0.25,15.75,13,10.25), Block.box(6.75,3,9.25,14.75,13,10.25), Block.box(6.75,3,0.25,14.75,13,1.25), Block.box(1,2,1.25,4,16,9.25), Block.box(4,15,3.25,12,16,7.25), Block.box(0.25,2,11,15.75,7,15), Block.box(8,3,10.25,15,6,11)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_S = Stream.of(Block.box(0,0,0,16,2,16), Block.box(5.75,2,5.75,15.75,3,15.75), Block.box(5.75,3,5.75,15.75,13,6.75), Block.box(5.75,3,14.75,15.75,13,15.75), Block.box(5.75,3,6.75,6.75,13,14.75), Block.box(14.75,3,6.75,15.75,13,14.75), Block.box(6.75,2,1,14.75,16,4), Block.box(8.75,15,4,12.75,16,12), Block.box(1,2,0.25,5,7,15.75), Block.box(5,3,8,5.75,6,15)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_W = Stream.of(Block.box(0,0,0,16,2,16), Block.box(0.25,2,5.75,10.25,3,15.75), Block.box(9.25,3,5.75,10.25,13,15.75), Block.box(0.25,3,5.75,1.25,13,15.75), Block.box(1.25,3,5.75,9.25,13,6.75), Block.box(1.25,3,14.75,9.25,13,15.75), Block.box(12,2,6.75,15,16,14.75), Block.box(4,15,8.75,12,16,12.75), Block.box(0.25,2,1,15.75,7,5), Block.box(1,3,5,8,6,5.75)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();



    public SiliconCrystallizerBlock() {
        super(ModBlocks.defaultMetal);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {    
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
        return new SiliconCrystallizerTile(pPos, pState);
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModTiles.SILICON_CRYSTALLIZER.get() ? (teLevel, pos, teState, tile) -> {
            ((SiliconCrystallizerTile) tile).tick();
        } : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pNewState, boolean pIsMoving) {
        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof SiliconCrystallizerTile tile) {
            tile.onRemove();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer, @Nonnull InteractionHand pHand, @Nonnull BlockHitResult pHit) {
        if(!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer){
            if(pLevel.getBlockEntity(pPos) instanceof SiliconCrystallizerTile tile){
                if(pPlayer.getItemInHand(pHand).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map((fluidItem) -> {
                    int amount = tile.inputTank.fill(fluidItem.drain(Integer.MAX_VALUE, FluidAction.SIMULATE), FluidAction.SIMULATE);
                    if(amount > 0){
                        tile.inputTank.fill(fluidItem.drain(amount, FluidAction.EXECUTE), FluidAction.EXECUTE);
                        pPlayer.setItemInHand(pHand, fluidItem.getContainer());
                        return true;
                    } 
                    return false;
                }).orElse(false)){
                    pLevel.playSound(null, pPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.6f, 1f);
                    return InteractionResult.SUCCESS;
                }         
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                new SiliconCrystallizerContainer(containerId, playerInventory, tile), Component.translatable("container.dysonsphere.silicon_crystallizer")), pPos);

                return InteractionResult.CONSUME;
            }
        }
        
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if(level.getBlockEntity(pos) instanceof SiliconCrystallizerTile tile){
            tile.onNeighborChange();
        }
    }

}
