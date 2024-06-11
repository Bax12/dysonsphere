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
        OrbitalLaserAttackPattern strike = new OrbitalLaserAttackPattern();
        strike.finishPattern();
        orbitalLaser.activePatterns.add(strike);
        DysonSphere.LOGGER.info("OrbitalLaserPlayerContainer constructor clientSide: {}", player.level().isClientSide);
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

        public List<OrbitalLaserAttackPattern> activePatterns = new ArrayList<>();


        public Map<Integer, Integer> laserCooldowns = new TreeMap<Integer, Integer>();


        public int getLasersOnCooldown(int gameTick){
            laserCooldowns.keySet().removeIf((key) -> {
                return key <= gameTick;
            });
            return laserCooldowns.values().stream().mapToInt(Integer::intValue).sum();
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
            CompoundTag patternTag = new CompoundTag();
            for (int i = 0; i < activePatterns.size(); i++){
                patternTag.put(""+i, activePatterns.get(i).serializeNBT());
            }
            tag.put("patterns", patternTag);
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
            activePatterns.clear();
            if(tag.contains("patterns")){
                CompoundTag patternTag = tag.getCompound("patterns");
                Set<String> keys = patternTag.getAllKeys();
                for(String key : keys){
                    OrbitalLaserAttackPattern pattern = new OrbitalLaserAttackPattern();
                    pattern.deserializeNBT(patternTag.getCompound(key));
                    activePatterns.add(pattern);
                }
            }
        }

        @Override
        public List<OrbitalLaserAttackPattern> getActivePatterns() {
            return activePatterns;
        }
    }
}
