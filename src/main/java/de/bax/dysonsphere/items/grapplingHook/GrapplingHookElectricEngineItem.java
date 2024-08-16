package de.bax.dysonsphere.items.grapplingHook;

import java.awt.Color;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.energy.ItemEnergyHandler;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookEngine;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class GrapplingHookElectricEngineItem extends Item {

    public static int CAPACITY = 15000;
    public static int MAX_TRANSFER = 500;
    public static int LAUNCH_USAGE = 50;
    public static int WINCH_USAGE = 5;
    public static int WINCH_RECUPERATION = 1;

    public GrapplingHookElectricEngineItem() {
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
                if(cap.equals(ForgeCapabilities.ENERGY)){
                    return LazyOptional.of(() -> new ItemEnergyHandler(stack, CAPACITY, MAX_TRANSFER)).cast();
                }
                if(cap.equals(DSCapabilities.GRAPPLING_HOOK_ENGINE)){
                    return LazyOptional.of(() -> new GrapplingHookElectricEngineWrapper(stack, new ItemEnergyHandler(stack, CAPACITY, MAX_TRANSFER))).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public void appendHoverText(ItemStack pStack, @javax.annotation.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.energy_display", AssetUtil.FLOAT_FORMAT.format(energy.getEnergyStored()), AssetUtil.FLOAT_FORMAT.format(energy.getMaxEnergyStored())));
        });
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return 0xDD2222;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        return pStack.getCapability(ForgeCapabilities.ENERGY).map((energy) -> {
            return (int) (13f * energy.getEnergyStored() / energy.getMaxEnergyStored());
        }).orElse(0);
    }


    public static class GrapplingHookElectricEngineWrapper implements IGrapplingHookEngine {

        protected ItemStack containingStack;
        protected IEnergyStorage energyReference;

        public GrapplingHookElectricEngineWrapper(ItemStack stack, IEnergyStorage energyRef){
            this.containingStack = stack;
            this.energyReference = energyRef;
        }

        @Override
        public float getDeployForce(Level level, Player player) {
            return 2.5f;
        }

        @Override
        public float getWinchForce(Level level, Player player) {
            return 3.2f;
        }

        @Override
        public void onHookLaunch(Level level, Player player, GrapplingHookEntity hook) {
            energyReference.extractEnergy(LAUNCH_USAGE, false);
        }

        @Override
        public void onActiveWinchTick(Level level, Player player) {
            energyReference.extractEnergy(WINCH_USAGE, false);
        }

        @Override
        public void onRappelTick(Level level, Player player) {
            energyReference.receiveEnergy(WINCH_RECUPERATION, false);
        }

        @Override
        public boolean canLaunch(Level level, Player player) {
            return energyReference.getEnergyStored() >= LAUNCH_USAGE;
        }

        @Override
        public boolean canWinch(Level level, Player player) {
            return energyReference.getEnergyStored() >= WINCH_USAGE;
        }

        @Override
        public boolean canRappel(Level level, Player player) {
            return true;
        }

        @Override
        public int getColor() {
            return Color.GRAY.getRGB();
        }

    }

    
    


}
