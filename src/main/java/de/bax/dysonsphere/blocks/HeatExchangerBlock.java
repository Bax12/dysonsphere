package de.bax.dysonsphere.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.containers.HeatExchangerContainer;
import de.bax.dysonsphere.tileentities.HeatExchangerTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.network.NetworkHooks;

public class HeatExchangerBlock extends Block implements EntityBlock {

    public HeatExchangerBlock() {
        super(ModBlocks.defaultMetal);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new HeatExchangerTile(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
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
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        if(!level.isClientSide && player instanceof ServerPlayer serverPlayer){
            if(level.getBlockEntity(pos) instanceof HeatExchangerTile tile){
                if(player.getItemInHand(hand).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map((fluidItem) -> {
                    int amount = tile.inputTank.fill(fluidItem.drain(Integer.MAX_VALUE, FluidAction.SIMULATE), FluidAction.SIMULATE);
                    if(amount > 0){
                        tile.inputTank.fill(fluidItem.drain(amount, FluidAction.EXECUTE), FluidAction.EXECUTE);
                        player.setItemInHand(hand, fluidItem.getContainer());
                        return true;
                    } else {
                        amount = fluidItem.fill(tile.outputTank.drain(Integer.MAX_VALUE, FluidAction.SIMULATE), FluidAction.SIMULATE);
                        if(amount > 0){
                            fluidItem.fill(tile.outputTank.drain(amount, FluidAction.EXECUTE), FluidAction.EXECUTE);
                            player.setItemInHand(hand, fluidItem.getContainer());
                            return true;
                        }
                    }
                    return false;
                }).orElse(false)){
                    level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.6f, 1f);
                    return InteractionResult.SUCCESS;
                }
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                new HeatExchangerContainer(containerId, playerInventory, (HeatExchangerTile) tile), Component.translatable("container.dysonsphere.heat_exchanger")), pos);

                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 1;
    }
}
