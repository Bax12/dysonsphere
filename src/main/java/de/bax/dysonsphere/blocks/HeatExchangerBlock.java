package de.bax.dysonsphere.blocks;

import javax.annotation.Nullable;

import de.bax.dysonsphere.containers.HeatExchangerContainer;
import de.bax.dysonsphere.tileentities.HeatExchangerTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class HeatExchangerBlock extends Block implements EntityBlock {

    public HeatExchangerBlock() {
        super(ModBlocks.defaultMetal);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HeatExchangerTile(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type.equals(ModTiles.HEAT_EXCHANGER.get()) ? (teLevel, pos, teState, tile) -> {
            ((HeatExchangerTile) tile).tick();
        } : null;
    }
    
    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        BlockEntity te = level.getBlockEntity(pos);
        if(te != null && te.getType().equals(ModTiles.HEAT_EXCHANGER.get())) {
            ((HeatExchangerTile) te).onNeighborChange();
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!level.isClientSide && player instanceof ServerPlayer serverPlayer){
            BlockEntity tile = level.getBlockEntity(pos);
            if(tile != null && tile.getType().equals(ModTiles.HEAT_EXCHANGER.get())){
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInvenory, playerProvided) -> 
                new HeatExchangerContainer(containerId, playerInvenory, (HeatExchangerTile) tile), Component.translatable("container.dysonsphere.heat_exchanger")), pos);

                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }
}
