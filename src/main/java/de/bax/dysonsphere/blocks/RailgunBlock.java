package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.containers.RailgunContainer;
import de.bax.dysonsphere.sounds.ModSounds;
import de.bax.dysonsphere.tileentities.ModTiles;
import de.bax.dysonsphere.tileentities.RailgunTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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

public class RailgunBlock extends Block implements EntityBlock {

    public static final VoxelShape Shape = Stream.of(Block.box(0,0,0,16,15,16), Block.box(4.5,15,4.5,11.5,29.5,11.5), Block.box(-6,0.05,-6,4,8,4), Block.box(-9,0,-9,-0.25,4,-0.25), Block.box(-5,0.05,12,4,8,21), Block.box(-9,0,16.25,-0.25,4,25), Block.box(12,0.05,12,21,8,21), Block.box(16.25,0,16.25,25,4,25), Block.box(12,0.1,-5,21,8,4), Block.box(16.25,0,-9,25,4,-0.25)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public RailgunBlock() {
        super(ModBlocks.defaultMetal);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext col) {
        return Shape;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 1;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RailgunTile(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModTiles.RAILGUN.get() ? (teLevel, pos, teState, tile) -> {
            ((RailgunTile) tile).tick();
        } : null;
    }


    // @Override
    // public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
    //     BlockEntity tile = level.getBlockEntity(pos);
    //     if(!level.isClientSide && willHarvest && tile != null && tile.getType().equals(ModTiles.RAILGUN.get())){
    //         ((RailgunTile) tile).dropContent();
    //     }
    //     return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    // }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if(!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof RailgunTile tile){
            tile.dropContent();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!level.isClientSide && player instanceof ServerPlayer serverPlayer){
            BlockEntity tile = level.getBlockEntity(pos);
            if(tile != null && tile.getType().equals(ModTiles.RAILGUN.get())){
                // player.openMenu(new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                // new RailgunContainer(containerId, playerInventory, (RailgunTile) tile), Component.translatable("container.dysonsphere.railgun")));
                // if(player.isCrouching()){
                //     ((RailgunTile) tile).energyStorage.receiveEnergy(5000, false);
                //     DysonSphere.LOGGER.info("RailgunBlock: Added Energy. Stored: {}", ((RailgunTile) tile).energyStorage.getEnergyStored());
                //     return InteractionResult.SUCCESS;
                // }
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                new RailgunContainer(containerId, playerInventory, (RailgunTile) tile), Component.translatable("container.dysonsphere.railgun")), pos);

                return InteractionResult.CONSUME;
            }
        }
        
        
        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if(level.isClientSide){
            BlockEntity tile = level.getBlockEntity(pos);
            if(tile != null && tile.getType().equals(ModTiles.RAILGUN.get())){
                int energy = ((RailgunTile) tile).energyStorage.getEnergyStored();
                int energyCap = ((RailgunTile) tile).energyStorage.getMaxEnergyStored();
                int i = energy == energyCap ? 0 : (int) (50f * energy / RailgunTile.launchEnergy);
                level.playLocalSound(pos.getX() + 0.5f, pos.getY() + 1.5f, pos.getZ() + 0.5f, ModSounds.RAILGUN_CHARGE.get(), SoundSource.BLOCKS, 0.3f * i / 50f, 0.9f, false);
                for (; i > 0; i--){
                    level.addParticle(ParticleTypes.MYCELIUM, (double)pos.getX() + random.nextDouble() * 3 - 1d, (double)pos.getY() + random.nextDouble() * 2, (double)pos.getZ() + random.nextDouble() * 3 - 1d, 0d, 5.5d, 0d);
                }
            }
            
        }
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }

    
}
