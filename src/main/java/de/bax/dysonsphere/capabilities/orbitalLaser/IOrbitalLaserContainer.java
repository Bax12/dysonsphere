package de.bax.dysonsphere.capabilities.orbitalLaser;

import java.util.List;

import net.minecraft.nbt.CompoundTag;

public interface IOrbitalLaserContainer  {
    

    public List<OrbitalLaserAttackPattern> getActivePatterns();

    public int getLasersOnCooldown(int gameTick);

    public CompoundTag save(int gameTick);

    public void load(CompoundTag tag, int gameTick);

}
