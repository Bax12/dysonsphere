package de.bax.dysonsphere.items.grapplingHook;

import java.awt.Color;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.energy.ItemEnergyHandler;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookEngine;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import de.bax.dysonsphere.sounds.ModSounds;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
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

    protected final int CAPACITY;
    protected final int MAX_TRANSFER;
    protected final int LAUNCH_USAGE;
    protected final int WINCH_USAGE;
    protected final int WINCH_RECUPERATION;
    protected final float LAUNCH_FORCE;
    protected final float WINCH_FORCE;
    protected final int COLOR;

    public GrapplingHookElectricEngineItem(int capacity, int maxTransfer, int launchUsage, int winchUsage, int winchRecuperation, float launchForce, float winchForce, int color) {
        super(new Item.Properties());

        this.CAPACITY = capacity;
        this.MAX_TRANSFER = maxTransfer;
        this.LAUNCH_USAGE = launchUsage;
        this.WINCH_USAGE = winchUsage;
        this.WINCH_RECUPERATION = winchRecuperation;
        this.LAUNCH_FORCE = launchForce;
        this.WINCH_FORCE = winchForce;
        this.COLOR = color;
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


    public class GrapplingHookElectricEngineWrapper implements IGrapplingHookEngine {

        protected ItemStack containingStack;
        protected IEnergyStorage energyReference;

        public GrapplingHookElectricEngineWrapper(ItemStack stack, IEnergyStorage energyRef){
            this.containingStack = stack;
            this.energyReference = energyRef;
        }

        @Override
        public float getLaunchForce(Level level, Player player) {
            return LAUNCH_FORCE;
        }

        @Override
        public float getWinchForce(Level level, Player player) {
            return WINCH_FORCE;
        }

        @Override
        public void onHookLaunch(Level level, Player player, GrapplingHookEntity hook) {
            energyReference.extractEnergy(LAUNCH_USAGE, false);
        }

        @Override
        public void onActiveWinchTick(Level level, Player player) {
            energyReference.extractEnergy(WINCH_USAGE, false);
            if(player.tickCount % 5 == 0){
                level.playSound(player, player, ModSounds.ELECTRIC_WINCH.get(), SoundSource.PLAYERS, 0.2f, 1.0f);
            }
            
        }

        @Override
        public void onRappelTick(Level level, Player player) {
            energyReference.receiveEnergy(WINCH_RECUPERATION, false);
            if(player.tickCount % 5 == 0){
                level.playSound(player, player, ModSounds.ELECTRIC_WINCH.get(), SoundSource.PLAYERS, 0.3f, 0.5f);
            }
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
            return COLOR;
        }

    }

    
    


}
