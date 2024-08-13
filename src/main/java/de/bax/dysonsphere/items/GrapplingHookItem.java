package de.bax.dysonsphere.items;

import java.util.List;

import org.joml.Vector3f;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GrapplingHookItem extends Item {

    public GrapplingHookItem() {
        super(new Item.Properties());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    protected void spawnHookEntity(ItemStack stack , Level level, LivingEntity entity, float force){
        if(!level.isClientSide){
            GrapplingHookEntity hook = new GrapplingHookEntity(entity, level, force);
            level.addFreshEntity(hook);
        }
    }


    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if(player.isShiftKeyDown()){
            player.getCapability(DSCapabilities.GRAPPLING_HOOK).ifPresent((hookContainer) -> {
                hookContainer.recallAll();
            });
        } else {
            spawnHookEntity(itemStack, level, player, 2.5f);
        }
        
        
        
        
        return InteractionResultHolder.consume(itemStack);
        
    }

    public static List<GrapplingHookEntity> getAttachedHooks(Player player, ItemStack stack){
        return List.of();
    }

    // @Override
    // public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
    //     super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
    //     player.getCapability(DSCapabilities.GRAPPLING_HOOK).ifPresent((hookContainer) -> {
    //         Vec3 hookMovement = hookContainer.getMotion(player.getPosition(0));
    //         Vec3 newMovement = hookMovement.add(player.getDeltaMovement());
    //         player.setDeltaMovement(newMovement);
    //     });
    // }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        // if(pLevel.isClientSide){
        if(pEntity instanceof Player player){
            player.getCapability(DSCapabilities.GRAPPLING_HOOK).ifPresent((hookContainer) -> {
                if(hookContainer.getDeployedHooks().size() > 0){
                    if(hookContainer.isPulling()){
                        Vec3 hookMovement = hookContainer.getMotion(pEntity.getPosition(1));
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
                            Vec3 hookMovement = hookContainer.getMotion(pEntity.getPosition(1)).normalize();
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
                            Vec3 hookMovement = hookContainer.getMotion(pEntity.getPosition(1)).normalize();
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

                                // if(player.isShiftKeyDown()){ //possible, but one its own more annoying then useful. Overloads controls and males unwind mode useless
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
        }
        // }
    }
    

}
