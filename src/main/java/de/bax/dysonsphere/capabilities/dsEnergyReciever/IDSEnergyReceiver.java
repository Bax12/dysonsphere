package de.bax.dysonsphere.capabilities.dsEnergyReciever;

import de.bax.dysonsphere.capabilities.dysonSphere.IDysonSphereContainer;
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

    /**
     * Add the receiver to the Dyson Sphere
     * Should be called whenever the receiver is loaded or created
     * @param dysonSphere the dysonSphere to add this receiver
     */
    void registerToDysonSphere(IDysonSphereContainer dysonSphere);

    /**
     * Remove the receiver from the DysonSphere
     * Should be called when the receiver is unloaded or destroyed
     * @param dysonSphere the dysonSphere to remove this receiver
     */
    void removeFromDysonSphere(IDysonSphereContainer dysonSphere);



    int getCurrentReceive(IDysonSphereContainer dysonSphere);
}
