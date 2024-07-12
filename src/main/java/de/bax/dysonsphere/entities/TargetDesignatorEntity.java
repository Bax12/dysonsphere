package de.bax.dysonsphere.entities;

import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;


public class TargetDesignatorEntity extends ThrowableProjectile {
    
    public int deployedAt = 0;
    protected int lifetime = 0;
    protected OrbitalLaserAttackPattern orbitalAttackPattern = new OrbitalLaserAttackPattern();
    protected int maxLifeTime = 750;

    public TargetDesignatorEntity(EntityType<? extends ThrowableProjectile> type, Level world) {
        super(type, world);
    }

    public TargetDesignatorEntity(EntityType<? extends ThrowableProjectile> type, LivingEntity thrower, Level world, float force) {
        super(type, thrower, world);
    
        setOwner(thrower);
        if(thrower != null){
            setRot(thrower.getYRot() + 180, -thrower.getXRot());
        }
        float f = 2.5f;
        double mx = Mth.sin(getYRot() / 180.0F * (float) Math.PI) * Mth.cos(getXRot() / 180.0F * (float) Math.PI) * f / 2D;
		double mz = -(Mth.cos(getYRot() / 180.0F * (float) Math.PI) * Mth.cos(getXRot() / 180.0F * (float) Math.PI) * f) / 2D;
		double my = Mth.sin(getXRot() / 180.0F * (float) Math.PI) * f / 2D;
		this.push(mx, my, mz);

    }

    public TargetDesignatorEntity(LivingEntity thrower, Level world, float force) {
        this(ModEntities.TARGET_DESIGNATOR.get(), thrower, world, force);
    }

    public TargetDesignatorEntity(LivingEntity thrower, Level world) {
        this(ModEntities.TARGET_DESIGNATOR.get(), thrower, world, 2.5f);
    }

    @Override
    protected float getGravity() {
        return 0.05f;
    }

    

    @Override
    protected void defineSynchedData() {
        
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putInt("deployedAt", deployedAt);
        tag.putInt("lifetime", lifetime);
        tag.put("pattern", orbitalAttackPattern.serializeNBT());
        tag.putInt("maxLifeTime", maxLifeTime);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        deployedAt = tag.getInt("deployedAt");
        lifetime = tag.getInt("lifetime");
        orbitalAttackPattern.deserializeNBT(tag.getCompound("pattern"));
        maxLifeTime = tag.getInt("maxLifeTime");
    }

    @Override
    public void tick() {
        super.tick();
        if(this.lifetime >= getLiveTime()){
            this.discard();
        }
        if(this.deployedAt > 0 && this.lifetime >= this.deployedAt + 10){
            //spawn in laser
            int delay = (orbitalAttackPattern.aimingArea > 0 ? 0 : orbitalAttackPattern.callInDelay);
            if(this.lifetime >= this.deployedAt + 10 + delay){
                int waveTick = this.lifetime - (this.deployedAt + 10 + delay);
                if(orbitalAttackPattern.repeatDelay == 0){
                    if(waveTick == 0){
                        for(int i = 0; i < orbitalAttackPattern.strikeCount; i++){
                            spawnLaserStrike();
                        }
                    }
                } else {
                    if(waveTick % orbitalAttackPattern.repeatDelay == 0 && waveTick <= orbitalAttackPattern.repeatDelay * orbitalAttackPattern.strikeCount){
                        spawnLaserStrike();
                    }
                }
            }
            // this.level().addParticle(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }

        
        lifetime++;
    }

    protected void spawnLaserStrike(){
        if(level().isClientSide) return;
        LaserStrikeEntity strike = new LaserStrikeEntity(this.level());
        strike.setPos(this.getRandomX(orbitalAttackPattern.spreadRadius), this.getY(), this.getRandomZ(orbitalAttackPattern.spreadRadius));
        if(this.getOwner() instanceof LivingEntity) strike.setOwner((LivingEntity) this.getOwner());
        strike.setOrbitalStrikeParameters(orbitalAttackPattern);
        this.level().addFreshEntity(strike);
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        setDeltaMovement(0, 0, 0);
        if(deployedAt == 0){
            deployedAt = this.tickCount;
        } 
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        setDeltaMovement(0, 0, 0);
        if(deployedAt == 0){
            deployedAt = this.tickCount;
        }
        //start riding the hit entity - is this a good idea?
        this.startRiding(hitResult.getEntity());
    }

    public int getLiveTime(){
        return maxLifeTime;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    public void setOrbitalAttackPattern(OrbitalLaserAttackPattern orbitalLaserAttackPattern) {
        this.orbitalAttackPattern = orbitalLaserAttackPattern;
        this.maxLifeTime = orbitalLaserAttackPattern.callInDelay + (orbitalLaserAttackPattern.repeatDelay * orbitalLaserAttackPattern.strikeCount) + 120;
    }


    
}
