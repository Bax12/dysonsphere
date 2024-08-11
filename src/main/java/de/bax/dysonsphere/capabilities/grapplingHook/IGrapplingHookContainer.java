package de.bax.dysonsphere.capabilities.grapplingHook;

import java.util.List;

import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.world.phys.Vec3;

public interface IGrapplingHookContainer {

    public List<GrapplingHookEntity> getHooks();

    public default List<GrapplingHookEntity> getDeployedHooks(){
        return getHooks().stream().filter((hook) -> { return !hook.isRemoved() && hook.isDeployed();}).toList();
    }

    public default GrapplingHookEntity getNearestHook(Vec3 position){
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

    public default void recallAll(){
        getHooks().forEach((hook) -> {
            hook.recall();
        });
    }

    public void addHook(GrapplingHookEntity hook);

    public void removeHook(GrapplingHookEntity hook);



} 
