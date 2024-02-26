package de.bax.dysonsphere.capabilities.dsPart;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IDSPart {
    

    /**
     * the energy in RF the part providers per tick in the dyson Sphere
     * @return the RF provided in on Tick
     */
    int getEnergyProvided();

    /**
     * the amount the part contributes to the completion progress
     * @return the contribution of a single part
     */
    float getCompletionProgress();
}
