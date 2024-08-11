package de.bax.dysonsphere.entities;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class GrapplingHookEntity extends ThrowableProjectile {

    protected boolean isDeployed = false;
    protected boolean isRecalling = false;

    public GrapplingHookEntity(EntityType<? extends ThrowableProjectile> type, Level world) {
        super(type, world);
    }

    public GrapplingHookEntity(EntityType<? extends ThrowableProjectile> type, LivingEntity thrower, Level world, float force) {
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


    public GrapplingHookEntity(LivingEntity thrower, Level world, float force) {
        this(ModEntities.GRAPPLING_HOOK.get(), thrower, world, force);
    }

    public GrapplingHookEntity(LivingEntity thrower, Level world) {
        this(ModEntities.GRAPPLING_HOOK.get(), thrower, world, 2.5f);
    }

    @Override
    protected void defineSynchedData() {
        
    }

    @Override
    protected float getGravity() {
        return 0;
    }

    @Override
    protected void onHitBlock(@Nonnull BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if(isRecalling) return;
        this.setDeltaMovement(0, 0, 0);
        this.isDeployed = true;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if(pResult.getEntity().equals(getOwner())){
            removeHook();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if((isRecalling || isDeployed) && this.getOwner() != null){
            if(isRecalling){
                this.setDeltaMovement(this.getOwner().getPosition(0).subtract(this.position()).normalize().scale(getForce()));
            }
            if(this.getPosition(0).distanceTo(this.getOwner().getPosition(0)) < 1){
                removeHook();
            }
        }
        if(getOwner() != null){
            getOwner().getCapability(DSCapabilities.GRAPPLING_HOOK).ifPresent((hookContainer) -> {
                hookContainer.addHook(this);
            });
        } else {
            this.discard();
        }
    }

    protected void removeHook(){
        // if(!level().isClientSide){
        //     getOwner().getCapability(DSCapabilities.GRAPPLING_HOOK).ifPresent((hookContainer) -> {
        //         hookContainer.removeHook(this);
        //     });
            this.discard();    
        // }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if(getOwner() != null){
            getOwner().getCapability(DSCapabilities.GRAPPLING_HOOK).ifPresent((hookContainer) -> {
                hookContainer.removeHook(this);
            });
        }
    }

    

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if(getOwner() != null){
            getOwner().getCapability(DSCapabilities.GRAPPLING_HOOK).ifPresent((hookContainer) -> {
                hookContainer.addHook(this);
            });
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putBoolean("deployed", isDeployed);
        pCompound.putBoolean("recalling", isRecalling);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        isDeployed = pCompound.getBoolean("deployed");
        isRecalling = pCompound.getBoolean("recalling");
    }


    public boolean isDeployed(){
        return this.isDeployed;
    };

    public Vec3 deployedAt(){
        if(isDeployed){
            return this.position();
        }
        return Vec3.ZERO;
    };

    public Vec3 appliedMotion(Vec3 playerPos){
        if(isDeployed()){           
            return deployedAt().subtract(playerPos).normalize().scale(getForce());
        }
        return Vec3.ZERO;
    }

    public float getForce(){
        return 3f;
    };

    public void recall(){
        this.isDeployed = false;
        this.isRecalling = true;
        this.setDeltaMovement(this.getOwner().getPosition(0).subtract(this.position()).normalize().scale(getForce()));
    };

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true; //todo make maxDistance + 1-2 for margin
    }

    
    
}
