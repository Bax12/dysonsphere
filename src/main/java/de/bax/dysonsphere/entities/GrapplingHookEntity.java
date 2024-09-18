package de.bax.dysonsphere.entities;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.advancements.ModAdvancements;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;


public class GrapplingHookEntity extends ThrowableProjectile {

    // protected boolean isDeployed = false;
    // protected boolean isRecalling = false;
    // protected ItemStack hookItem = ItemStack.EMPTY;
    // protected float gravity = 0f;
    // protected double maxDistance = 64d;
    // protected float winchForce = 3f;

    private static final EntityDataAccessor<Boolean> DEPLOYED = SynchedEntityData.defineId(GrapplingHookEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RECALLING = SynchedEntityData.defineId(GrapplingHookEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> HOOK_ITEM = SynchedEntityData.defineId(GrapplingHookEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> ROPE_COLOR = SynchedEntityData.defineId(GrapplingHookEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(GrapplingHookEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> MAX_DISTANCE = SynchedEntityData.defineId(GrapplingHookEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WINCH_FORCE = SynchedEntityData.defineId(GrapplingHookEntity.class, EntityDataSerializers.FLOAT);

    public static final int maxRecallTicks = 40;
    protected int recallingTicks;

    public GrapplingHookEntity(EntityType<? extends ThrowableProjectile> type, Level world) {
        super(type, world);
    }

    public GrapplingHookEntity(EntityType<? extends ThrowableProjectile> type, LivingEntity thrower, Level world, float force) {
        super(type, thrower, world);

        this.setNoGravity(true);
        setOwner(thrower);
        if(thrower != null){
            setRot(thrower.getYRot() + 180, -thrower.getXRot());
        }
        
        double mx = Mth.sin(getYRot() / 180.0F * (float) Math.PI) * Mth.cos(getXRot() / 180.0F * (float) Math.PI) * force / 2D;
		double mz = -(Mth.cos(getYRot() / 180.0F * (float) Math.PI) * Mth.cos(getXRot() / 180.0F * (float) Math.PI) * force) / 2D;
		double my = Mth.sin(getXRot() / 180.0F * (float) Math.PI) * force / 2D;

        if(thrower != null){
            mx += thrower.getDeltaMovement().x;
            my += thrower.getDeltaMovement().y;
            mz += thrower.getDeltaMovement().z;
        }

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
        entityData.define(DEPLOYED, false);
        entityData.define(RECALLING, false);
        entityData.define(HOOK_ITEM, ItemStack.EMPTY);
        entityData.define(ROPE_COLOR, 0x0);
        entityData.define(GRAVITY, 0f);
        entityData.define(MAX_DISTANCE, 4096f);
        entityData.define(WINCH_FORCE, 1f);
    }

    public void setGrapplingHookParameters(ItemStack hookItem, int color, float gravity, float maxDistance, float winchForce){
        setHookStack(hookItem);
        setRopeColor(color);
        setGravity(gravity);
        setMaxDistance(maxDistance);
        setWinchForce(winchForce);
    }

    @Override
    protected void onHitBlock(@Nonnull BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if(isRecalling()) return;
        if(getOwner() instanceof Player player){
            player.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
                hookContainer.getGrapplingHookFrame().ifPresent((hookFrame) -> {
                    if(hookFrame.canDeployAt(level(), player, this, pResult).orElse(false)){
                        setPos(pResult.getLocation());
                        setDeployed(true);
                    }
                });
            });
        }
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
        if((isRecalling() || isDeployed()) && this.getOwner() != null){
            if(isRecalling()){
                this.setDeltaMovement(this.getOwner().position().subtract(this.position()).normalize().scale(getWinchForce()));
                if(++recallingTicks > maxRecallTicks){
                    removeHook();
                } 
            }
            if(this.position().distanceTo(this.getOwner().position()) < 1){
                removeHook();
            }
        }
        if(getOwner() instanceof Player player){
            player.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
                hookContainer.addHook(this);
                // DysonSphere.LOGGER.debug("GrapplingHookEntity: tick: ownerDistance: {}, maxDistance: {}", this.distanceToSqr(player), getMaxDistance() * getMaxDistance());
                if(this.distanceToSqr(player) > getMaxDistance() * getMaxDistance()){
                    // this.recall();
                    hookContainer.getGrapplingHookFrame().ifPresent((hookFrame) -> {
                        if(player instanceof ServerPlayer serverPlayer && this.isDeployed()){
                            ModAdvancements.HOOK_DETACH_TRIGGER.trigger(serverPlayer);
                        }
                        hookFrame.onHookOutOfRange(level(), player, this);
                    });
                }
            });
            
        } else {
            if(getOwner() == null && tickCount > 200){ //a ten second cleanup window, should prevent over-zealous hook removal and trash hooks
                this.discard();
            }
        }
        if (this.getGravity() > 0 && !isDeployed()) {
            Vec3 vec3 = this.getDeltaMovement();
            this.setDeltaMovement(vec3.x, vec3.y - (double)this.getGravity(), vec3.z);
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
    protected void addAdditionalSaveData(@Nonnull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        
        pCompound.putBoolean("deployed", isDeployed());
        pCompound.putBoolean("recalling", isRecalling());
        pCompound.put("hookItem", getHookStack().save(new CompoundTag()));
        pCompound.putInt("ropeColor", getRopeColor());
        pCompound.putFloat("gravity", getGravity());
        pCompound.putFloat("maxDistance", getMaxDistance());
        pCompound.putFloat("winchForce", getWinchForce());
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        setDeployed(pCompound.getBoolean("deployed"));
        setRecalling(pCompound.getBoolean("recalling"));
        setHookStack(ItemStack.of(pCompound.getCompound("hookItem")));
        setRopeColor(pCompound.getInt("ropeColor"));
        setGravity(pCompound.getFloat("gravity"));
        setMaxDistance(pCompound.getFloat("maxDistance"));
        setWinchForce(pCompound.getFloat("winchForce"));
    }

    @Override
    protected float getGravity() {
        return entityData.get(GRAVITY);
    }

    protected void setGravity(float gravity){
        entityData.set(GRAVITY, gravity);
    }

    public ItemStack getHookStack(){
        return entityData.get(HOOK_ITEM);
    }

    public void setHookStack(ItemStack hookStack){
        entityData.set(HOOK_ITEM, hookStack);
    }

    public int getRopeColor(){
        return entityData.get(ROPE_COLOR);
    }

    public void setRopeColor(int color){
        entityData.set(ROPE_COLOR, color);
    }

    public boolean isDeployed(){
        return entityData.get(DEPLOYED);
    };

    public void setDeployed(boolean deployed){
        entityData.set(DEPLOYED, deployed);
        if(deployed){
            this.setDeltaMovement(0, 0, 0);
            if(getOwner() instanceof Player player){
                player.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
                    hookContainer.getGrapplingHookFrame().ifPresent((hookFrame) -> {
                        hookFrame.onHookDeploy(level(), player, this);
                    });
                });
            }
        }
    }

    public boolean isRecalling(){
        return entityData.get(RECALLING);
    };

    public void setRecalling(boolean recalling){
        entityData.set(RECALLING, recalling);
        if(recalling){
            if(getOwner() instanceof Player player){
                player.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
                    hookContainer.getGrapplingHookFrame().ifPresent((hookFrame) -> {
                        hookFrame.onHookRecall(level(), player, this);
                    });
                });
            }
        }
    }

    public Vec3 deployedAt(){
        if(isDeployed()){
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
        return entityData.get(WINCH_FORCE);
    };

    public void setWinchForce(float winchForce){
        entityData.set(WINCH_FORCE, winchForce);
    }

    public float getMaxDistance(){
        return entityData.get(MAX_DISTANCE);
    }

    public void setMaxDistance(float maxDistance){
        entityData.set(MAX_DISTANCE, maxDistance);
    }

    public void recall(){
        // this.isDeployed = false;
        // this.isRecalling = true;
        setDeployed(false);
        setRecalling(true);
        
        this.setDeltaMovement(this.getOwner().position().subtract(this.position()).normalize().scale(getWinchForce()));
    };

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return pDistance <= getMaxDistance() * getMaxDistance();
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }


    
    
}
