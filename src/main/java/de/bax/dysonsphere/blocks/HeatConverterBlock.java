package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.color.ModColors.ITintableTileBlock;
import de.bax.dysonsphere.containers.HeatConverterContainer;
import de.bax.dysonsphere.tileentities.HeatConverterTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class HeatConverterBlock extends Block implements EntityBlock, ITintableTileBlock {

    public static final VoxelShape SHAPE = Stream.of(Block.box(1,1,1,15,5,15), Block.box(1,11,1,15,15,15), Block.box(1,8.25,1,15,10.25,15), Block.box(1,5.75,1,15,7.75,15), Block.box(0,15,1,1,16,4), Block.box(1,15,1,2,16,3), Block.box(2,15,1,3,16,2), Block.box(1,15,15,4,16,16), Block.box(1,15,14,3,16,15), Block.box(1,15,13,2,16,14), Block.box(15,15,12,16,16,15), Block.box(14,15,13,15,16,15), Block.box(13,15,14,14,16,15), Block.box(12,15,0,15,16,1), Block.box(13,15,1,15,16,2), Block.box(14,15,2,15,16,3), Block.box(3,15,3,13,16,13), Block.box(2,15,4,3,16,12), Block.box(13,15,4,14,16,12), Block.box(4,15,2,12,16,3), Block.box(4,15,13,12,16,14), Block.box(0,0,0,1,1,4), Block.box(1,0,0,2,1,3), Block.box(2,0,0,3,1,2), Block.box(0,0,15,4,1,16), Block.box(0,0,14,3,1,15), Block.box(0,0,13,2,1,14), Block.box(0,0,12,1,1,13), Block.box(15,0,12,16,1,16), Block.box(14,0,13,15,1,16), Block.box(13,0,14,14,1,16), Block.box(12,0,0,16,1,1), Block.box(13,0,1,16,1,2), Block.box(14,0,2,16,1,3), Block.box(15,0,3,16,1,4), Block.box(3,0,3,13,1,13), Block.box(2,0,4,3,1,12), Block.box(13,0,4,14,1,12), Block.box(4,0,13,12,1,14), Block.box(4,0,2,12,1,3), Block.box(0,12,15,1,16,16), Block.box(1,13,15,2,15,16), Block.box(2,14,15,3,15,16), Block.box(1,1,15,3,2,16), Block.box(1,2,15,2,3,16), Block.box(15,1,15,16,4,16), Block.box(14,1,15,15,3,16), Block.box(13,1,15,14,2,16), Block.box(12,0,15,13,1,16), Block.box(12,15,15,16,16,16), Block.box(13,14,15,15,15,16), Block.box(14,13,15,15,14,16), Block.box(3,3,15,13,13,16), Block.box(4,13,15,12,14,16), Block.box(4,2,15,12,3,16), Block.box(13,4,15,14,12,16), Block.box(2,4,15,3,12,16), Block.box(15,3,3,16,13,13), Block.box(15,13,4,16,14,12), Block.box(15,2,4,16,3,12), Block.box(15,4,2,16,12,3), Block.box(15,4,13,16,12,14), Block.box(15,12,15,16,15,16), Block.box(15,13,14,16,15,15), Block.box(15,14,13,16,15,14), Block.box(15,1,13,16,2,15), Block.box(15,2,14,16,3,15), Block.box(15,1,0,16,4,1), Block.box(15,1,1,16,3,2), Block.box(15,1,2,16,2,3), Block.box(15,15,0,16,16,4), Block.box(15,14,1,16,15,3), Block.box(15,13,1,16,14,2), Block.box(15,12,0,16,15,1), Block.box(14,13,0,15,15,1), Block.box(13,14,0,14,15,1), Block.box(13,1,0,15,2,1), Block.box(14,2,0,15,3,1), Block.box(0,1,0,1,4,1), Block.box(1,1,0,2,3,1), Block.box(2,1,0,3,2,1), Block.box(3,0,0,4,1,1), Block.box(1,15,0,4,16,1), Block.box(1,14,0,3,15,1), Block.box(1,13,0,2,14,1), Block.box(3,3,0,13,13,1), Block.box(4,13,0,12,14,1), Block.box(4,2,0,12,3,1), Block.box(2,4,0,3,12,1), Block.box(13,4,0,14,12,1), Block.box(0,12,0,1,16,1), Block.box(0,13,1,1,15,2), Block.box(0,14,2,1,15,3), Block.box(0,1,1,1,2,3), Block.box(0,2,1,1,3,2), Block.box(0,1,15,1,4,16), Block.box(0,1,14,1,3,15), Block.box(0,1,13,1,2,14), Block.box(0,15,12,1,16,15), Block.box(0,14,13,1,15,15), Block.box(0,13,14,1,14,15), Block.box(0,3,3,1,13,13), Block.box(0,13,4,1,14,12), Block.box(0,2,4,1,3,12), Block.box(0,4,13,1,12,14), Block.box(0,4,2,1,12,3)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public HeatConverterBlock() {
        super(ModBlocks.defaultMetal.noParticlesOnBreak());
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        return new HeatConverterTile(pPos, pState);
    }
    
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType.equals(ModTiles.HEAT_CONVERTER.get()) ? (teLevel, pos, teState, tile) -> {
            ((HeatConverterTile) tile).tick();
        } : null;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if(level.getBlockEntity(pos) instanceof HeatConverterTile tile) {
            tile.onNeighborChange();
        }
    }

    @Override
    public InteractionResult use(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer, @Nonnull InteractionHand pHand, @Nonnull BlockHitResult pHit) {
        if(!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer){
            if(pLevel.getBlockEntity(pPos) instanceof HeatConverterTile tile){
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                    new HeatConverterContainer(containerId, playerInventory, tile), Component.translatable("container.dysonsphere.heat_converter")), pPos);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext){
        return SHAPE;
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }

    //I really want to place a lever on it, ok?
    @Override
    public VoxelShape getBlockSupportShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return Shapes.block();
    }

    @Override
    public void animateTick(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull RandomSource pRandom) {
        if(pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof HeatConverterTile tile){
            int i = 20;
            for (; i > 0; i--){
                pLevel.addParticle(ParticleTypes.DRAGON_BREATH, (double)pPos.getX() + 0.5, (double)pPos.getY() + pRandom.nextDouble() * 0.5 + 0.4, (double)pPos.getZ() + 0.5, (pRandom.nextDouble() - 0.5d) * 0.03d, -0.001d, (pRandom.nextDouble() - 0.5d) * 0.03d);
            }
        }
        
    }

}
