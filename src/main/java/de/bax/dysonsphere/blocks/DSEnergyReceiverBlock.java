package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.containers.DSEnergyReceiverContainer;
import de.bax.dysonsphere.tileentities.DSEnergyReceiverTile;
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

public class DSEnergyReceiverBlock extends Block implements EntityBlock{

    public static final VoxelShape Shape = Stream.of(Block.box(0,0,0,16,6,16), Block.box(0.2,6,0.2,2.2,16,2.2), Block.box(13.8,6,13.8,15.8,16,15.8), Block.box(0.2,6,13.8,2.2,16,15.8), Block.box(13.8,6,0.2,15.8,16,2.2), Block.box(6,7,6,10,8,10), Block.box(3,6,3,13,7,13), Block.box(7.75,8,7.75,8.25,14,8.25), Block.box(7.25,12.7,7.15,8.75,13,7.25), Block.box(7.25,12.2,7.15,8.75,12.5,7.25), Block.box(4,7,4,5,12,5), Block.box(11,7,4,12,12,5), Block.box(11,7,11,12,12,12), Block.box(4,7,11,5,12,12), Block.box(4,10.7,3.9,12,11,4), Block.box(4,10.2,3.9,12,10.5,4)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public DSEnergyReceiverBlock() {
        super(ModBlocks.defaultMetal);
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext col) {
        return Shape;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DSEnergyReceiverTile(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModTiles.DS_ENERGY_RECEIVER.get() ? (teLevel, pos, teState, tile) -> {
            ((DSEnergyReceiverTile) tile).tick();
        } : null;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        BlockEntity te = level.getBlockEntity(pos);
        if(te != null && te.getType().equals(ModTiles.DS_ENERGY_RECEIVER.get())) {
            ((DSEnergyReceiverTile) te).onNeighborChange();
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!level.isClientSide && player instanceof ServerPlayer serverPlayer){
            BlockEntity tile = level.getBlockEntity(pos);
            if(tile != null && tile.getType().equals(ModTiles.DS_ENERGY_RECEIVER.get())){
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                new DSEnergyReceiverContainer(containerId, playerInventory, (DSEnergyReceiverTile) tile), 
                Component.translatable("container.dysonsphere.ds_energy_receiver")), pos);
            }

            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }

}
