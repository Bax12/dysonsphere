package de.bax.dysonsphere.capabilities.orbitalLaser;

import net.minecraft.nbt.CompoundTag;

public interface IOrbitalLaserContainer  {
    
    public int getLasersOnCooldown(int gameTick);

    public int getLasersAvailable(int gameTick);

    public void putLasersOnCooldown(int gameTick, int laserCount, int cooldownDuration);

    public int getTimeToNextCooldown(int gameTick);

    public int getDysonSphereLaserCount();

    public void setDysonSphereLaserCount(int laserCount);

    public CompoundTag save(int gameTick);

    public void load(CompoundTag tag, int gameTick);

    public void setCurrentInputSequence(String sequence);

    public String getCurrentInputSequence();

}
