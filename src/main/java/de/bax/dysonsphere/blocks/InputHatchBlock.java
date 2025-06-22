package de.bax.dysonsphere.blocks;

import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.bax.dysonsphere.color.ModColors.ITintableTile;
import de.bax.dysonsphere.color.ModColors.ITintableTileBlock;
import de.bax.dysonsphere.containers.InputHatchEnergyContainer;
import de.bax.dysonsphere.containers.InputHatchParallelContainer;
import de.bax.dysonsphere.containers.InputHatchSerialContainer;
import de.bax.dysonsphere.items.tools.WrenchItem;
import de.bax.dysonsphere.tileentities.InputHatchTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class InputHatchBlock extends Block implements EntityBlock, ITintableTileBlock {

    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public static final VoxelShape ShapeNeutral = Stream.of(Block.box(1,1,1,15,15,15), Block.box(0,0,0,2,2,2), Block.box(0,14,14,2,16,16), Block.box(0,0,14,2,2,16), Block.box(0,14,0,2,16,2), Block.box(14,0,14,16,2,16), Block.box(14,14,0,16,16,2), Block.box(14,0,0,16,2,2), Block.box(14,14,14,16,16,16), Block.box(2,0,0,14,1,1), Block.box(2,15,0,14,16,1), Block.box(2,15,15,14,16,16), Block.box(2,0,15,14,1,16), Block.box(0,0,2,1,1,14), Block.box(0,15,2,1,16,14), Block.box(15,15,2,16,16,14), Block.box(15,0,2,16,1,14), Block.box(15,2,0,16,14,1), Block.box(0,2,0,1,14,1), Block.box(0,2,15,1,14,16), Block.box(15,2,15,16,14,16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_U = Stream.of(Block.box(1,1,1,15,16,15), Block.box(0,14,0,2,15.975,2), Block.box(0,0,14,2,2,16), Block.box(0,0,0,2,2,2), Block.box(0,14,14,2,15.975,16), Block.box(14,0,0,16,2,2), Block.box(14,14,14,16,15.975,16), Block.box(14,14,0,16,15.975,2), Block.box(14,0,14,16,2,16), Block.box(2,0,15,14,1,16), Block.box(2,0,0,14,1,1), Block.box(0,2,0,1,14,1), Block.box(0,2,15,1,14,16), Block.box(15,2,15,16,14,16), Block.box(15,2,0,16,14,1), Block.box(0,0,2,1,1,14), Block.box(15,0,2,16,1,14)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_N = Stream.of(Block.box(1,1,0,15,15,15), Block.box(0,0,0.025,2,2,2), Block.box(0,14,14,2,16,16), Block.box(0,0,14,2,2,16), Block.box(0,14,0.025,2,16,2), Block.box(14,0,14,16,2,16), Block.box(14,14,0.025,16,16,2), Block.box(14,0,0.025,16,2,2), Block.box(14,14,14,16,16,16), Block.box(2,15,15,14,16,16), Block.box(2,0,15,14,1,16), Block.box(0,0,2,1,1,14), Block.box(0,15,2,1,16,14), Block.box(15,15,2,16,16,14), Block.box(15,0,2,16,1,14), Block.box(0,2,15,1,14,16), Block.box(15,2,15,16,14,16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_E = Stream.of(Block.box(1,1,1,16,15,15), Block.box(14,0,0,15.975,2,2), Block.box(0,14,0,2,16,2), Block.box(0,0,0,2,2,2), Block.box(14,14,0,15.975,16,2), Block.box(0,0,14,2,2,16), Block.box(14,14,14,15.975,16,16), Block.box(14,0,14,15.975,2,16), Block.box(0,14,14,2,16,16), Block.box(0,15,2,1,16,14), Block.box(0,0,2,1,1,14), Block.box(2,0,0,14,1,1), Block.box(2,15,0,14,16,1), Block.box(2,15,15,14,16,16), Block.box(2,0,15,14,1,16), Block.box(0,2,0,1,14,1), Block.box(0,2,15,1,14,16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    
    public static final VoxelShape Shape_S = Stream.of(Block.box(1,1,1,15,15,16), Block.box(14,0,14,16,2,15.975), Block.box(14,14,0,16,16,2), Block.box(14,0,0,16,2,2), Block.box(14,14,14,16,16,15.975), Block.box(0,0,0,2,2,2), Block.box(0,14,14,2,16,15.975), Block.box(0,0,14,2,2,15.975), Block.box(0,14,0,2,16,2), Block.box(2,15,0,14,16,1), Block.box(2,0,0,14,1,1), Block.box(15,0,2,16,1,14), Block.box(15,15,2,16,16,14), Block.box(0,15,2,1,16,14), Block.box(0,0,2,1,1,14), Block.box(15,2,0,16,14,1), Block.box(0,2,0,1,14,1)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    
    public static final VoxelShape Shape_W = Stream.of(Block.box(0,1,1,15,15,15), Block.box(0.025,0,14,2,2,16), Block.box(14,14,14,16,16,16), Block.box(14,0,14,16,2,16), Block.box(0.025,14,14,2,16,16), Block.box(14,0,0,16,2,2), Block.box(0.025,14,0,2,16,2), Block.box(0.025,0,0,2,2,2), Block.box(14,14,0,16,16,2), Block.box(15,15,2,16,16,14), Block.box(15,0,2,16,1,14), Block.box(2,0,15,14,1,16), Block.box(2,15,15,14,16,16), Block.box(2,15,0,14,16,1), Block.box(2,0,0,14,1,1), Block.box(15,2,15,16,14,16), Block.box(15,2,0,16,14,1)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape Shape_D = Stream.of(Block.box(1,0,1,15,15,15), Block.box(0,0.025,14,2,2,16), Block.box(0,14,0,2,16,2), Block.box(0,14,14,2,16,16), Block.box(0,0.025,0,2,2,2), Block.box(14,14,14,16,16,16), Block.box(14,0.025,0,16,2,2), Block.box(14,0.025,14,16,2,16), Block.box(14,14,0,16,16,2), Block.box(2,15,0,14,16,1), Block.box(2,15,15,14,16,16), Block.box(0,2,15,1,14,16), Block.box(0,2,0,1,14,1), Block.box(15,2,0,16,14,1), Block.box(15,2,15,16,14,16), Block.box(0,15,2,1,16,14), Block.box(15,15,2,16,16,14)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    protected static Set<BlockEntityType<?>> VALID_ENTITY_TYPES;

    public enum TYPE {
        SERIAL,
        PARALLEL,
        SERIAL_HEAT,
        PARALLEL_HEAT,
        PROXY,
        PROXY_HEAT,
        ENERGY,
        ENERGY_HEAT;

        
        public boolean isHeatConducting(){
            return this.name().endsWith("_HEAT");
        };
    }

    public final TYPE type;

    public InputHatchBlock(TYPE type) {
        super(ModBlocks.defaultMetal);
        this.type = type;
        registerDefaultState(defaultBlockState().setValue(ATTACHED, false).setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull Builder<Block, BlockState> pBuilder) {
        pBuilder.add(ATTACHED, FACING);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        // DysonSphere.LOGGER.info("InputHatchBlock: newBlockEntity: type: {}, state: {}", type, pState.getBlock().getName());
        switch (type) {
            case SERIAL:
                return new InputHatchTile.Serial(pPos, pState);
            case SERIAL_HEAT:
                return new InputHatchTile.SerialHeat(pPos, pState);
            case PARALLEL:
                return new InputHatchTile.Parallel(pPos, pState);
            case PARALLEL_HEAT:
                return new InputHatchTile.ParallelHeat(pPos, pState);
            case ENERGY:
                return new InputHatchTile.Energy(pPos, pState);
            case ENERGY_HEAT:
                return new InputHatchTile.EnergyHeat(pPos, pState);
            case PROXY:
                return new InputHatchTile.Proxy(pPos, pState);
            case PROXY_HEAT:
                return new InputHatchTile.ProxyHeat(pPos, pState);
        }
        return null;
    }
    
    @Override
    public InteractionResult use(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer, @Nonnull InteractionHand pHand, @Nonnull BlockHitResult pHit) {
        // if(pPlayer.isShiftKeyDown()){
        //     if(!pLevel.isClientSide){
        //         pState = pState.cycle(ATTACHED);
        //         pLevel.setBlock(pPos, pState, 67);
        //     }
        //     return InteractionResult.CONSUME;
        // }
        

        if(!pLevel.isClientSide && pPlayer instanceof ServerPlayer serverPlayer){
            BlockEntity tile = pLevel.getBlockEntity(pPos);
            if(WrenchItem.isWrench(pPlayer.getItemInHand(pHand))){
                if(tile instanceof InputHatchTile hatchTile){
                    hatchTile.onPlacedInWorld(); //reload connections when hit with a wrench. Like all precision machinery.
                    return InteractionResult.SUCCESS;
                }
            }
            if(tile instanceof InputHatchTile.Serial serial){
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvider) ->
                new InputHatchSerialContainer(containerId, playerInventory, serial), Component.translatable("container.dysonsphere.input_hatch_serial" + (type.isHeatConducting() ? "_heat" : ""))), pPos);
                return InteractionResult.CONSUME;
            } else if(tile instanceof InputHatchTile.Parallel parallel){
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvider) ->
                new InputHatchParallelContainer(containerId, playerInventory, parallel), Component.translatable("container.dysonsphere.input_hatch_parallel" + (type.isHeatConducting() ? "_heat" : ""))), pPos);
                return InteractionResult.CONSUME;
            } else  if(tile instanceof InputHatchTile.Energy energy){
                NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((containerId, playerInventory, playerProvider) ->
                new InputHatchEnergyContainer(containerId, playerInventory, energy), Component.translatable("container.dysonsphere.input_hatch_energy" + (type.isHeatConducting() ? "_heat" : ""))), pPos);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos) {
        return false;
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if(/*!level.isClientSide() &&*/ level.getBlockEntity(pos) instanceof InputHatchTile tile){
            tile.onNeighborChange();
        }
    }

    @Override
    public void onRemove(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pNewState, boolean pMovedByPiston) {
        if(!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof InputHatchTile tile && !pMovedByPiston){
            tile.onRemove();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public void onPlace(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pOldState, boolean pMovedByPiston) {
        if(!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof InputHatchTile tile && !pMovedByPiston){
            tile.onPlacedInWorld();
        }
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

    @Override
    public boolean hasAnalogOutputSignal(@Nonnull BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos) {
        if(!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof InputHatchTile tile){
            return tile.getSignalStrength();
        }
        return 0;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level pLevel, @Nonnull BlockState pState, @Nonnull BlockEntityType<T> pBlockEntityType) {        
        if(VALID_ENTITY_TYPES == null){
            VALID_ENTITY_TYPES = Set.of(ModTiles.INPUT_HATCH_PARALLEL.get(), ModTiles.INPUT_HATCH_SERIAL.get(), ModTiles.INPUT_HATCH_PARALLEL_HEAT.get(), ModTiles.INPUT_HATCH_SERIAL_HEAT.get(),
                ModTiles.INPUT_HATCH_ENERGY.get(), ModTiles.INPUT_HATCH_PROXY.get(), ModTiles.INPUT_HATCH_ENERGY_HEAT.get(), ModTiles.INPUT_HATCH_PROXY_HEAT.get()); //Really need a better scaling solution for this.
        }
        return VALID_ENTITY_TYPES.contains(pBlockEntityType) ? (teLevel, pos, teState, tile) -> {
            ((InputHatchTile)tile).tick();
        } : null;
    }

    @Override
    public int getTintColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if(level != null && level.getBlockEntity(pos) instanceof ITintableTile tile){
            return tile.getTintColor(tintIndex);
        }
        return type.isHeatConducting() ? 0x00b4b4b4 :  0x00252525;
    }

    @Override
    public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
        if(pState.getValue(ATTACHED)){
            switch (pState.getValue(FACING)) {
                case DOWN: return Shape_D;
                case EAST: return Shape_E;
                case NORTH: return Shape_N;
                case SOUTH: return Shape_S;
                case UP: return Shape_U;
                case WEST: return Shape_W;
            }
        }
        return ShapeNeutral;
    }
}
