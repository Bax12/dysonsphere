package de.bax.dysonsphere.items;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.dysonSphere.IDysonSphereContainer;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import de.bax.dysonsphere.entities.TargetDesignatorEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.server.command.TextComponentHelper;

public class LaserControllerItem extends Item {
    
    public static int capacity = 50000;
    public static int maxInput = 500;

    public LaserControllerItem() {
        super(new Item.Properties());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return cap.equals(ForgeCapabilities.ENERGY) ? LazyOptional.of(() -> new EnergyStorage(capacity, maxInput, Integer.MAX_VALUE)).cast() : LazyOptional.empty();
            }
        };
    }


    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            tooltip.add(Component.translatable("tooltip.dysonsphere.energy_display", energy.getEnergyStored(), energy.getMaxEnergyStored()));
        });
        
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if(player.isCrouching()){
            var orbitalLaserContainer = player.getCapability(DSCapabilities.ORBITAL_LASER);
            orbitalLaserContainer.ifPresent((orbitalLaser) -> {
                DysonSphere.LOGGER.info("LaserControllerItem use activePattern size pre : {}", orbitalLaser.getActivePatterns().size());
                orbitalLaser.getActivePatterns().clear();
                orbitalLaser.getActivePatterns().add(new OrbitalLaserAttackPattern());
                DysonSphere.LOGGER.info("LaserControllerItem use activePattern size post : {}", orbitalLaser.getActivePatterns().size());
            });
            return InteractionResultHolder.success(stack);
        }
        if(!level.isClientSide){
            stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
                // if(energy.getEnergyStored() >= 750){
                    // LazyOptional<IDysonSphereContainer> dysonSphere = level.getCapability(DSCapabilities.DYSON_SPHERE);
                    // dysonSphere.ifPresent(ds -> {
                    //     //spawn orbital strike entity
                    //     TargetDesignatorEntity projectile = new TargetDesignatorEntity(player, level);
                    //     level.addFreshEntity(projectile);
                    //     // energy.extractEnergy(750, false);
                    // });

                    ItemStack targetDesignator = new ItemStack(ModItems.TARGET_DESIGNATOR.get());
                    if(hand.equals(InteractionHand.MAIN_HAND)) {
                        TargetDesignatorItem.setContainedStack(targetDesignator, stack);
                    } else {
                        ItemStack mainHandStack = player.getMainHandItem();
                        TargetDesignatorItem.setContainedStack(targetDesignator, mainHandStack);
                    }
                    TargetDesignatorItem.setOrbitalStrikePatternIndex(targetDesignator, 0);
                    player.setItemInHand(InteractionHand.MAIN_HAND, targetDesignator);
                    

                // }
            });
        }

        return InteractionResultHolder.success(stack);
    }


}
