package de.bax.dysonsphere.capabilities.orbitalLaser;

import java.util.List;

import net.minecraft.nbt.CompoundTag;

public interface IOrbitalLaserContainer  {
    

    public List<OrbitalLaserAttackPattern> getActivePatterns();

    public void setActivePatterns(List<OrbitalLaserAttackPattern> patterns);

    public void setActivePattern(OrbitalLaserAttackPattern pattern, int index);

    public void addActivePattern(OrbitalLaserAttackPattern pattern);

    public int getLasersOnCooldown(int gameTick);

    public CompoundTag save(int gameTick);

    public void load(CompoundTag tag, int gameTick);

}
