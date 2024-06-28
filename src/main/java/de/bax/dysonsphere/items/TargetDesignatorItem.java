package de.bax.dysonsphere.items;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.IOrbitalLaserContainer;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import de.bax.dysonsphere.entities.TargetDesignatorEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

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
            //todo: add orbitalattackpattern from entity based on orbitalattackindex of stack
            LazyOptional<IOrbitalLaserContainer> orbitalLaserContainer = entity.getCapability(DSCapabilities.ORBITAL_LASER);
            orbitalLaserContainer.ifPresent((orbitalLaser) -> {
                DysonSphere.LOGGER.info("TargetDesignatorItem spawnTargetDesignatorEntity activePatterSize: {}", orbitalLaser.getActivePatterns().size());

                List<OrbitalLaserAttackPattern> patterns = orbitalLaser.getActivePatterns();
                int index = getOrbitalStrikePatternIndex(stack);
                if(patterns.size() > index){
                    designator.setOrbitalAttackPattern(patterns.get(index));  
                    level.addFreshEntity(designator);
                }
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

    public static int getOrbitalStrikePatternIndex(ItemStack stack){
        return stack.getOrCreateTag().getInt("orbitalStrikePatternIndex");
    }

    public static void setOrbitalStrikePatternIndex(ItemStack stack, int index){
        stack.getOrCreateTag().putInt("orbitalStrikePatternIndex", index);
    }
}
