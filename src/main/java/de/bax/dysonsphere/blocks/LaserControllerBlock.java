package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.containers.LaserControllerContainer;
import de.bax.dysonsphere.tileentities.LaserControllerTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

public class LaserControllerBlock extends Block implements EntityBlock{

    public static final VoxelShape SHAPE = Stream.of(Block.box(0,0,0,16,6,16), Block.box(1,8,1,15,14,15), Block.box(4,14,4,12,16,12), Block.box(4,6,4,12,8,12), Block.box(6,6,14.5,10,11,15.5), Block.box(14.5,6,6,15.5,11,10), Block.box(0.5,6,6,1.5,11,10), Block.box(6,6,0.5,10,11,1.5)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();


    public LaserControllerBlock() {
        super(ModBlocks.defaultMetal);
        
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if(!pLevel.isClientSide()){
            if(pLevel.hasNeighborSignal(pPos)){
                if(pLevel.getBlockEntity(pPos) instanceof LaserControllerTile laserTile){
                    laserTile.getOwner().getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((orbitalLaser) -> {
                        int laserCount = orbitalLaser.getLasersAvailable(laserTile.getOwner().tickCount);
                        if(laserCount >= laserTile.getPattern().getLasersRequired()){
                            laserTile.startLaunch();
                        }
                    });
                }
            }
        }
    }
    

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LaserControllerTile(pPos, pState);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!level.isClientSide && player instanceof ServerPlayer serverPlayer){
            BlockEntity tile = level.getBlockEntity(pos);
            if(tile != null && tile.getType().equals(ModTiles.LASER_CONTROLLER.get())){
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                new LaserControllerContainer(containerId, playerInventory, (LaserControllerTile) tile), 
                Component.translatable("container.dysonsphere.laser_controller_block")), pos);
            }

            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 1;
    }

    // @Override
    // public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
    //     BlockEntity tile = level.getBlockEntity(pos);
    //     if(!level.isClientSide && willHarvest && tile != null && tile.getType().equals(ModTiles.LASER_CONTROLLER.get())){
    //         ((LaserControllerTile) tile).dropContent();
    //     }
    //     return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    // }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if(!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof LaserControllerTile tile){
            tile.dropContent();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModTiles.LASER_CONTROLLER.get() ? (teLevel, pos, teState, tile) -> {
            ((LaserControllerTile) tile).tick();
        } : null;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if(pLevel.isClientSide){
            BlockEntity tile = pLevel.getBlockEntity(pPos);
            if(tile != null && tile.getType().equals(ModTiles.LASER_CONTROLLER.get())){
                if(((LaserControllerTile) tile).isWorking()){
                    pLevel.addParticle(ParticleTypes.REVERSE_PORTAL, pPos.getX() + 0.4f + pRandom.nextDouble() * 0.2f, pPos.getY() + 1f, pPos.getZ() + 0.4f + pRandom.nextDouble() * 0.2f, 0, 0.1f, 0);
                }
            }
        }
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if(!pLevel.isClientSide && pPlacer instanceof Player player){
            BlockEntity tile = pLevel.getBlockEntity(pPos);
            if(tile != null && tile.getType().equals(ModTiles.LASER_CONTROLLER.get())){
                ((LaserControllerTile) tile).setOwner(player);
            }
        }
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }
    
}
