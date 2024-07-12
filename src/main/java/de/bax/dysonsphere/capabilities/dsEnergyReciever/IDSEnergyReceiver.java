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


    /**
     * Return the energy amount that can currently be drawn from the dysonsphere.
     * Defaults to reduce the amount when the DysonSphere is overloaded,
     * reducing all receivers by the overloaded percentage
     * @param dysonSphere the dysonsphere to receive energy from
     * @return the energy amount that can be currently received
     */
    default int getCurrentReceive(IDysonSphereContainer dysonSphere){
        if(!canReceive()) return 0;
            int receive = Math.min(getMaxReceive(), (int) dysonSphere.getDysonSphereEnergy());
            if(dysonSphere.getUtilization() > 100){
                receive = (int) Math.floor(receive * dysonSphere.getDysonSphereEnergy() / dysonSphere.getEnergyRequested());
            }
            return receive;
    };

    /**
     * Called from the dysonsphere when the parts in the dysonsphere change.
     * @param dysonSphere the changed dysonsphere
     */
    default void handleDysonSphereChange(IDysonSphereContainer dysonSphere) {};
}
