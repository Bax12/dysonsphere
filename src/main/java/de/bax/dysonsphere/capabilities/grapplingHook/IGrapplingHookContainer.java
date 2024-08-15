 package de.bax.dysonsphere.capabilities.grapplingHook;

import java.util.List;
import java.util.Optional;

import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public interface IGrapplingHookContainer {

    public List<GrapplingHookEntity> getHooks();

    public default List<GrapplingHookEntity> getDeployedHooks(){
        return getHooks().stream().filter((hook) -> { return !hook.isRemoved() && hook.isDeployed();}).toList();
    }

    public default GrapplingHookEntity getNearestDeployedHook(Vec3 position){
        double minDistanceSqr = Double.MAX_VALUE;
        GrapplingHookEntity nearestHook = null;
        for (GrapplingHookEntity hook : getDeployedHooks()){
            double distanceSqr = hook.getPosition(0).distanceToSqr(position);
            if(distanceSqr < minDistanceSqr){
                minDistanceSqr = distanceSqr;
                nearestHook = hook;
            }
        }
        return nearestHook;
    }

    public default GrapplingHookEntity getNearestHookToLook(Vec3 position, Vec3 look) {
        float minAngle = 2;
        GrapplingHookEntity nearestHook = null;
        for (GrapplingHookEntity hook : getHooks()){
            float angle =  Math.abs(hook.appliedMotion(position).toVector3f().angle(look.toVector3f()));
            if(angle < minAngle){
                minAngle = angle;
                nearestHook = hook;
            }
        }
        return nearestHook;
    }

    public default Vec3 getMotion(Vec3 playerPos){
        return getDeployedHooks().stream().reduce(Vec3.ZERO, (a, b) -> 
        {
            if(!b.isAlive()) return a;
            return a.add(b.appliedMotion(playerPos));
        }, (a, b) -> 
        {
            return a.add(b);
        });
    }

    public default Vec3 getCentralVector(){
        int count = 0;
        Vec3 vector = Vec3.ZERO;
        for(var hook : getDeployedHooks()){
            vector = vector.add(hook.getPosition(0));
            count++;
        }
        vector = vector.scale(1d / count); //Basically vector divide
        return vector;
    }

    public default void recallAll(){
        getHooks().forEach((hook) -> {
            hook.recall();
        });
    }

    public void addHook(GrapplingHookEntity hook);

    public void removeHook(GrapplingHookEntity hook);

    public void deployHook();

    public void recallSingleHook();

    public void togglePulling();

    public void toggleUnwinding();

    public void stopWinch();

    public boolean isPulling();

    public boolean isUnwinding();

    public boolean isStopped();

    public CompoundTag save();

    public void load(CompoundTag tag);

    public Optional<IGrapplingHookFrame> getGrapplingHookFrame();

} 
