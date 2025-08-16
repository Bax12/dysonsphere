package de.bax.dysonsphere.capabilities.dysonSphere;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import de.bax.dysonsphere.capabilities.dsEnergyReciever.IDSEnergyReceiver;
import de.bax.dysonsphere.constructs.Construct;
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
     * Bulk add amount of item. 
     * @param stack the stack representing the item to add
     * @param amount how many items to add
     * @return the added amount.
     */
    int addDysonSpherePartBulk(ItemStack stack, int amount);

    /**
     * Remove a part to the Dyson Sphere
     * @param stack
     *      the ItemStack to be removed from the DysonSphere (aka launched into the sun orbit)
     * @param simulate
     *      if TRUE the ItemStack will only be simulated to be removed
     * @return
     *      TRUE if the ItemStack was removed successfully (or can be removed if simulated)
     */
    boolean removeDysonSpherePart(ItemStack stack, boolean simulate);

    /**
     * Bulk remove amount of item. 
     * @param stack the stack representing the item to remove
     * @param amount how many items to remove
     * @return the removed amount.
     */
    int removeDysonSpherePartBulk(ItemStack stack, int amount);

    /**
     * Reset all parts in the Dyson Sphere to 0. 
     * Make sure this is not called on accident!
     * @return true if reset was successful
     */
    boolean resetDysonSphereParts();


    /**
     * Get all currently active parts of the Dyson Sphere
     * @return
     *      A ImmutableList of all currently active Parts of the Dyson Sphere.
     */
    Map<Item, Long> getDysonSphereParts();

    /**
     * Get the amount of a specific item in the DysonSphere
     * @param part the item to check
     * @return the amount of the item currently in the DysonSphere
     */
    long getDysonSpherePartCount(Item part);

    /**
     * Get the amount of a specific Ingredient in the DysonSphere
     * @param part the Ingredient to check
     * @return the amount of the items matching with the ingredient currently in the DysonSphere
     */
    long getDysonSpherePartCount(Predicate<ItemStack> item);

    /**
     * Add a new construct to the DysonSphere. May fail if already present
     * @param construct to add
     * @return if the construct was added successfully.
     */
    boolean addConstruct(Construct construct);

    /**
     * Remove a construct from the DysonSphere. Fails if the construct does not exist.
     * @param construct to remove
     * @return if the construct was successfully removed.
     */
    boolean removeConstruct(Construct construct);

    /**
     * Enable a existing construct, allowing it to work.
     * May fail if the construct does not exist or is already enabled
     * @param construct to enable
     * @return if the construct was successfully enabled
     */
    boolean enableConstruct(Construct construct);

    /**
     * Disable a existing construct, preventing it from working.
     * May fial if the construct does not exist or is already disabled
     * @param construct to disable
     * @return if the construct was successfully disabled
     */
    boolean disableConstruct(Construct construct);

    /**
     * @return Immutable list of all constructs, enabled and disable in the DysonSphere
     */
    List<Construct> getAllConstructs();

    /**
     * @return Immutable list of all enabled constructs in the DysonSphere
     */
    List<Construct> getEnabledConstructs();

    /**
     * @return Immutable list of all disabled constructs in the DysonSphere
     */
    List<Construct> getDisabledConstructs();

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
