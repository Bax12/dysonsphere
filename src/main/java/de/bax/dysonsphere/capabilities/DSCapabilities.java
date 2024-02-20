package de.bax.dysonsphere.capabilities;

import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class DSCapabilities {
    
    public static final Capability<IHeatContainer> HEAT = CapabilityManager.get(new CapabilityToken<>(){});

}
