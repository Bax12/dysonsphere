package de.bax.dysonsphere.capabilities.orbitalLaser;

import java.util.List;

import net.minecraft.nbt.CompoundTag;

public interface IOrbitalLaserContainer  {
    
    public int getLasersOnCooldown(int gameTick);

    public void putLasersOnCooldown(int gameTick, int laserCount, int cooldownDuration);

    public CompoundTag save(int gameTick);

    public void load(CompoundTag tag, int gameTick);

}
