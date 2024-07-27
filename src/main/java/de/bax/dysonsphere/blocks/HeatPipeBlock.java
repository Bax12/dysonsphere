package de.bax.dysonsphere.blocks;

import javax.annotation.Nullable;

import de.bax.dysonsphere.tileentities.HeatPipeTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

public class HeatPipeBlock extends Block implements EntityBlock {
    
    public HeatPipeBlock() {
        super(ModBlocks.defaultMetal);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HeatPipeTile(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type.equals(ModTiles.HEAT_PIPE.get()) ? (teLevel, pos, teState, tile) -> {
            ((HeatPipeTile) tile).tick();
        } : null;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        BlockEntity te = level.getBlockEntity(pos);
        if(te != null && te.getType().equals(ModTiles.HEAT_PIPE.get())) {
            ((HeatPipeTile) te).onNeighborChange();
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity tile = level.getBlockEntity(pos);
        if(tile != null && tile.getType().equals(ModTiles.HEAT_PIPE.get())){
            player.displayClientMessage(Component.translatable("tooltip.dysonsphere.heat_pipe", AssetUtil.FLOAT_FORMAT.format(((HeatPipeTile) tile).heatHandler.getHeatStored())), true);
        }
        
        return InteractionResult.SUCCESS;
    }
    

}
