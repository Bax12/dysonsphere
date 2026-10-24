package de.bax.dysonsphere.tileentities;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.blocks.LaserRedirectorBlock;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.ILaserReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class LaserRedirectorTile extends BaseTile implements ILaserReceiver {

    protected LazyOptional<ILaserReceiver> lazyLaser = LazyOptional.of(() -> this);

    public double energy = 0;
    public int emittedLength = 0;
    
    protected int ticksElapsed = 0;
    protected int lastLength = 0;

    public LaserRedirectorTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public LaserRedirectorTile(BlockPos pos, BlockState state) {
        super(ModTiles.LASER_REDIRECTOR.get(), pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.LASER_RECEIVER)){
            return lazyLaser.cast();
        }
        return super.getCapability(cap, side);
    }

    public static class Toggle extends LaserRedirectorTile {

        protected boolean toggled = false;

        public Toggle(BlockPos pos, BlockState state) {
            super(ModTiles.LASER_REDIRECTOR_TOGGLE.get(), pos, state);
        }

        //could probably live in the block class instead.
        public void setToggled(boolean hasNeighborSignal) {
            if(toggled != hasNeighborSignal){
                toggled = hasNeighborSignal;
                BlockState state = this.getBlockState();
                if(state.getBlock() instanceof LaserRedirectorBlock){
                    state = state.setValue(BlockStateProperties.FACING, state.getValue(BlockStateProperties.FACING).getOpposite());
                    setChanged();
                    if(!level.isClientSide()){
                        level.setBlock(worldPosition, state, 67);
                    }
                }
                
            }
        }

        @Override
        protected void saveAdditional(@Nonnull CompoundTag pTag) {
            super.saveAdditional(pTag);
            pTag.putBoolean("toggled", toggled);
        }

        @Override
        public void load(@Nonnull CompoundTag pTag) {
            super.load(pTag);
            toggled = pTag.getBoolean("toggled");
        }
        
    }

    public void tick(){
        if(!level.isClientSide && ++ticksElapsed % 5 == 0){
            if(energy > 0){
                emitLaser(this.getBlockState().getValue(BlockStateProperties.FACING));
            } else {
                emittedLength = 0;
            }
            if(lastLength != emittedLength){
                lastLength = emittedLength;
                this.sendSyncPackageToNearbyPlayers();
            }
        }
    }

    @Override
    public void receiveLaserEnergy(double energy) {
        this.energy += energy;
        setChanged();
    }

    protected void emitLaser(Direction dir){
        BlockHitResult hitResult = level.clip(new ClipContext(this.getBlockPos().getCenter().relative(dir, .495), this.getBlockPos().getCenter().relative(dir, 1500), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));

        if(hitResult.getType().equals(HitResult.Type.MISS)){
            emittedLength = 1500;
            this.energy -= 1000;
            return;
        }

        BlockPos hitPos = hitResult.getBlockPos();
        BlockState hitState = level.getBlockState(hitPos);

        emittedLength = Math.abs(hitPos.get(dir.getAxis()) - worldPosition.get(dir.getAxis()));

        if(!hitState.isAir()){
            BlockEntity tile = level.getBlockEntity(hitPos);
            if(tile != null){
                if(tile.getCapability(DSCapabilities.LASER_RECEIVER, dir.getOpposite()).map((receiver) -> {
                    receiver.receiveLaserEnergy(energy);
                    this.energy = 0;
                    return true;
                }).orElse(false)){
                    return;
                } 
            }
            
            Explosion explosion = new Explosion(level, null, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), 0, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY);
            boolean flammable = hitState.isFlammable(level, hitPos, dir.getOpposite());
            float resistance = hitState.getExplosionResistance(level, hitPos, explosion);
            if(!flammable){
                resistance *= 2;
            }

            if(resistance * 1000 < energy){
                level.destroyBlock(hitPos, false, null);
            }
            this.energy -= resistance * 100;
            
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putDouble("energy", energy);
        pTag.putInt("length", emittedLength);
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        energy = pTag.getDouble("energy");
        emittedLength = pTag.getInt("length");
    }
    
}
