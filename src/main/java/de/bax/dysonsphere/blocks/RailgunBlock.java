package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.containers.RailgunContainer;
import de.bax.dysonsphere.tileentities.ModTiles;
import de.bax.dysonsphere.tileentities.RailgunTile;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class RailgunBlock extends Block implements EntityBlock {

    public static final VoxelShape Shape = Stream.of(Block.box(0,0,0,16,15,16), Block.box(4,15,4,12,30,12), Block.box(-6,0.05,-6,4,8,4), Block.box(-9,0,-9,0,4,0), Block.box(-5,0.05,12,4,8,21), Block.box(-9,0,16,0,4,25), Block.box(12,0.05,12,21,8,21), Block.box(16,0,16,25,4,25), Block.box(12,0.1,-5,21,8,4), Block.box(16,0,-9,25,4,0)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

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


    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockEntity tile = level.getBlockEntity(pos);
        if(!level.isClientSide && willHarvest && tile != null && tile.getType().equals(ModTiles.RAILGUN.get())){
            ((RailgunTile) tile).dropContent();
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!level.isClientSide && player instanceof ServerPlayer serverPlayer){
            BlockEntity tile = level.getBlockEntity(pos);
            if(tile != null && tile.getType().equals(ModTiles.RAILGUN.get())){
                // player.openMenu(new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                // new RailgunContainer(containerId, playerInventory, (RailgunTile) tile), Component.translatable("container.dysonsphere.railgun")));
                if(player.isCrouching()){
                    ((RailgunTile) tile).energyStorage.receiveEnergy(5000, false);
                    DysonSphere.LOGGER.info("RailgunBlock: Added Energy. Stored: {}", ((RailgunTile) tile).energyStorage.getEnergyStored());
                    return InteractionResult.SUCCESS;
                }
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                new RailgunContainer(containerId, playerInventory, (RailgunTile) tile), Component.translatable("container.dysonsphere.railgun")), pos);

                return InteractionResult.CONSUME;
            }
        }
        
        
        return InteractionResult.SUCCESS;
    }

    
}
