package de.bax.dysonsphere.capabilities.grapplingHook;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.advancements.ModAdvancements;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.compat.ModCompat;
import de.bax.dysonsphere.compat.ad_astra.AdAstra;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import de.bax.dysonsphere.util.ConvexHullUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;

@AutoRegisterCapability
public interface IGrapplingHookFrame {
    

    public default void tick(Player player){
        player.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
            if(isFreeMoving()){
                //enable flight ability when deployed
                //limit flight to in between deployed hooks and constant gravity (in between hooks: yes, below hooks: yes, else no)
                //draw xz polygon, limit xz to within the polygon
                //-y limited by rope length from hook; +y limited by max hook y

                if(player.getAbilities().instabuild){
                    return;
                } 
                int hookCount = hookContainer.getDeployedHooks().size();
                if(hookCount > 0){ 
                    List<Double> yList = hookContainer.getDeployedHooks().stream().map((a) -> {return a.position().y;}).sorted().toList();
                    if(player.position().y + 0.05 < yList.get(yList.size()-1)){
                        if(canWinch(player.level(), player).orElse(false)){
                            player.getAbilities().flying = true;
                            
                            switch(hookCount){
                                case 1:
                                    if(!player.onGround()){
                                        this.onActiveWinchTick(player.level(), player);
                                        Vec3 hookMovement = hookContainer.getMotion(player.position()).normalize();
                                        
                                        double xyLength = hookMovement.length();
                                        
                                        Vec3 addedMovement = new Vec3((hookMovement.x * (xyLength / Math.max(hookMovement.y, 0) )/ 3) , 0, (hookMovement.z * (xyLength / Math.max(hookMovement.y, 0)) / 3));

                                        if(addedMovement.lengthSqr() < yList.get(yList.size()-1) - player.position().y){
                                            player.addDeltaMovement(addedMovement);    
                                        } else {
                                            player.getAbilities().flying = false;
                                        }
                                    } else {
                                        player.getAbilities().flying = false;
                                    }
                                break;
                                case 2:
                                    if(!player.onGround()){

                                        Point2D hook1 = new Point2D.Double(hookContainer.getDeployedHooks().get(0).position().x, hookContainer.getDeployedHooks().get(0).position().z);
                                        Point2D hook2 = new Point2D.Double(hookContainer.getDeployedHooks().get(1).position().x, hookContainer.getDeployedHooks().get(1).position().z);
                                        Point2D playerPos = new Point2D.Double(player.position().x, player.position().z);

                                        Point2D toLine = ConvexHullUtil.shortestVectorToHull(List.of(hook1, hook2), playerPos);
                                        this.onActiveWinchTick(player.level(), player);
                                        if(toLine.distanceSq(0, 0) > 0.1){
                                            // player.addDeltaMovement(hookContainer.getMotion(player.position()).normalize().multiply(0.25, 0, 0.25));
                                            // Vec3 movement = hookContainer.getNearestDeployedHook(player.position()).position().add(hookContainer.getCentralVector()).multiply(distance, 0, distance);
                                            if(toLine.distanceSq(0, 0) <  yList.get(yList.size()-1) - player.position().y){
                                                player.addDeltaMovement(new Vec3(toLine.getX(), 0, toLine.getY()).scale(0.1));
                                            } else {
                                                player.getAbilities().flying = false;
                                            }
                                        }
                                    } else {
                                        player.getAbilities().flying = false;
                                    }
                                break;
                                default:
                                    // Polygon hookShaperDelimiter = new Polygon();
                                    // GrapplingHookEntity hook1 = null, hook2 = null;
                                    // double minDist1 = Integer.MAX_VALUE, minDist2 = Integer.MAX_VALUE;
                                    // hookContainer.getDeployedHooks().stream().sorted(new Comparator<GrapplingHookEntity>() {
                                    //     @Override
                                    //     public int compare(GrapplingHookEntity arg0, GrapplingHookEntity arg1) {
                                    //         return (int) arg0.position().subtract(player.position()).toVector3f().angle(arg1.position().subtract(player.position()).toVector3f());
                                    //     }
                                    // }).forEach((hook) -> {
                                    //     var hookVec = player.position().subtract(hook.position()).multiply(10, 0, 10);

                                    //     hookShaperDelimiter.addPoint((int) hookVec.x, (int) hookVec.z);
                                    // });
                                        
                                        // if(hookVec.lengthSqr() < minDist2){
                                        //     if(hookVec.lengthSqr() <= minDist1){
                                        //         minDist2 = minDist1;
                                        //         hook2 = hook1;

                                        //         minDist1 = hookVec.lengthSqr();
                                        //         hook1 = hook;
                                        //     } else {
                                        //         minDist2 = hookVec.lengthSqr();
                                        //         hook2 = hook;
                                        //     }
                                        // }
                                        // }

                                    // if(!hookShaperDelimiter.getBounds().contains(0, 0) || !hookShaperDelimiter.contains(0, 0)){

                                        //get two closest hooks, calculate perpendicular vector, normalize and apply as movement
                                        // Vec3 movement = hook1.appliedMotion(player.position()).add(hook2.appliedMotion(player.position())).multiply(0.1, 0, 0.1);

                                        // double k = (((hook2.position().y - hook1.position().y) * (player.position().x - hook1.position().x)) - ((hook2.position().x - hook1.position().x) * (player.position().y- hook1.position().y))) / (((hook2.position().y - hook1.position().y) * (hook2.position().y - hook1.position().y)) + ((hook2.position().x - hook1.position().x) * (hook2.position().x - hook1.position().x)));
                                        // Vec3 movement = new Vec3(player.position().x - (k * (hook2.position().y -hook1.position().y)), 0, player.position().y + (k * (hook2.position().x - hook1.position().x)));

                                        // Vec3 movement = hookContainer.getNearestDeployedHook(player.position()).position().add(hookContainer.getCentralVector()).multiply(0.5, 0, 0.5);


                                        // player.setDeltaMovement(movement.subtract(player.position()).normalize().multiply(0.75, 0, 0.75).add(0, player.getDeltaMovement().y, 0));
                                        // player.addDeltaMovement(hookContainer.getMotion(player.position()).normalize().multiply(0.25, 0, 0.25));
                                        // var foo = new java.awt.geom.Line2D.Double();
                                        
                                        
                                        
                                    // }
                                    

                                    // double dist = this.getMaxDistance(player.level(), player).orElse(0f) - hookContainer.getCentralVector().distanceTo(player.position());
                                    // if(dist < 1 && dist > 0){
                                    //     player.addDeltaMovement(hookContainer.getMotion(player.position()).normalize().scale(1 - dist));
                                    // }

                                    this.onActiveWinchTick(player.level(), player);
                                    List<Point2D> points = new ArrayList<>();
                                    for(var hook :hookContainer.getDeployedHooks()){
                                        points.add(new Point2D.Double(hook.position().x, hook.position().z));
                                    }
                                    List<Point2D> hull = ConvexHullUtil.computeConvexHull(points);
                                    Point2D.Double horizontalPlayerPos = new Point2D.Double(player.position().x, player.position().z);
                                    if(!ConvexHullUtil.isInsideConvexPolygon(hull, horizontalPlayerPos)){

                                        Point2D toHull = ConvexHullUtil.shortestVectorToHull(hull, horizontalPlayerPos);
                                        // Vec3 movement = hookContainer.getNearestDeployedHook(player.position()).position().add(hookContainer.getCentralVector()).multiply(0.5, 0, 0.5);
                                        // player.setDeltaMovement(movement.subtract(player.position()).normalize().multiply(0.75, 0, 0.75).add(0, player.getDeltaMovement().y, 0));
                                        if(toHull.distanceSq(0, 0) <  yList.get(yList.size()-1) - player.position().y){
                                            player.addDeltaMovement(new Vec3(toHull.getX(), 0, toHull.getY()));
                                        } else {
                                            player.getAbilities().flying = false;
                                        }
                                    }
                                break;
                            }
                        } else {
                            player.getAbilities().flying = false;
                            if(!player.onGround()){
                                Vec3 hookMovement = hookContainer.getMotion(player.position()).normalize();
                                Vec3 playerMovement = player.getDeltaMovement();
                                double xyLength = hookMovement.length();
                                double yMotion = playerMovement.y;
                                if(playerMovement.y < 0 && hookMovement.y > 0){
                                    player.fallDistance = 1.0F;
                                    var hook = hookContainer.getNearestDeployedHook(player.position());
                                    if(hook.getMaxDistance() * hook.getMaxDistance() <= hook.distanceToSqr(player) + 5f){ //stop falling once reaching the end of the rope plus a little wiggle room
                                        yMotion = 0d;
                                    } else {
                                        yMotion = playerMovement.y * 0.5 + (playerMovement.y * Math.min(1, 1 - (hookMovement.y / xyLength)));
                                    }
                                }
                                player.setDeltaMovement(new Vec3(playerMovement.x + (hookMovement.x * (Math.max(hookMovement.y, 0)  / xyLength)/ 3) , yMotion, playerMovement.z + (hookMovement.z * (Math.max(hookMovement.y, 0)  / xyLength) / 3)));
                            }
                        }
                    } else {
                        player.getAbilities().flying = false;
                    }       
                } else {
                        player.getAbilities().flying = false;
                }
            } else {
                hookContainer.setIgnoreGravityChange(false);
                if(hookContainer.getDeployedHooks().size() > 0){
                    if(hookContainer.isPulling()){
                        if(this.canWinch(player.level(), player).orElse(false)){
                            Vec3 hookMovement = hookContainer.getMotion(player.position());
                            if(player.getDeltaMovement().lengthSqr() < 1f){
                                hookMovement = hookMovement.scale(0.5f);
                            }
                            if(hookMovement.lengthSqr() > 0.01f){
                                if(player instanceof ServerPlayer serverPlayer){
                                    ModAdvancements.HOOK_SPEED_TRIGGER.trigger(serverPlayer, (float) hookMovement.lengthSqr());
                                }
                                
                                hookMovement = hookMovement.scale(0.1);
                                // player.setDeltaMovement(player.getDeltaMovement().add(hookMovement));
                                player.addDeltaMovement(hookMovement);
                                
                                this.onActiveWinchTick(player.level(), player);
                            }
                        } else {
                            hookContainer.stopWinch();
                        }
                    } else if(hookContainer.isUnwinding()){ //fall into x/z of the hook, fall on y slower then gravity
                        if(this.canRappel(player.level(), player).orElse(false)){
                            if(!player.onGround()){
                                Vec3 hookMovement = hookContainer.getMotion(player.position()).normalize();
                                Vec3 playerMovement = player.getDeltaMovement();
                                // double xyLength = hookMovement.length(); //should always be 1 after normalizing... so completely useless
                                float gravity = 1f;
                                if(ModCompat.isLoaded(ModCompat.MODID.AD_ASTRA)){
                                    gravity = AdAstra.getGravity(player);
                                }
                                double yMotion = playerMovement.y;
                                if(playerMovement.y < 0 && hookMovement.y > 0){
                                    player.fallDistance = 1.0F;
                                    var hook = hookContainer.getNearestDeployedHook(player.position());
                                    if(hook.getMaxDistance() * hook.getMaxDistance() <= hook.distanceToSqr(player) + 5f){ //stop falling once reaching the end of the rope plus a little wiggle room
                                        yMotion = 0d;
                                    } else {
                                        yMotion = -0.05f * Math.min(1, gravity) + ( playerMovement.y * Math.min(1, 2 * (1 - (Math.abs(hookMovement.y))))); //fall based on gravity, but never faster
                                        this.onRappelTick(player.level(), player);
                                    }
                                }
                                
                                player.setDeltaMovement((playerMovement.x + (hookMovement.x * Math.max(hookMovement.y, 0) / 3)) * gravity , yMotion, (playerMovement.z + (hookMovement.z * Math.max(hookMovement.y, 0) / 3)) * gravity);
                                hookContainer.setIgnoreGravityChange(true); //prevent ad astra reduced gravity from pushing us upwards
                            }
                        } else {
                            hookContainer.stopWinch();
                        }
                        
                    } else { //has to be stopped //fall into x/z of the hook, fall by y proportional to the x/z movement
                        if(!player.onGround()){
                            Vec3 hookMovement = hookContainer.getMotion(player.position()).normalize();
                            Vec3 playerMovement = player.getDeltaMovement();
                            // double xyLength = hookMovement.length(); //should always be 1 after normalizing... so completely useless
                            double yMotion = playerMovement.y;
                            float gravity = 1f;
                            if(ModCompat.isLoaded(ModCompat.MODID.AD_ASTRA)){
                                gravity = AdAstra.getGravity(player);
                            }
                            if(playerMovement.y < 0 && hookMovement.y > 0){
                                player.fallDistance = 1.0F;
                                yMotion = playerMovement.y * Math.min(1, 2 * (1 - (Math.abs(hookMovement.y)))) * gravity;
                                // if(player instanceof LocalPlayer lPlayer){ //would need synchronization -- there is not space/jump input check on the server side
                                //     if(lPlayer.input.jumping){
                                //         yMotion += 0.1;
                                //     }
                                // }

                                // if(player.isShiftKeyDown()){ //possible, but on its own more annoying then useful. Overloads controls and makes unwind mode useless
                                //     yMotion -= 0.1;
                                // }
                            }
                            // DysonSphere.LOGGER.debug("IGrapplingHookFrame: gravity: {}", gravity);
                            Vec3 movement = new Vec3((playerMovement.x + (hookMovement.x * Math.max(hookMovement.y, 0) / 3)) * gravity , yMotion, (playerMovement.z + (hookMovement.z * Math.max(hookMovement.y, 0) / 3)) * gravity);
                            if(player instanceof ServerPlayer serverPlayer && movement.length() < 0.01f && hookMovement.y > 0 && !serverPlayer.level().getBlockState(serverPlayer.getOnPos()).entityCanStandOn(serverPlayer.level(), serverPlayer.getOnPos(), player)){
                                ModAdvancements.HOOK_HANGING_TRIGGER.trigger(serverPlayer);
                            }
                            player.setDeltaMovement(movement);
                            hookContainer.setIgnoreGravityChange(true);
                        }

                    }
                } else {
                    // hookContainer.stopWinch();
                }
                
            }
        });
    }

    public LazyOptional<IGrapplingHookEngine> getEngine();

    public LazyOptional<IGrapplingHookRope> getRope();

    public LazyOptional<IGrapplingHookHook> getHook();


    public default LazyOptional<Float> getLaunchForce(Level level, Player player){//DONE
        return getEngine().lazyMap((engine) -> {
            float deployForce = getRope().lazyMap((rope) -> {
                return engine.getLaunchForce(level, player) * rope.getLaunchForceMultiplier(level, player);
            }).orElse(1f);
            return deployForce;
        });
    }

    public default LazyOptional<Float> getWinchForce(Level level, Player player){//DONE
        return getEngine().lazyMap((engine) -> {
            float winchForce = getRope().lazyMap((rope) -> {
                return engine.getWinchForce(level, player) * rope.getWinchForceMultiplier(level, player);
            }).orElse(1f);
            return winchForce;
        });
    }

    public default LazyOptional<Float> getMaxDistance(Level level, Player player){//DONE
        return getRope().lazyMap((rope) -> {
            return rope.getMaxDistance(level, player);
        });
    }

    public default LazyOptional<Integer> getMaxHooks(Level level, Player player){//DONE
        return getHook().lazyMap((hook) -> {
            return hook.getMaxHookCount(level, player);
        });
    }

    public default LazyOptional<Float> getGravity(Level level, Player player){//DONE
        return getHook().lazyMap((hook) -> {
            float gravity = getRope().lazyMap((rope) -> {
                return hook.getGravity(level, player) * rope.getHookGravityMultiplier(level, player);
            }).orElse(0f);
            return gravity;
        });
    }

    public default LazyOptional<ItemStack> getHookIcon(Level level, Player player){//DONE
        return getHook().lazyMap((hook) -> {
            return hook.getHookIcon(level, player);
        });
    }

    public default LazyOptional<Boolean> canDeployAt(Level level, Player player, GrapplingHookEntity hook, BlockHitResult hitResult){//DONE
        return getHook().lazyMap((gHook) -> {
            return gHook.canDeployAt(level, player, hook, hitResult);
        });
    }

    public default LazyOptional<Boolean> canLaunch(Level level, Player player){//DONE
        return getEngine().lazyMap((engine) -> {
            return engine.canLaunch(level, player) && getRope().isPresent() && getHook().isPresent(); //always check the engine in case something down the line relies on it.
        });
    }

    public default LazyOptional<Boolean> canWinch(Level level, Player player){//DONE
        return getEngine().lazyMap((engine) -> {
            return engine.canWinch(level, player);
        });
    }

    public default LazyOptional<Boolean> canRappel(Level level, Player player){//DONE
        return getEngine().lazyMap((engine) -> {
            return engine.canRappel(level, player);
        });
    }

    public default void onHookLaunch(Level level, Player player, GrapplingHookEntity hook){//DONE
        getHook().ifPresent((gHook) -> {
            gHook.onHookLaunch(level, player, hook);
        });
        getRope().ifPresent((rope) -> {
            rope.onHookLaunch(level, player, hook);
        });
        getEngine().ifPresent((engine) -> {
            engine.onHookLaunch(level, player, hook);
        });
    }

    public default void onHookDeploy(Level level, Player player, GrapplingHookEntity hook){//DONE
        getHook().ifPresent((gHook) -> {
            gHook.onHookDeploy(level, player, hook);
        });
        getRope().ifPresent((rope) -> {
            rope.onHookDeploy(level, player, hook);
        });
    }

    public default void onHookRecall(Level level, Player player, GrapplingHookEntity hook){//DONE
        getHook().ifPresent((gHook) -> {
            gHook.onHookRecall(level, player, hook);
        });
    }

    public default void onHookOutOfRange(Level level, Player player, GrapplingHookEntity hook){//DONE
        getRope().ifPresent((rope) -> {
            rope.onHookOutOfRange(level, player, hook);
        });
    }

    public default void onActiveWinchTick(Level level, Player player){//DONE
        getEngine().ifPresent((engine) -> {
            engine.onActiveWinchTick(level, player);
        });
        getRope().ifPresent((rope) -> {
            rope.onActiveWinchTick(level, player);
        });
    }

    public default void onRappelTick(Level level, Player player){//DONE
        getEngine().ifPresent((engine) -> {
            engine.onRappelTick(level, player);
        });
        getRope().ifPresent((rope) -> {
            rope.onRappelTick(level, player);
        });
    }

    public default boolean isFreeMoving(){
        return getEngine().map((engine) -> {return engine.isFreeMoving();}).orElse(false);
    }


}
