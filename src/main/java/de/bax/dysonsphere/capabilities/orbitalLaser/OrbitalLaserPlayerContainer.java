package de.bax.dysonsphere.capabilities.orbitalLaser;

import java.util.Map;
import java.util.TreeMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.dsEnergyReciever.IDSEnergyReceiver;
import de.bax.dysonsphere.capabilities.dysonSphere.IDysonSphereContainer;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.network.LaserCooldownSyncPackage;
import de.bax.dysonsphere.network.ModPacketHandler;
import de.bax.dysonsphere.tags.DSTags;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;

public class OrbitalLaserPlayerContainer implements ICapabilitySerializable<CompoundTag> {

    OrbitalLaserContainer orbitalLaser;
    LazyOptional<OrbitalLaserContainer> lazyOrbitalLaser = LazyOptional.of(() -> orbitalLaser);
    Player containingEntity;


    public OrbitalLaserPlayerContainer(Player player){
        containingEntity = player;
        orbitalLaser = new OrbitalLaserContainer();
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

    public class OrbitalLaserContainer implements IOrbitalLaserContainer, IDSEnergyReceiver {


        protected Map<Integer, Integer> laserCooldowns = new TreeMap<Integer, Integer>();//key: gameTick to be available again. value: amount of lasers on this cooldown.
        protected int dsLaserCount = -1;
        protected String currentSequence = "";

        protected LazyOptional<IDSEnergyReceiver> lazyDSReceiver = LazyOptional.of(() -> this);


        public OrbitalLaserContainer(){
            if(!containingEntity.level().isClientSide){
                containingEntity.level().getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((dysonsphere) -> {
                    dsLaserCount = dysonsphere.getDysonSphereEnergy() >= 0 ? dysonsphere.getDysonSpherePartCount(Ingredient.of(DSTags.itemCapsuleLaser)) : 0;
                    dysonsphere.registerEnergyReceiver(lazyDSReceiver);
                });
            }
        }


        public int getLasersOnCooldown(int gameTick){
            laserCooldowns.keySet().removeIf((key) -> {
                return key <= gameTick;
            });
            return laserCooldowns.values().stream().mapToInt(Integer::intValue).sum();
        }

        @Override
        public void putLasersOnCooldown(int gameTick, int laserCount, int cooldownDuration) {
            laserCooldowns.put(gameTick + cooldownDuration, laserCount);
            //trigger client sync, only Serverside has ServerPlayer
            if(containingEntity instanceof ServerPlayer serverPlayer){
                ModPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),new LaserCooldownSyncPackage(laserCooldowns, gameTick, dsLaserCount));
            }
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
            tag.putInt("dsLasers", dsLaserCount);
            return tag;
        }

        public void load(CompoundTag tag, int gameTick){
            laserCooldowns.clear();
            if(tag.contains("lasers")){
                CompoundTag laserTag = tag.getCompound("lasers");
                laserTag.getAllKeys().forEach((keyName) -> {
                    int key = Integer.parseInt(keyName);
                    int value = laserTag.getInt(keyName);
                    laserCooldowns.put(key, value);
                });
            }
            if(!containingEntity.level().isClientSide){
                containingEntity.level().getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((dysonsphere) -> {
                    dsLaserCount = dysonsphere.getDysonSphereEnergy() >= 0 ? dysonsphere.getDysonSpherePartCount(Ingredient.of(DSTags.itemCapsuleLaser)) : 0;
                    dysonsphere.registerEnergyReceiver(lazyDSReceiver);
                });
            } else {
                dsLaserCount = tag.getInt("dsLasers");
            }
        }

        @Override
        public int getTimeToNextCooldown(int gameTick) {
            laserCooldowns.keySet().removeIf((key) -> {
                return key <= gameTick;
            });
            return laserCooldowns.size() > 0 ? laserCooldowns.keySet().iterator().next() - gameTick : 0;
        }

        @Override
        public int getLasersAvailable(int gameTick) {
            int onCooldown = getLasersOnCooldown(gameTick);
            return Math.max(0, dsLaserCount - onCooldown);
        }

        @Override
        public void setDysonSphereLaserCount(int laserCount) {
            this.dsLaserCount = laserCount;
        }

        @Override
        public int getDysonSphereLaserCount() {
            return dsLaserCount;
        }

        @Override
        public boolean canReceive() {
            return false;
        }

        @Override
        public int getMaxReceive() {
            return 0;
        }

        @Override
        public void registerToDysonSphere(IDysonSphereContainer dysonSphere) {
            dysonSphere.registerEnergyReceiver(lazyDSReceiver);
        }

        @Override
        public void removeFromDysonSphere(IDysonSphereContainer dysonSphere) {
            dysonSphere.removeEnergyReceiver(lazyDSReceiver);
        }

        @Override
        public void handleDysonSphereChange(IDysonSphereContainer dysonSphere) {
            int count = dysonSphere.getDysonSphereEnergy() >= 0 ? dysonSphere.getDysonSpherePartCount(Ingredient.of(DSTags.itemCapsuleLaser)) : 0;//return 0 if dysonsphere is overloaded, not using utilization as we ignore the worldly consumers for our lasers
            if(count != dsLaserCount){
                dsLaserCount = count;
                if(containingEntity instanceof ServerPlayer serverPlayer){
                    ModPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),new LaserCooldownSyncPackage(laserCooldowns, containingEntity.tickCount, dsLaserCount));
                }
            }
        }

        @Override
        public void setCurrentInputSequence(String sequence) {
            if(sequence.matches(OrbitalLaserAttackPattern.validCallInChars) || sequence.equals("")){
                currentSequence = sequence;
            }
        }

        @Override
        public String getCurrentInputSequence() {
            return currentSequence;
        }




    }
}
