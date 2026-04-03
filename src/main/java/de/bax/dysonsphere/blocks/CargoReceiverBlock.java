package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.containers.CargoReceiverContainer;
import de.bax.dysonsphere.tileentities.CargoReceiverTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
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

public class CargoReceiverBlock extends Block implements EntityBlock {
    
    public static final VoxelShape Shape = Stream.of(Block.box(0,1,0,1.25,16,1.25), Block.box(0,1,14.75,1.25,16,16), Block.box(14.75,1,14.75,16,16,16), Block.box(14.75,1,0,16,16,1.25), Block.box(0,0,0,16,1,16), Block.box(1.25,10,1.25,14.75,11,14.75), Block.box(1.25,1,0.75,14.75,16,1.25), Block.box(0.75,1,1.25,1.25,16,14.75), Block.box(1.25,1,14.75,14.75,16,15.25), Block.box(1.25,1,15.25,14.75,10,15.75), Block.box(1.25,1,15.5,14.75,4,16), Block.box(15.25,1,1.25,15.75,10,14.75), Block.box(15.5,1,1.25,16,4,14.75), Block.box(1.25,1,0.25,14.75,10,0.75), Block.box(1.25,1,0,14.75,4,0.5), Block.box(0.25,1,1.25,0.75,10,14.75), Block.box(0,1,1.25,0.5,4,14.75), Block.box(14.75,1,1.25,15.25,16,14.75)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();


    public CargoReceiverBlock(){
        super(ModBlocks.defaultMetal);
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
        return Shape;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        return new CargoReceiverTile(pPos, pState);
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModTiles.CARGO_RECEIVER.get() ? (teLevel, pos, teState, tile) -> {
            ((CargoReceiverTile) tile).tick();
        } : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pNewState, boolean pMovedByPiston) {
        if(!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof CargoReceiverTile tile){
            tile.onRemove();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public InteractionResult use(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer, @Nonnull InteractionHand pHand, @Nonnull BlockHitResult pHit) {
        if(!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer){
            if(pLevel.getBlockEntity(pPos) instanceof CargoReceiverTile tile){
                                
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                new CargoReceiverContainer(containerId, playerInventory, (CargoReceiverTile) tile), Component.translatable("container.dysonsphere.cargo_receiver")), pPos);

                return InteractionResult.CONSUME;
            }
        }
        
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if(level.getBlockEntity(pos) instanceof CargoReceiverTile tile){
            tile.onNeighborChange();
        }
    }

    @Override
    public void animateTick(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull RandomSource pRandom) {
        if(pLevel.isClientSide){
            if(pLevel.getBlockEntity(pPos) instanceof CargoReceiverTile cargoReceiverTile){
                
                if(cargoReceiverTile.getCurrentRecipe() != null && cargoReceiverTile.getEnergyStored() >= cargoReceiverTile.getCurrentRecipe().energy()){
                    //todo add cargo capsule particle
                }
                
            }
            
        }
    }
    
}
