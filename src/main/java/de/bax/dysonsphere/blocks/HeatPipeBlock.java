package de.bax.dysonsphere.blocks;

import javax.annotation.Nullable;

import de.bax.dysonsphere.tileentities.HeatPipeTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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
        return EntityBlock.super.getTicker(level, state, type);
    }

    

}
