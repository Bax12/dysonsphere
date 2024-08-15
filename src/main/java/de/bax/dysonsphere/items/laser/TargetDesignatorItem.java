package de.bax.dysonsphere.items.laser;

import org.antlr.v4.parse.ANTLRParser.localsSpec_return;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import de.bax.dysonsphere.entities.TargetDesignatorEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TargetDesignatorItem extends Item {


    public TargetDesignatorItem() {
        super(new Item.Properties());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        spawnTargetDesignatorEntity(stack, level, entity, 3.0f);
        return getContainedStack(stack);
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        spawnTargetDesignatorEntity(pStack, pLevel, pLivingEntity, 2.5f);
        pLivingEntity.setItemInHand(pLivingEntity.getUsedItemHand(), getContainedStack(pStack));
    }
        

    protected void spawnTargetDesignatorEntity(ItemStack stack, Level level, LivingEntity entity, float force){
        if(!level.isClientSide){
            TargetDesignatorEntity designator = new TargetDesignatorEntity(entity, level, force);
            // level.getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((ds) -> {
                entity.getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((orbitalLaser) -> {         
                    OrbitalLaserAttackPattern pattern = getOrbitalStrikePattern(stack);
                    // if(ds.getUtilization() < 100 && ds.getDysonSpherePartCount(ModItems.CAPSULE_LASER.get()) >= pattern.getLasersRequired() + orbitalLaser.getLasersOnCooldown(entity.tickCount)){
                    if(orbitalLaser.getLasersAvailable(entity.tickCount) >= pattern.getLasersRequired()) {
                        designator.setOrbitalAttackPattern(pattern);  
                        level.addFreshEntity(designator);

                        orbitalLaser.putLasersOnCooldown(entity.tickCount, pattern.getLasersRequired(), pattern.getRechargeTime());
                    } else {
                        ((Player) entity).displayClientMessage(Component.translatable("tooltip.dysonsphere.orbital_lasers_unavailable"), true);
                    }
                });
            // });
            
            
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        

        
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
        
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public @Nullable Entity createEntity(Level level, Entity location, ItemStack stack) {
        // TargetDesignatorEntity designator = new TargetDesignatorEntity((LivingEntity) null, level);
        // designator.setPos(location.getPosition(0.5f));
        ItemStack contained = getContainedStack(stack);

        return new ItemEntity(level, location.getX(), location.getY(), location.getZ(), contained, location.getDeltaMovement().x, location.getDeltaMovement().y, location.getDeltaMovement().z);
    }

    //just cancel the call-in
    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        
        // spawnTargetDesignatorEntity(item, player.level(), player);
        int selected = player.getInventory().selected;
        player.getInventory().setItem(selected, getContainedStack(item));
        return false;
    }

    public static ItemStack getContainedStack(ItemStack stack) {
        ItemStack contained = ItemStack.of(stack.getOrCreateTagElement("contained"));
        return contained;
    }

    public static void setContainedStack(ItemStack stack, ItemStack containedStack){
        CompoundTag tag = new CompoundTag();
        containedStack.save(tag);
        stack.addTagElement("contained", tag);
    }

    public static OrbitalLaserAttackPattern getOrbitalStrikePattern(ItemStack stack){
        var tag = stack.getOrCreateTag();
        if(tag.contains("pattern")){
            var pattern = new OrbitalLaserAttackPattern();
            pattern.deserializeNBT(tag.getCompound("pattern"));    
            return pattern;
        }
        return OrbitalLaserAttackPattern.EMPTY;
    }

    public static void setOrbitalStrikePattern(ItemStack stack, OrbitalLaserAttackPattern pattern){
        if(!pattern.isEmpty()){
            stack.getOrCreateTag().put("pattern", pattern.serializeNBT());
        }
    }
}
