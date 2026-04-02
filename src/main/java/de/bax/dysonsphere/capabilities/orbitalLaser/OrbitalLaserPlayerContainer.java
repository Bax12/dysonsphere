package de.bax.dysonsphere.capabilities.orbitalLaser;

import java.util.PriorityQueue;
import java.util.TreeMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DSConfig;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.dsEnergyReciever.IDSEnergyReceiver;
import de.bax.dysonsphere.capabilities.dysonSphere.IDysonSphereContainer;
import de.bax.dysonsphere.constructs.ModConstructs;
import de.bax.dysonsphere.network.LaserCooldownSyncPackage;
import de.bax.dysonsphere.network.ModPacketHandler;
import de.bax.dysonsphere.tags.DSTags;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
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

        public static record LaserCooldown(int gameTick, int count) {};

        protected PriorityQueue<LaserCooldown> laserCooldowns = new PriorityQueue<LaserCooldown>(10, (arg0, arg1) -> Integer.compare(arg0.gameTick(), arg1.gameTick()));//key: gameTick to be available again. value: amount of lasers on this cooldown.
        protected int dsLaserAvailableCount = -1;
        protected int dsLaserCooldownCount = -1;
        protected String currentSequence = "";
        protected boolean hasHeatSink;

        protected LazyOptional<IDSEnergyReceiver> lazyDSReceiver = LazyOptional.of(() -> this);

        


        public OrbitalLaserContainer(){
            if(!containingEntity.level().isClientSide()){
                containingEntity.level().getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((dysonsphere) -> {
                    dsLaserAvailableCount = (int) (dysonsphere.getDysonSphereEnergy() >= 0 ? dysonsphere.getDysonSpherePartCount(Ingredient.of(DSTags.itemCapsuleLaser)) : 0);
                    dysonsphere.registerEnergyReceiver(lazyDSReceiver);
                });
            }
        }

        @Override
        public int getLasersOnCooldown(int gameTick){
            tickCooldowns(gameTick);
            return dsLaserCooldownCount != -1 ? dsLaserCooldownCount : (dsLaserCooldownCount = laserCooldowns.stream().mapToInt(LaserCooldown::count).sum());
        }

        protected void tickCooldowns(int gameTick){
            LaserCooldown cooldown;
            while ((cooldown = laserCooldowns.peek()) != null && cooldown.gameTick() <= gameTick) {
                laserCooldowns.poll();
                dsLaserCooldownCount = -1; //at least on laser is of cooldown, so recalc the count.
            }
        }

        @Override
        public void putLasersOnCooldown(int gameTick, int laserCount, int cooldownDuration) {
            laserCooldowns.add(new LaserCooldown(gameTick + (hasHeatSink ? (int) (cooldownDuration * DSConfig.CONSTRUCT_HEAT_SINK_MULT_VALUE) : cooldownDuration), laserCount));

            dsLaserCooldownCount = -1; //at least on laser more is on cooldown, so recalc the count.

            //trigger client sync, only Serverside has ServerPlayer
            if(containingEntity instanceof ServerPlayer serverPlayer){
                ModPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new LaserCooldownSyncPackage(laserCooldowns, gameTick, dsLaserAvailableCount));
            }
        }

        public CompoundTag save(int gameTick){
            CompoundTag tag = new CompoundTag();
            CompoundTag laserTag = new CompoundTag();
            laserCooldowns.forEach((cooldown) -> {
                int ticksRemaining = cooldown.gameTick() - gameTick;
                if(ticksRemaining > 0){
                    laserTag.putInt(Integer.toString(ticksRemaining), cooldown.count());
                }
            });
            tag.put("lasers", laserTag);
            tag.putInt("dsLasers", dsLaserAvailableCount);
            return tag;
        }

        public void load(CompoundTag tag, int gameTick){
            laserCooldowns.clear();
            if(tag.contains("lasers")){
                CompoundTag laserTag = tag.getCompound("lasers");
                laserTag.getAllKeys().forEach((keyName) -> {
                    int key = Integer.parseInt(keyName);
                    int value = laserTag.getInt(keyName);
                    laserCooldowns.add(new LaserCooldown(key, value));
                });
            }
            if(!containingEntity.level().isClientSide){
                containingEntity.level().getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((dysonsphere) -> {
                    dsLaserAvailableCount = (int) (dysonsphere.getDysonSphereEnergy() >= 0 ? dysonsphere.getDysonSpherePartCount(Ingredient.of(DSTags.itemCapsuleLaser)) : 0);
                    dysonsphere.registerEnergyReceiver(lazyDSReceiver);
                });
            } else {
                dsLaserAvailableCount = tag.getInt("dsLasers");
            }
        }

        @Override
        public int getTimeToNextCooldown(int gameTick) {
            tickCooldowns(gameTick);
            return laserCooldowns.size() > 0 ? laserCooldowns.peek().gameTick() - gameTick : 0;
        }

        @Override
        public int getLasersAvailable(int gameTick) {
            int onCooldown = getLasersOnCooldown(gameTick);
            return Math.max(0, dsLaserAvailableCount - onCooldown);
        }

        @Override
        public void setDysonSphereLaserCount(int laserCount) {
            this.dsLaserAvailableCount = laserCount;
        }

        @Override
        public int getDysonSphereLaserCount() {
            return dsLaserAvailableCount;
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
            int count = (int) (dysonSphere.getDysonSphereEnergy() >= 0 ? dysonSphere.getDysonSpherePartCount(Ingredient.of(DSTags.itemCapsuleLaser)) : 0);//return 0 if dysonsphere is overloaded, not using utilization as we ignore the worldly consumers for our lasers
            if(count != dsLaserAvailableCount){
                dsLaserAvailableCount = count;
                if(containingEntity instanceof ServerPlayer serverPlayer){
                    ModPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),new LaserCooldownSyncPackage(laserCooldowns, containingEntity.tickCount, dsLaserAvailableCount));
                }
            }
            hasHeatSink = dysonSphere.getEnabledConstructs().contains(ModConstructs.HEAT_SINK.get());
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
