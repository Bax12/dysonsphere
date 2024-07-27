package de.bax.dysonsphere.capabilities.heat;

public interface IHeatTile {
    
    default double getHeat(){
        return getHeatContainer().getHeatStored();
    }

    IHeatContainer getHeatContainer();

}
