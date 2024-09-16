package de.bax.dysonsphere.items.grapplingHook;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.compat.ModCompat;
import de.bax.dysonsphere.compat.pneumaticcraft.GrapplingHookPressureEngineWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class GrapplingHookPressureEngine extends Item {
    
    public GrapplingHookPressureEngine(){
        super(new Item.Properties());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if(ModCompat.isLoaded(ModCompat.MODID.PNEUMATICCRAFT)){
            return new GrapplingHookPressureEngineWrapper(stack);
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @javax.annotation.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(ModCompat.isLoaded(ModCompat.MODID.PNEUMATICCRAFT)){
            GrapplingHookPressureEngineWrapper.getHoverText(pStack, pTooltipComponents);
        }
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return ModCompat.isLoaded(ModCompat.MODID.PNEUMATICCRAFT);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return 0xFAFAFA;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        if(ModCompat.isLoaded(ModCompat.MODID.PNEUMATICCRAFT)){
            return GrapplingHookPressureEngineWrapper.getBarWidth(pStack);
        }
        return 0;
    }
}
