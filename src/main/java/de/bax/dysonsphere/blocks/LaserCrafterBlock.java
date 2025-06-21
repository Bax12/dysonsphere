package de.bax.dysonsphere.blocks;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.inputHatch.IInputProvider.ProviderType;
import de.bax.dysonsphere.color.ModColors.ITintableTileBlock;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.items.tools.WrenchItem;
import de.bax.dysonsphere.tileentities.LaserCrafterTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class LaserCrafterBlock extends Block implements EntityBlock, ITintableTileBlock {

    public LaserCrafterBlock() {
        super(ModBlocks.defaultMetal.noOcclusion());
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LaserCrafterTile(pPos, pState);
    }


    @Override
    public InteractionResult use(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer, @Nonnull InteractionHand pHand, @Nonnull BlockHitResult pHit) {
        if(!pLevel.isClientSide){
            if(pLevel.getBlockEntity(pPos) instanceof LaserCrafterTile tile){
                ItemStack playerStack = pPlayer.getMainHandItem();
                if(playerStack.is(ModItems.TARGET_DESIGNATOR.get())){
                    return InteractionResult.PASS;
                }
                if(WrenchItem.isWrench(playerStack)){
                    tile.acceptorHandler.markForRefresh();
                }
                ItemStack outputStack = tile.output.extractItem(0, Integer.MAX_VALUE, false);
                //output if output isn't empty
                if(!outputStack.isEmpty()){
                    if(playerStack.isEmpty()){
                        pPlayer.setItemInHand(InteractionHand.MAIN_HAND, outputStack);
                    } else {
                        if(!pPlayer.getInventory().add(outputStack)){
                            ItemEntity entity = new ItemEntity(pLevel, tile.getBlockPos().getX(), tile.getBlockPos().getY() + 1, tile.getBlockPos().getZ(), outputStack);
                            pLevel.addFreshEntity(entity);
                        }
                    }
                } else {
                    //remove input item on shift+R-click
                    ItemStack inputStack = tile.input.getStackInSlot(0);
                    if(pPlayer.isShiftKeyDown() && !inputStack.isEmpty()){
                        if(playerStack.isEmpty()){
                            pPlayer.setItemInHand(InteractionHand.MAIN_HAND, inputStack);
                        } else {
                            if(!pPlayer.getInventory().add(inputStack)){
                                ItemEntity entity = new ItemEntity(pLevel, tile.getBlockPos().getX(), tile.getBlockPos().getY() + 1, tile.getBlockPos().getZ(), inputStack);
                                pLevel.addFreshEntity(entity);
                            }
                        }
                        tile.input.extractItem(0, 1, false);
                    } else {
                        //Add held item to input
                        if(inputStack.isEmpty() && !playerStack.isEmpty()){
                            pPlayer.setItemInHand(InteractionHand.MAIN_HAND, tile.input.insertItem(0, playerStack.copy(), false));
                        }
                    }
                }
            }
        }         
        
        
        return InteractionResult.CONSUME;
    }

    // public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
    //     return Shapes.empty();
    // }

    // @Override
    // public RenderShape getRenderShape(BlockState pState) {
    //     return RenderShape.INVISIBLE;
    // }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModTiles.LASER_CRAFTER.get() ? (teLevel, pos, teState, tile) -> {
            ((LaserCrafterTile)tile).tick();
        } : null;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if(!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof LaserCrafterTile tile){
            tile.onRemove();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        return pLevel.getBlockEntity(pPos) instanceof LaserCrafterTile tile ? (int) tile.getNeededChargeRatio() : 0;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof LaserCrafterTile tile){
            tile.onNeighborChange();
        }
    }

    
}
