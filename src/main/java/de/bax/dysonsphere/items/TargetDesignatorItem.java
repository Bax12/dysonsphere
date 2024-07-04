package de.bax.dysonsphere.items;

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
        spawnTargetDesignatorEntity(stack, level, entity);
        return getContainedStack(stack);
    }

    protected void spawnTargetDesignatorEntity(ItemStack stack, Level level, LivingEntity entity){
        if(!level.isClientSide){
            TargetDesignatorEntity designator = new TargetDesignatorEntity(entity, level);
            level.getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((ds) -> {
                entity.getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((orbitalLaser) -> {         
                    OrbitalLaserAttackPattern pattern = getOrbitalStrikePattern(stack);
                    if(ds.getUtilization() < 100 && ds.getDysonSpherePartCount(ModItems.CAPSULE_LASER.get()) >= pattern.getLasersRequired() + orbitalLaser.getLasersOnCooldown(entity.tickCount)){
                        designator.setOrbitalAttackPattern(pattern);  
                        level.addFreshEntity(designator);

                        orbitalLaser.putLasersOnCooldown(entity.tickCount, pattern.getLasersRequired(), pattern.getRechargeTime());
                    } else {
                        ((Player) entity).displayClientMessage(Component.literal("lasers on cooldown!"), true);
                    }
                });
            });
            
            
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 10;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        

        
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
        
    }

    @Override
    public @Nullable Entity createEntity(Level level, Entity location, ItemStack stack) {
        TargetDesignatorEntity designator = new TargetDesignatorEntity((LivingEntity) null, level);
        designator.setPos(location.getPosition(0.5f));
        return designator;
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        
        spawnTargetDesignatorEntity(item, player.level(), player);
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
