package de.bax.dysonsphere.capabilities.heat;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;
/** shameless adaption of IEnergyStorage
*   Enables transfer of heat, should split equally
*   heat in °Kelvin
*/
@AutoRegisterCapability
public interface IHeatContainer {
        /**
    * Adds heat to the storage. Returns quantity of heat that was accepted.
    *
    * @param maxReceive
    *            Maximum amount of heat to be inserted.
    * @param simulate
    *            If TRUE, the insertion will only be simulated.
    * @return Amount of heat that was (or would have been, if simulated) accepted by the storage.
    */
    double receiveHeat(double maxReceive, boolean simulate);

    /**
    * Removes heat from the storage. Returns quantity of heat that was removed.
    *
    * @param maxExtract
    *            Maximum amount of heat to be extracted.
    * @param simulate
    *            If TRUE, the extraction will only be simulated.
    * @return Amount of heat that was (or would have been, if simulated) extracted from the storage.
    */
    double extractHeat(double maxExtract, boolean simulate);

    /**
    * Returns the amount of heat currently stored.
    */
    double getHeatStored();

    /**
    * Returns the maximum amount of heat that can be stored.
    */
    double getMaxHeatStored();

    /**
     * Returns the multiplier of transferable heat. 0.0 - 1.0
     * 0 means no transfer at all; 1 means 100% heat gets moved at once
     */
    double getThermalConductivity();

}
