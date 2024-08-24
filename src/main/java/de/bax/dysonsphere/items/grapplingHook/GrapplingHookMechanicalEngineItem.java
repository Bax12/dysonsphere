package de.bax.dysonsphere.items.grapplingHook;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.energy.ItemEnergyHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class GrapplingHookMechanicalEngineItem extends GrapplingHookElectricEngineItem {
    
    public GrapplingHookMechanicalEngineItem(int capacity, int maxTransfer, int launchUsage, int winchUsage, int winchRecuperation, float launchForce, float winchForce, int color){
        super(capacity, maxTransfer, launchUsage, winchUsage, winchRecuperation, launchForce, winchForce, color);
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
                if(cap.equals(ForgeCapabilities.ENERGY)){
                    return LazyOptional.of(() -> new ItemEnergyHandler(stack, CAPACITY, MAX_TRANSFER)).cast();
                }
                if(cap.equals(DSCapabilities.GRAPPLING_HOOK_ENGINE)){
                    return LazyOptional.of(() -> new GrapplingHookMechanicalEngineWrapper(stack, new ItemEnergyHandler(stack, CAPACITY, MAX_TRANSFER))).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public void appendHoverText(ItemStack pStack, @javax.annotation.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        if(Screen.hasControlDown()){
            pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.grappling_hook_engine_mechanical.desc"));
        }
    }

    public class GrapplingHookMechanicalEngineWrapper extends GrapplingHookElectricEngineWrapper{

        public GrapplingHookMechanicalEngineWrapper(ItemStack stack, IEnergyStorage energy){
            super(stack, energy);
        }

        @Override
        public boolean isFreeMoving() {
            return true;
        }

        

    } 



}
