package de.bax.dysonsphere.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;


public class TargetDesignatorEntity extends ThrowableProjectile {
    
    public int deployedAt = 0;
    protected int lifetime = 0;


    public TargetDesignatorEntity(EntityType<? extends ThrowableProjectile> type, Level world) {
        super(type, world);
    }

    public TargetDesignatorEntity(EntityType<? extends ThrowableProjectile> type, LivingEntity thrower, Level world) {
        super(type, thrower, world);
    
        setOwner(thrower);
        setRot(thrower.getYRot() + 180, -thrower.getXRot());
        
        float f = 2.5f;
        double mx = Mth.sin(getYRot() / 180.0F * (float) Math.PI) * Mth.cos(getXRot() / 180.0F * (float) Math.PI) * f / 2D;
		double mz = -(Mth.cos(getYRot() / 180.0F * (float) Math.PI) * Mth.cos(getXRot() / 180.0F * (float) Math.PI) * f) / 2D;
		double my = Mth.sin(getXRot() / 180.0F * (float) Math.PI) * f / 2D;
		this.push(mx, my, mz);

    }

    public TargetDesignatorEntity(LivingEntity thrower, Level world) {
        this(ModEntities.TARGET_DESIGNATOR.get(), thrower, world);
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
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        deployedAt = tag.getInt("deployedAt");
        lifetime = tag.getInt("lifetime");
    }

    @Override
    public void tick() {
        super.tick();
        if(this.lifetime >= getLiveTime() || (this.deployedAt > 0 && this.lifetime >= this.deployedAt + 120)){
            this.discard();
        }
        if(this.deployedAt > 0 && this.lifetime >= this.deployedAt + 10){
            //spawn in laser

            if(this.lifetime == this.deployedAt + 10){
                LaserStrikeEntity strike = new LaserStrikeEntity(this.level());
                strike.setPos(this.getPosition(1));
                strike.setHoming(level().getNearestEntity(LivingEntity.class, TargetingConditions.forCombat(), (LivingEntity) getOwner(), this.getX(), this.getY(), this.getZ(), getBoundingBox().inflate(10f)));
                if(this.getOwner() instanceof LivingEntity) strike.setOwner((LivingEntity) this.getOwner());
                this.level().addFreshEntity(strike);
            }
            // this.level().addParticle(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }

        
        lifetime++;
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
        return 750;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }


    
}
