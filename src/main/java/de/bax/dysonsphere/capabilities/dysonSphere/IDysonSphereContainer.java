package de.bax.dysonsphere.capabilities.dysonSphere;

import com.google.common.collect.ImmutableMap;

import de.bax.dysonsphere.capabilities.dsEnergyReciever.IDSEnergyReceiver;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;

@AutoRegisterCapability
public interface IDysonSphereContainer {
    
    /**
     * Add a new part to the Dyson Sphere
     * @param stack
     *      the ItemStack to be added to the DysonSphere (aka launched into the sun orbit)
     * @param simulate
     *      if TRUE the ItemStack will only be simulated to be added
     * @return
     *      TRUE if the ItemStack was added successfully (or can be added if simulated)
     */
    boolean addDysonSpherePart(ItemStack stack, boolean simulate);


    /**
     * Get all currently active parts of the Dyson Sphere
     * @return
     *      A ImmutableList of all currently active Parts of the Dyson Sphere.
     */
    ImmutableMap<Item, Integer> getDysonSphereParts();


    /**
     * get the energy generation capacity of the Dyson Sphere
     * @return RF the DysonSphere can produce per tick
     */
    double getDysonSphereEnergy();

    /**
     * get the current completion of the Dyson Sphere
     * @return current completion 0.0-100.0
     */
    float getCompletionPercentage();

    /**
     * get the current % of used energy to available energy
     * @return current utilization 0.0-100.0
     */
    float getUtilization();

    /**
     * get the current energy provided by the Dyson Sphere to receivers
     * @return energy provided per Tick
     */
    double getEnergyProvided();

    /**
     * get the toptal energy requested from the Dyson Sphere
     * @return energy requester per Tick
     */
    double getEnergyRequested();


    /**
     * add a new @see IDSEnergyReceiver to the Dyson Sphere
     * wont be saved in the Dyson SPhere Container. receiver should call this method on every world load.
     * @param energyReceiver a LazyOptional of the IDSEnergyReceiver to be added
     */
    void registerEnergyReceiver(LazyOptional<IDSEnergyReceiver> energyReceiver);

    /**
     * remove a @see IDSEnergyReceiver from the Dyson Sphere
     * receiver should call this method when being separated from the Dyson Sphere.
     * On Removal or Destruction of the receiver invalidating the LazyOptional is enough.
     * @param energyReceiver a LazyOptional of the IDSEnergyReceiver to be removed
     */
    void removeEnergyReceiver(LazyOptional<IDSEnergyReceiver> energyReceiver);

}
