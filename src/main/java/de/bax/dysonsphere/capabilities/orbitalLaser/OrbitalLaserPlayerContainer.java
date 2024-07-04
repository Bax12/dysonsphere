package de.bax.dysonsphere.capabilities.orbitalLaser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class OrbitalLaserPlayerContainer implements ICapabilitySerializable<CompoundTag> {

    OrbitalLaserContainer orbitalLaser = new OrbitalLaserContainer();
    LazyOptional<OrbitalLaserContainer> lazyOrbitalLaser = LazyOptional.of(() -> orbitalLaser);
    Player containingEntity;


    public OrbitalLaserPlayerContainer(Player player){
        containingEntity = player;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.ORBITAL_LASER)){
            return lazyOrbitalLaser.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return orbitalLaser.save(containingEntity.tickCount);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        orbitalLaser.load(nbt, containingEntity.tickCount);
    }

    public class OrbitalLaserContainer implements IOrbitalLaserContainer {

        protected Map<Integer, Integer> laserCooldowns = new TreeMap<Integer, Integer>();//key: gameTick to be available again. value: amount of lasers on this cooldown.


        public int getLasersOnCooldown(int gameTick){
            laserCooldowns.keySet().removeIf((key) -> {
                return key <= gameTick;
            });
            return laserCooldowns.values().stream().mapToInt(Integer::intValue).sum();
        }

        @Override
        public void putLasersOnCooldown(int gameTick, int laserCount, int cooldownDuration) {
            laserCooldowns.put(gameTick + cooldownDuration, laserCount);
        }

        public CompoundTag save(int gameTick){
            CompoundTag tag = new CompoundTag();
            CompoundTag laserTag = new CompoundTag();
            laserCooldowns.forEach((key, value) -> {
                int ticksRemaining = key - gameTick;
                if(ticksRemaining > 0){
                    laserTag.putInt(Integer.toString(ticksRemaining), value);
                }
            });
            tag.put("lasers", laserTag);
            return tag;
        }

        public void load(CompoundTag tag, int gameTick){
            laserCooldowns.clear();
            if(tag.contains("lasers")){
                tag.getCompound("lasers").getAllKeys().forEach((keyName) -> {
                    int key = Integer.parseInt(keyName);
                    int value = tag.getInt(keyName);
                    laserCooldowns.put(key, value);
                });
            }
        }




    }
}
