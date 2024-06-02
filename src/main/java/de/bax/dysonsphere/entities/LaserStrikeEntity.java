package de.bax.dysonsphere.entities;

import java.util.List;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public class LaserStrikeEntity extends Entity implements IEntityAdditionalSpawnData {


    protected int startAiming = 0;
    protected int startStriking = startAiming + 55;
    protected int startLingering = startStriking + 20;
    protected int end = startLingering + 1200;
    protected float dmg = 5f;
    protected int lifetime = 0;

    protected LivingEntity owner;

    public float size = 1f;


    public LaserStrikeEntity(EntityType<?> type, Level level) {
        super(type, level);
        
    }

    public LaserStrikeEntity(Level level){
        this(ModEntities.LASER_STRIKE.get(), level);
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide){
            if(this.lifetime >= getLifeTime()){
                this.discard();
            }
            // DysonSphere.LOGGER.info("LaserStrikeEntity tick isStriking: {}, lifetime: {}, lt - sS mod: {}", isStriking(), lifetime, (this.lifetime - startStriking) % 10);
            if(isStriking() && (this.lifetime - startStriking) % 10 == 0){
                damageAOE();
            }
        }
        lifetime++;
    }

    public LaserStrikeEntity setStartAiming(int startAiming) {
        this.startAiming = startAiming;
        return this;
    }

    public LaserStrikeEntity setStartStriking(int startStriking){
        this.startStriking = startStriking;
        return this;
    }

    public LaserStrikeEntity setStartLingering(int startLingering){
        this.startLingering = startLingering;
        return this;
    }

    public LaserStrikeEntity setEnd(int end){
        this.end = end;
        return this;
    }

    public LaserStrikeEntity setOwner(LivingEntity owner){
        this.owner = owner;
        return this;
    }

    public LaserStrikeEntity setHoming(LivingEntity target){
        this.startRiding(target, true);
        return this;
    }

    public LaserStrikeEntity setSize(float size){
        this.size = size;
        return this;
    }

    public LaserStrikeEntity setDamage(float dmg){
        this.dmg = dmg;
        return this;
    }

    protected void damageAOE(){
        float radius = size/2;
        AABB area = new AABB(this.position().subtract(size, 0.5f, size), this.position().add(size, level().getMaxBuildHeight() + 20, size));
        List<LivingEntity> targets = level().getEntitiesOfClass( LivingEntity.class, area);
        float radiusSq = radius * radius;
        for (LivingEntity target : targets) {
            if(target.equals(owner)) continue;
            if(getDistanceSqHorizontal(target) <= radiusSq){
                if(target.hurt(damageSources().lightningBolt(), dmg)){
                    target.setSecondsOnFire(2);
                    target.invulnerableTime = 0;
                }
                if(target.hurt(damageSources().mobProjectile(this, owner), dmg)){
                    target.invulnerableTime = 0;
                }
                
            }
        }
    }

    public double getDistanceSqHorizontal(LivingEntity target){
        double dx = this.getX() - target.getX();
        double dz = this.getZ() - target.getZ();
        return dx * dx + dz * dz;
    }

    public boolean isAiming(){
        return this.lifetime >= startAiming && this.lifetime < startStriking;
    }

    public boolean isStriking(){
        return this.lifetime >= startStriking && this.lifetime < startLingering;
    }

    public boolean isLingering(){
        return this.lifetime >= startLingering && this.lifetime < end;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    public int getLifeTime(){
        return end;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        
        if(tag.contains("startAiming")){
            startAiming = tag.getInt("startAiming");
        }
        if(tag.contains("startStriking")){
            startStriking = tag.getInt("startStriking");
        }
        if(tag.contains("startLingering")){
            startLingering = tag.getInt("startLingering");
        }
        if(tag.contains("end")){
            end = tag.getInt("end");
        }
        if(tag.contains("owner")){
            owner = this.level().getPlayerByUUID(tag.getUUID("owner"));
        }
        if(tag.contains("size")){
            size = tag.getFloat("size");
        }
        if(tag.contains("dmg")){
            dmg = tag.getFloat("dmg");
        }
        lifetime = tag.getInt("lifetime");
        
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("startAiming", startAiming);
        tag.putInt("startStriking", startStriking);
        tag.putInt("startLingering", startLingering);
        tag.putInt("end", end);
        if(owner instanceof Player){
            tag.putUUID("owner", owner.getUUID());
        }
        tag.putFloat("size", size);
        tag.putFloat("dmg", dmg);
        tag.putInt("lifetime", lifetime);
    }

    @Override
    protected void defineSynchedData() {
        
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(lifetime);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        this.lifetime = buffer.readInt();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    
}
