package de.bax.dysonsphere.capabilities.grapplingHook;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.LazyOptional;

@AutoRegisterCapability
public interface IGrapplingHookFrame {
    

    public default void tick(Player player){
        player.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
            if(hookContainer.getDeployedHooks().size() > 0){
                if(hookContainer.isPulling()){
                    Vec3 hookMovement = hookContainer.getMotion(player.getPosition(1));
                    if(player.getDeltaMovement().lengthSqr() < 1f){
                        hookMovement = hookMovement.scale(0.5f);
                    }
                    if(hookMovement.lengthSqr() > 0.01f){
                        hookMovement = hookMovement.scale(0.1);
                        // player.setDeltaMovement(player.getDeltaMovement().add(hookMovement));
                        player.addDeltaMovement(hookMovement);;
                    }
                } else if(hookContainer.isUnwinding()){ //fall into x/z of the hook, fall on y slower then gravity
                    if(!player.onGround()){
                        Vec3 hookMovement = hookContainer.getMotion(player.getPosition(1)).normalize();
                        Vec3 playerMovement = player.getDeltaMovement();
                        double xyLength = hookMovement.length();
                        double yMotion = playerMovement.y;
                        if(playerMovement.y < 0 && hookMovement.y > 0){
                            player.fallDistance = 1.0F;
                            yMotion = (playerMovement.y < 0 ? playerMovement.y * 0.5 : playerMovement.y) + (playerMovement.y * Math.min(1, 1 - (hookMovement.y / xyLength)));
                        }
                        player.setDeltaMovement(new Vec3(playerMovement.x + (hookMovement.x * (Math.max(hookMovement.y, 0)  / xyLength)/ 3) , yMotion, playerMovement.z + (hookMovement.z * (Math.max(hookMovement.y, 0)  / xyLength) / 3)));
                    }
                } else { //has to be stopped //fall into x/z of the hook, fall by y proportional to the x/z movement
                    if(!player.onGround()){
                        Vec3 hookMovement = hookContainer.getMotion(player.getPosition(1)).normalize();
                        Vec3 playerMovement = player.getDeltaMovement();
                        double xyLength = hookMovement.length();
                        double yMotion = playerMovement.y;
                        if(playerMovement.y < 0 && hookMovement.y > 0){
                            player.fallDistance = 1.0F;
                            yMotion = 2 * playerMovement.y * Math.min(1, 1 - (Math.abs(hookMovement.y) / xyLength));
                            // if(player instanceof LocalPlayer lPlayer){ //would need synchronization -- there is not space/jump input check on the server side
                            //     if(lPlayer.input.jumping){
                            //         yMotion += 0.1;
                            //     }
                            // }

                            // if(player.isShiftKeyDown()){ //possible, but one its own more annoying then useful. Overloads controls and makes unwind mode useless
                            //     yMotion -= 0.1;
                            // }
                        }
                        player.setDeltaMovement(new Vec3(playerMovement.x + (hookMovement.x * (Math.max(hookMovement.y, 0) / xyLength)/ 3) , yMotion, playerMovement.z + (hookMovement.z * (Math.max(hookMovement.y, 0) / xyLength) / 3)));
                    }

                }
            } else {
                // hookContainer.stopWinch();
            }
            
        });
    };

    public LazyOptional<IGrapplingHookEngine> getEngine();

    public LazyOptional<IGrapplingHookRope> getRope();

    public LazyOptional<IGrapplingHookHook> getHook();


    public default LazyOptional<Float> getDeployForce(Level level, Player player){
        return getEngine().lazyMap((engine) -> {
            float deployForce = getRope().lazyMap((rope) -> {
                return engine.getDeployForce(level, player) * rope.getDeployForceMultiplier(level, player);
            }).orElse(1f);
            return deployForce;
        });
    }

    public default LazyOptional<Float> getWinchForce(Level level, Player player){
        return getEngine().lazyMap((engine) -> {
            float winchForce = getRope().lazyMap((rope) -> {
                return engine.getWinchForce(level, player) * rope.getWinchForceMultiplier(level, player);
            }).orElse(1f);
            return winchForce;
        });
    }

    public default LazyOptional<Double> getMaxDistance(Level level, Player player){
        return getRope().lazyMap((rope) -> {
            return rope.getMaxDistance(level, player);
        });
    }

    public default LazyOptional<Integer> getMaxHooks(Level level, Player player){
        return getHook().lazyMap((hook) -> {
            return hook.getMaxHookCount(level, player);
        });
    }

    public default LazyOptional<Float> getGravity(Level level, Player player){
        return getHook().lazyMap((hook) -> {
            float gravity = getRope().lazyMap((rope) -> {
                return hook.getGravity(level, player) * rope.getHookGravityMultiplier(level, player);
            }).orElse(0f);
            return gravity;
        });
    }

    public default LazyOptional<ItemStack> getHookIcon(Level level, Player player){
        return getHook().lazyMap((hook) -> {
            return hook.getHookIcon(level, player);
        });
    }



}
