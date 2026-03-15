package de.bax.dysonsphere.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.containers.HeatGeneratorContainer;
import de.bax.dysonsphere.tileentities.HeatGeneratorTile;
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

public class HeatGeneratorBlock extends Block implements EntityBlock {

    public HeatGeneratorBlock() {
        super(ModBlocks.defaultMetal);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new HeatGeneratorTile(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
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
    
    @Override
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        if(!level.isClientSide && player instanceof ServerPlayer serverPlayer){
            BlockEntity tile = level.getBlockEntity(pos);
            if(tile != null && tile.getType().equals(ModTiles.HEAT_GENERATOR.get())){
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                new HeatGeneratorContainer(containerId, playerInventory, (HeatGeneratorTile) tile), Component.translatable("container.dysonsphere.heat_generator")), pos);

                return InteractionResult.CONSUME;
            }
        }
        
        
        return InteractionResult.SUCCESS;
    }

}
