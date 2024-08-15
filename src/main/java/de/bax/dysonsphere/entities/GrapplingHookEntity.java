package de.bax.dysonsphere.entities;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class GrapplingHookEntity extends ThrowableProjectile {

    protected boolean isDeployed = false;
    protected boolean isRecalling = false;
    protected ItemStack hookItem = ItemStack.EMPTY;
    protected float gravity = 0f;
    protected double maxDistance = 64d;
    protected float winchForce = 3f;

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
        return gravity;
    }

    public void setGrapplingHookParameters(ItemStack hookItem, float gravity, double maxDistance, float winchForce){
        this.hookItem = hookItem;
        this.gravity = gravity;
        this.maxDistance = maxDistance;
        this.winchForce = winchForce;
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
                this.setDeltaMovement(this.getOwner().getPosition(0).subtract(this.position()).normalize().scale(getWinchForce()));
            }
            if(this.getPosition(0).distanceTo(this.getOwner().getPosition(0)) < 1){
                removeHook();
            }
        }
        if(getOwner() != null){
            getOwner().getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
                hookContainer.addHook(this);
            });
            if(this.distanceToSqr(getOwner()) > getMaxDistance() * getMaxDistance()){
                this.recall();
            }
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
            getOwner().getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
                hookContainer.removeHook(this);
            });
        }
    }

    

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if(getOwner() != null){
            getOwner().getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
                hookContainer.addHook(this);
            });
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putBoolean("deployed", isDeployed);
        pCompound.putBoolean("recalling", isRecalling);
        pCompound.put("hookItem", hookItem.save(new CompoundTag()));
        pCompound.putFloat("gravity", gravity);
        pCompound.putDouble("maxDistance", maxDistance);
        pCompound.putFloat("winchForce", winchForce);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        isDeployed = pCompound.getBoolean("deployed");
        isRecalling = pCompound.getBoolean("recalling");
        hookItem = ItemStack.of(pCompound.getCompound("hookItem"));
        gravity = pCompound.getFloat("gravity");
        maxDistance = pCompound.getDouble("maxDistance");
        winchForce = pCompound.getFloat("winchForce");
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
            return deployedAt().subtract(playerPos).normalize().scale(getWinchForce());
        }
        return Vec3.ZERO;
    }

    public float getWinchForce(){
        return winchForce;
    };

    public double getMaxDistance(){
        return maxDistance;
    }

    public void recall(){
        this.isDeployed = false;
        this.isRecalling = true;
        this.setDeltaMovement(this.getOwner().getPosition(0).subtract(this.position()).normalize().scale(getWinchForce()));
    };

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return pDistance <= getMaxDistance() * getMaxDistance();
    }

    
    
}
