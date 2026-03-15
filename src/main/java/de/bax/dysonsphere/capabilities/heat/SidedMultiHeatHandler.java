package de.bax.dysonsphere.capabilities.heat;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

public class SidedMultiHeatHandler extends HeatHandler {

    protected LazyOptional<IHeatContainer> neighbor = LazyOptional.empty();

    public SidedMultiHeatHandler(double maxHeat){
        super(maxHeat);
    }

    public void updateNeighbors(Level level, BlockPos pos, Direction facing){
        BlockPos neiPos = pos.relative(facing);
            BlockEntity neighborTile = level.getBlockEntity(neiPos);
            if(neighborTile != null){
                LazyOptional<IHeatContainer> neighborHandler = neighborTile.getCapability(DSCapabilities.HEAT, facing.getOpposite());
                if (neighborHandler.isPresent()){
                    neighbor = neighborHandler;
                }
            } else if(shouldAirTrade() && level.getBlockState(neiPos).isAir()){
                neighbor = LazyOptional.of(() -> new AirHeatHandler());
            } else {
                neighbor = LazyOptional.empty();
            }
    }

    @Override
    public void updateNeighbors(Level level, BlockPos pos) {
        // use with direction instead.
    }

    @Override
    public void splitShare(){
        neighbor.ifPresent((neiHeat) -> {
            double diff = (neiHeat.getHeatStored() - this.getHeatStored()) / 2d;
            if(diff == 0){ //skip transfer logic when equal
                return;
            } else if(diff > 0){ //tranfer from neighbor to self when neighbor is greater
                double transfer = Math.min(neiHeat.extractHeat(diff, true), this.receiveHeat(diff, true));
                neiHeat.extractHeat(transfer, false);
                this.receiveHeat(transfer, false);
            } else { //transfer from self to neighbor if neighbor is lesser
                double transfer = Math.min(neiHeat.receiveHeat(-diff, true), this.extractHeat(-diff, true));
                neiHeat.receiveHeat(transfer, false);
                this.extractHeat(transfer, false);
            }
        });
    }
}
