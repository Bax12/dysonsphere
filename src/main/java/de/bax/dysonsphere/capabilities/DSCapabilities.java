package de.bax.dysonsphere.capabilities;

import de.bax.dysonsphere.capabilities.dsEnergyReciever.IDSEnergyReceiver;
import de.bax.dysonsphere.capabilities.dsPart.IDSPart;
import de.bax.dysonsphere.capabilities.dysonSphere.IDysonSphereContainer;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class DSCapabilities {
    
    //TileEntity
    public static final Capability<IHeatContainer> HEAT = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<IDSEnergyReceiver> DS_ENERGY_RECEIVER = CapabilityManager.get(new CapabilityToken<>(){});

    //Items
    public static final Capability<IDSPart> DS_PART = CapabilityManager.get(new CapabilityToken<>(){});

    //Level
    public static final Capability<IDysonSphereContainer> DYSON_SPHERE = CapabilityManager.get(new CapabilityToken<>(){});
}
