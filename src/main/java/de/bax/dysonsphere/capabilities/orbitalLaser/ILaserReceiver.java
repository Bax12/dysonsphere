package de.bax.dysonsphere.capabilities.orbitalLaser;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface ILaserReceiver {
    
    public void receiveLaserEnergy(double energy);

}
