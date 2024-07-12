package de.bax.dysonsphere.blocks;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.containers.LaserPatternControllerContainer;
import de.bax.dysonsphere.containers.LaserPatternControllerInventoryContainer;
import de.bax.dysonsphere.tileentities.LaserPatternControllerTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class LaserPatternControllerBlock extends HorizontalDirectionalBlock implements EntityBlock{
    
    public static final VoxelShape Shape_N = Stream.of(Block.box(0,0,0,16,1,16), Block.box(3,1,3,13,4,13), Block.box(5,4,8,11,11,10)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_E = Stream.of(Block.box(0,0,0,16,1,16), Block.box(3,1,3,13,4,13), Block.box(6,4,5,8,11,11)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    
    public static final VoxelShape Shape_S = Stream.of(Block.box(0,0,0,16,1,16), Block.box(3,1,3,13,4,13), Block.box(5,4,6,11,11,8)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    
    public static final VoxelShape Shape_W = Stream.of(Block.box(0,0,0,16,1,16), Block.box(3,1,3,13,4,13), Block.box(8,4,5,10,11,11)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
        
    public LaserPatternControllerBlock() {
        super(ModBlocks.defaultMetal);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!level.isClientSide && player instanceof ServerPlayer serverPlayer){
            BlockEntity tile = level.getBlockEntity(pos);
            if(tile != null && tile.getType().equals(ModTiles.LASER_PATTERN_CONTROLLER.get())){
                ItemStack playerStack = player.getMainHandItem();
                LaserPatternControllerTile controllerTile = ((LaserPatternControllerTile) tile);
                if(player.isCrouching()){
                    ItemStack containerStack = controllerTile.inventory.extractItem(0, 1, false);
                    if(playerStack.isEmpty()){
                        //remove internal itemstack
                        player.setItemInHand(InteractionHand.MAIN_HAND, containerStack);
                    } else  {
                        if(!player.getInventory().add(containerStack)){
                            controllerTile.dropContent();
                        }
                    }
                } else if(!playerStack.isEmpty() && playerStack.getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER).isPresent()){
                    playerStack.getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER).ifPresent((container) -> {
                        ItemStack remainder = controllerTile.inventory.insertItem(0, playerStack.copyWithCount(1), false);
                        if(remainder.isEmpty()){
                            playerStack.shrink(1);
                            player.setItemInHand(InteractionHand.MAIN_HAND, playerStack);
                        }
                    });
                } else {
                    if(controllerTile.inventory.getStackInSlot(0).isEmpty() && controllerTile.hasMinEnergy()){
                        NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                        new LaserPatternControllerInventoryContainer(containerId, playerInventory, (LaserPatternControllerTile) tile), Component.translatable("container.dysonsphere.laser_pattern_controller")), pos);
                        controllerTile.consumeEnergy();
                    } else {
                        NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                        new LaserPatternControllerContainer(containerId, playerInventory, (LaserPatternControllerTile) tile), Component.translatable("container.dysonsphere.laser_pattern_controller")), pos);
                    }
                    controllerTile.sendSyncPackageToNearbyPlayers();
                    return InteractionResult.CONSUME;
                }
                
            }
        }
        
        return InteractionResult.SUCCESS;
    }

        @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext col) {
        if(state.hasProperty(FACING)){
            switch (state.getValue(FACING)){
                case EAST: return Shape_E;
                case NORTH: return Shape_N;
                case SOUTH: return Shape_S;
                case WEST: return Shape_W;
                default:
                    break;
            }
        }
        return Shape_N;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 1;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LaserPatternControllerTile(pPos, pState);
    }

        @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockEntity tile = level.getBlockEntity(pos);
        if(!level.isClientSide && willHarvest && tile != null && tile.getType().equals(ModTiles.RAILGUN.get())){
            ((LaserPatternControllerTile) tile).dropContent();
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

}
