package de.bax.dysonsphere.items.grapplingHook;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nonnull;

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

    public enum TYPE{
        E1(15000, 500, 50, 5, 1, 2.5f, 3.2f, Color.GRAY.getRGB()),
        E2(1_000_000, 5000, 50, 10, 5, 5f, 4.4f, 0x353535),
        MECHANICAL(20000, 5000, 75, 10, 5, 2.8f, 3f, 0x949da4);

        public int CAPACITY;
        public int MAX_TRANSFER;
        public int LAUNCH_USAGE;
        public int WINCH_USAGE;
        public int WINCH_RECUPERATION;
        public float LAUNCH_FORCE;
        public float WINCH_FORCE;
        public int COLOR;

        TYPE(int capacity, int maxTransfer, int launchUsage, int winchUsage, int winchRecuperation, float launchForce, float winchForce, int color){
            this.CAPACITY = capacity;
            this.MAX_TRANSFER = maxTransfer;
            this.LAUNCH_USAGE = launchUsage;
            this.WINCH_USAGE = winchUsage;
            this.WINCH_RECUPERATION = winchRecuperation;
            this.LAUNCH_FORCE = launchForce;
            this.WINCH_FORCE = winchForce;
            this.COLOR = color;
        }


    }

    protected final TYPE type;

    public GrapplingHookElectricEngineItem(int type) {
        super(new Item.Properties());

        this.type = TYPE.values()[type];
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
                    return LazyOptional.of(() -> new ItemEnergyHandler(stack, type.CAPACITY, type.MAX_TRANSFER)).cast();
                }
                if(cap.equals(DSCapabilities.GRAPPLING_HOOK_ENGINE)){
                    return LazyOptional.of(() -> new GrapplingHookElectricEngineWrapper(stack, new ItemEnergyHandler(stack, type.CAPACITY, type.MAX_TRANSFER))).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack pStack, @javax.annotation.Nullable Level pLevel, @Nonnull List<Component> pTooltipComponents, @Nonnull TooltipFlag pIsAdvanced) {
        pStack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.energy_display", AssetUtil.FLOAT_FORMAT.format(energy.getEnergyStored()), AssetUtil.FLOAT_FORMAT.format(energy.getMaxEnergyStored())));
        });
    }

    @Override
    public boolean isBarVisible(@Nonnull ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarColor(@Nonnull ItemStack pStack) {
        return 0xDD2222;
    }

    @Override
    public int getBarWidth(@Nonnull ItemStack pStack) {
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
            return type.LAUNCH_FORCE;
        }

        @Override
        public float getWinchForce(Level level, Player player) {
            return type.WINCH_FORCE;
        }

        @Override
        public void onHookLaunch(Level level, Player player, GrapplingHookEntity hook) {
            energyReference.extractEnergy(type.LAUNCH_USAGE, false);
        }

        @Override
        public void onActiveWinchTick(Level level, Player player) {
            energyReference.extractEnergy(type.WINCH_USAGE, false);
            if(player.tickCount % 5 == 0){
                level.playSound(player, player, ModSounds.ELECTRIC_WINCH.get(), SoundSource.PLAYERS, 0.2f, 1.0f);
            }
            
        }

        @Override
        public void onRappelTick(Level level, Player player) {
            energyReference.receiveEnergy(type.WINCH_RECUPERATION, false);
            if(player.tickCount % 5 == 0){
                level.playSound(player, player, ModSounds.ELECTRIC_WINCH.get(), SoundSource.PLAYERS, 0.3f, 0.5f);
            }
        }

        @Override
        public boolean canLaunch(Level level, Player player) {
            return energyReference.getEnergyStored() >= type.LAUNCH_USAGE;
        }

        @Override
        public boolean canWinch(Level level, Player player) {
            return energyReference.getEnergyStored() >= type.WINCH_USAGE;
        }

        @Override
        public boolean canRappel(Level level, Player player) {
            return true;
        }

        @Override
        public int getColor() {
            return type.COLOR;
        }

    }

    
    


}
