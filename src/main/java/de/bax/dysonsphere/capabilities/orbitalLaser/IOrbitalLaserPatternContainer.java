package de.bax.dysonsphere.capabilities.orbitalLaser;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IOrbitalLaserPatternContainer {
    
    /**
     * Save a reference to the pattern
     * @param pattern
     */
    public void setPattern(OrbitalLaserAttackPattern pattern);

    /**
     * Load the pattern previously saved
     * @return
     */
    public OrbitalLaserAttackPattern getPattern();

    /**
     * If a pattern was set
     * @return true if no pattern was set
     */
    public boolean isEmpty();

}
