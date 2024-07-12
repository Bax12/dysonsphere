package de.bax.dysonsphere.items;

import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.IOrbitalLaserPatternContainer;
import de.bax.dysonsphere.capabilities.orbitalLaser.ItemLaserPatternContainer;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class LaserPatternItem extends Item {

    public LaserPatternItem() {
        super(new Item.Properties());
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                
                return cap.equals(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER) ? LazyOptional.of(() -> new ItemLaserPatternContainer(stack)).cast() : LazyOptional.empty();
            }
        };
    }

    @Override
    public void appendHoverText(ItemStack pStack, @javax.annotation.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pStack.getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER).ifPresent((patternContainer) -> {
            if(!patternContainer.isEmpty()){
                pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.orbital_laser_hud_pattern_name", patternContainer.getPattern().name, patternContainer.getPattern().getCallInSequenceArrows()));
                pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.orbital_laser_hud_pattern_lasers", patternContainer.getPattern().getLasersRequired(), patternContainer.getPattern().getRechargeTime() / 20 / 60 , String.format(Locale.ENGLISH, "%02d", (patternContainer.getPattern().getRechargeTime() / 20) % 60)));
            }
        });
    }
    
    

}
