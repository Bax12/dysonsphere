package de.bax.dysonsphere.items;

import java.util.List;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
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
            hook.getId();
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
                    Vec3 hookMovement = hookContainer.getMotion(pEntity.getPosition(1));

                    if(player.getDeltaMovement().lengthSqr() < 1f){
                        hookMovement = hookMovement.scale(0.5f);
                    }

                    double distance = hookContainer.getNearestHook(player.getPosition(0)).getPosition(0).distanceToSqr(player.getPosition(0));
                    if(distance < 20){

                        DysonSphere.LOGGER.info("GrapplingHookItem inventoryTick: Hook near!");

                        hookMovement = hookMovement.scale(distance / 20);

                    }

                    if(hookMovement.lengthSqr() > 0.01f){
                        player.setNoGravity(true); //prevents jittering in suspension
                        
                        // Vec3 newMovement = hookMovement.add(pEntity.getDeltaMovement());
                        player.setDeltaMovement(hookMovement);
                        
                    }
                } else {
                    pEntity.setNoGravity(false);
                }
                
            });
        }
        // }
    }
    

}
