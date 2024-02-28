package de.bax.dysonsphere.blocks;

import javax.annotation.Nullable;

import de.bax.dysonsphere.tileentities.HeatGeneratorTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HeatGeneratorBlock extends Block implements EntityBlock {

    public HeatGeneratorBlock() {
        super(ModBlocks.defaultMetal);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HeatGeneratorTile(pos, state);
    }

        @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type.equals(ModTiles.HEAT_GENERATOR.get()) ? (teLevel, pos, teState, tile) -> {
            ((HeatGeneratorTile) tile).tick();
        } : null;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        BlockEntity te = level.getBlockEntity(pos);
        if(te != null && te.getType().equals(ModTiles.HEAT_GENERATOR.get())) {
            ((HeatGeneratorTile) te).onNeighborChange();
        }
    }
    
}
