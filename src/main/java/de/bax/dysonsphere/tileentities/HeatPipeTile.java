package de.bax.dysonsphere.tileentities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class HeatPipeTile extends BlockEntity {

    public static final double maxHeat = 1950;

    protected HeatHandler heatHandler = new HeatHandler(300, maxHeat);

    protected LazyOptional<IHeatContainer> lazyHeatContainer = LazyOptional.of(() -> heatHandler);

    protected int ticksElapsed = 0;
    protected double lastHeat = 0;

    public HeatPipeTile(BlockPos pos, BlockState state) {
        super(ModTiles.HEAT_PIPE.get(), pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.HEAT)){
            return lazyHeatContainer.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyHeatContainer.invalidate();
    }

    public void tick(){
        if(!level.isClientSide){
            if(ticksElapsed++ % 5 == 0){
                heatHandler.splitShare();
                if(lastHeat != heatHandler.getHeatStored()){
                    this.setChanged();
                    lastHeat = heatHandler.getHeatStored();
                }
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("Heat")) {
            heatHandler.deserializeNBT(tag.getCompound("heat"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Heat", heatHandler.serializeNBT());

    }

    public void onNeighborChange() {
        heatHandler.updateNeighbors(level, getBlockPos());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        heatHandler.updateNeighbors(level, getBlockPos());
    }

    

    

    

}
