package de.bax.dysonsphere.capabilities.heat;

import net.minecraft.core.Direction;

public interface ISidedHeatTile extends IHeatTile {
    
    public IHeatContainer getHeatContainer(Direction facing);

}
