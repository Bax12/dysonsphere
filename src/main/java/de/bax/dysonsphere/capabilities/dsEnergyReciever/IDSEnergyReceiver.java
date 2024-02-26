package de.bax.dysonsphere.capabilities.dsEnergyReciever;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IDSEnergyReceiver {
 
    /**
     * if the receiver can currently accept energy
     * @return true if ready to accept energy
     */
    boolean canReceive();


    /**
     * the amount of energy to be received per tick
     * @return the maximum amount of energy that can be received
     */
    int getMaxReceive();

}
