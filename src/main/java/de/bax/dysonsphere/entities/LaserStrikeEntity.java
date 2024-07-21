package de.bax.dysonsphere.entities;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.ILaserReceiver;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import mekanism.api.math.FloatingLong;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

public class LaserStrikeEntity extends Entity implements IEntityAdditionalSpawnData {


    protected int startAiming = 0;
    protected int startStriking = startAiming + 55;
    protected int startLingering = startStriking + 200;
    protected int end = startLingering + 1200;
    protected float dmg = 5f;
    public float blockDmg = 1f;
    protected int lifetime = 0;
    protected float homingArea = 10f;
    protected float homingSpeed = 1f;


    protected LivingEntity owner;

    protected float size = 1f;
    protected float radius;

    protected LivingEntity homingTarget;
    // protected BlockInteractionUtil.AsyncBlockBreak asyncBlockBreak;

    public LaserStrikeEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        
    }

    public LaserStrikeEntity(Level level){
        this(ModEntities.LASER_STRIKE.get(), level);
    }

    @Override
    public void tick() {
        super.tick();
        
        if(this.lifetime >= getLifeTime()){
            this.discard();
        }
        if(isStriking()){
            moveToSurface();
            if(isHoming()){
                //move towards nearest entity by homingSpeed, running on client and server prevents stuttering movement
                if(homingTarget == null || homingTarget.isDeadOrDying()){
                    homingTarget = level().getNearestEntity(LivingEntity.class, TargetingConditions.forCombat(), owner, this.getX(), this.getY(), this.getZ(), new AABB(this.getPosition(1).add(homingArea, level().getMaxBuildHeight(), homingArea), this.getPosition(1f).subtract(homingArea, homingArea, homingArea)));
                } 
                if(homingTarget != null) {
                    Vec3 distance = homingTarget.getPosition(1f).subtract(this.getPosition(1f));
                    distance = distance.multiply(1, 0, 1);
                    if(distance.lengthSqr() > homingArea * homingArea){
                        homingTarget = null;
                    } else {
                        if(distance.lengthSqr() > homingSpeed){
                            distance = distance.scale(homingSpeed * homingSpeed / distance.lengthSqr());
                        }
                        // this.move(MoverType.SELF, distance);
                        this.setPos(this.getX() + distance.x, this.getY() + distance.y, this.getZ() + distance.z);
                    }
                }
            }
            if((this.lifetime - startStriking) % 10 == 0){
                if(!level().isClientSide){
                    damageAOE();
                    // if(asyncBlockBreak != null){
                    //     asyncBlockBreak.blocksToBreak.forEach((pos) -> {
                    //         level().destroyBlock(pos, false, this);
                    // }); 
                    // }
                    // if(asyncBlockBreak == null || !asyncBlockBreak.isAlive()){
                        dealBlockDamage();
                    // }
                }
            }
            
        }
        lifetime++;
    }

    protected void moveToSurface(){
        Vec3 startPos = new Vec3(this.getX(), level().getMaxBuildHeight(), this.getZ());
        Vec3 endPos = new Vec3(this.getX(), level().getMinBuildHeight(), this.getZ());
        BlockHitResult hit = this.level().clip(new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        this.setPos(this.getX(), hit.getLocation().y - 0.55f, this.getZ());
    }

    protected void dealBlockDamage(){
        if(level().isClientSide) return;
        if(this.blockDmg == 0)  return;
        float radius = Math.max(0.75f, (size / 10f) - 1f);
        
        Stream<BlockPos> blocks = BlockPos.betweenClosedStream(new AABB(this.getX() + radius, this.getY() + 20, this.getZ() + radius, this.getX() - radius, this.getY(), this.getZ() -radius));
        
        
        
        Explosion explosion = new Explosion(level(), this, this.getX(), this.getY(), this.getZ(), 0, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY);
        blocks.forEach((pos) -> {
            // DysonSphere.LOGGER.debug("laserStrikeEntity dealBLockDamage horizonal: {} radius: {} canSeeSky: {}", entity.horizontalDistanceSqr(pos), radius, level.canSeeSkyFromBelowWater(pos));
            if(this.horizontalDistanceSqr(pos) <= radius * radius && level().canSeeSkyFromBelowWater(pos.above())){ //take above to prevent inconsistent behavior on full blocks above see level
                BlockState state = level().getBlockState(pos);
                // DysonSphere.LOGGER.debug("laserStrikeEntity dealBlockDamage state: {}, pos: {}", state.getBlock().getName(), pos);
                if(!state.isAir()){
                    BlockEntity tile = level().getBlockEntity(pos);
                    if(tile != null){
                        Optional<ILaserReceiver> optionalReceptor = tile.getCapability(DSCapabilities.LASER_RECEIVER, Direction.UP).resolve();
                        if(optionalReceptor.isPresent()){
                            optionalReceptor.get().receiveLaserEnergy((dmg * blockDmg * 20_000d));
                            return;
                        }
                    }
                    boolean flammable = state.isFlammable(level(), pos, Direction.UP);
                    // Explosion explosion = level().explode(this, this.getX(), this.getY(), this.getZ(), 0, ExplosionInteraction.NONE);
                    
                    float resistance = state.getExplosionResistance(level(), pos, explosion);
                    
                    // DysonSphere.LOGGER.info("LaserStrikeEntity dealBlockDamage flammable: {}, explosion: {}, resistance: {}", flammable, explosion.interactsWithBlocks(), resistance);
                    if(!flammable){
                        resistance *= 2;
                    }
                    // DysonSphere.LOGGER.debug("laserStrikeEntity dealBLockDamage resistance: {} blockDmg: {} ", resistance, entity.blockDmg);
                    if(resistance < this.blockDmg){
                        
                        level().destroyBlock(pos, false, this);
                        
                        // DysonSphere.LOGGER.debug("laserStrikeEntity dealBLockDamage blockBroken: {} ", pos);
                        // blocksToBreak.add(new BlockPos(pos));
                    }
                }
            }
        });             

    }

    public double horizontalDistanceSqr(BlockPos pos){
        double x = (double)pos.getX() + 0.5d - this.getX();
        double z = (double)pos.getZ() + 0.5d - this.getZ();
        return x * x + z * z;
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

    public LaserStrikeEntity setAiming(LivingEntity target){
        this.startRiding(target, true);
        return this;
    }

    public LaserStrikeEntity setSize(float size){
        if(size <= 0){
            this.size = 0.1f;
        } else {
            this.size = size;
        }
        return this;
    }

    public LaserStrikeEntity setDamage(float dmg){
        this.dmg = dmg;
        return this;
    }

    public LaserStrikeEntity setBlockDamage(float blockDmg){
        this.blockDmg = blockDmg;
        return this;
    }

    public LaserStrikeEntity setHoming(float area, float speed){
        this.homingArea = area;
        this.homingSpeed = speed;
        return this;
    }

    public void setOrbitalStrikeParameters(OrbitalLaserAttackPattern pattern){
        setSize(pattern.strikeSize);

        if(pattern.aimingArea > 0){
            setAiming(level().getNearestEntity(LivingEntity.class, TargetingConditions.forCombat(), owner, this.getX(), this.getY(), this.getZ(), getBoundingBox().inflate(pattern.aimingArea)));
            startStriking = startAiming + pattern.callInDelay;
        } else {
            startStriking = 0;
        }
        setHoming(pattern.homingArea, pattern.homingSpeed);
        setDamage(pattern.damage);
        setBlockDamage(pattern.blockDamage);
        startLingering = startStriking + pattern.strikeDuration;
        radius = Math.max(0.5f, (size / 10f) - 1f);


    }

    public boolean isHoming() {
        return homingArea > 0 && homingSpeed > 0;
    }

    protected void damageAOE(){
        // float radius = size/10;
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

    @Override
    public boolean fireImmune() {
        return true;
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
        if(tag.contains("blockDmg")){
            blockDmg = tag.getFloat("blockDmg");
        }
        lifetime = tag.getInt("lifetime");
        radius = Math.max(0.5f, (size / 10f) - 1f);
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
        tag.putFloat("blockDmg", blockDmg);
        tag.putInt("lifetime", lifetime);
    }

    @Override
    protected void defineSynchedData() {
        
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(startAiming);
        buffer.writeInt(startStriking);
        buffer.writeInt(startLingering);
        buffer.writeInt(end);
        buffer.writeFloat(dmg);
        buffer.writeFloat(blockDmg);
        buffer.writeFloat(homingArea);
        buffer.writeFloat(homingSpeed);

        buffer.writeInt(lifetime);
        buffer.writeFloat(size);
        
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        this.startAiming = buffer.readInt();
        this.startStriking = buffer.readInt();
        this.startLingering = buffer.readInt();
        this.end = buffer.readInt();
        this.dmg = buffer.readFloat();
        this.blockDmg = buffer.readFloat();
        this.homingArea = buffer.readFloat();
        this.homingSpeed = buffer.readFloat();


        this.lifetime = buffer.readInt();
        this.size = buffer.readFloat();
        radius = Math.max(0.5f, (size / 10f) - 1f);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public float getSize() {
        return size;
    }

    public float getDamage() {
        return dmg;
    }

    public float getBlockDamage() {
        return blockDmg;
    }

    public float getHomingArea() {
        return homingArea;
    }

    public float getHomingSpeed() {
        return homingSpeed;
    }

    public LivingEntity getOwner() {
        return owner;
    }
    
    

}
