package de.bax.dysonsphere.compat.pneumaticcraft;

import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookEngine;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import de.bax.dysonsphere.util.AssetUtil;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerItem;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class GrapplingHookPressureEngineWrapper implements ICapabilityProvider, IAirHandlerItem, IGrapplingHookEngine {

    public static int CAPACITY = 4000;
    public static float MAX_PRESSURE = 10;

    public static int LAUNCH_USAGE = 60;
    public static int WINCH_USAGE = 15;
    public static float LAUNCH_FORCE = 2.5f;
    public static float WINCH_FORCE = 3.8f;

    protected final ItemStack containingStack;
    
    public GrapplingHookPressureEngineWrapper(ItemStack stack){
        this.containingStack = stack;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(Pneumaticcraft.PRESSURE_ITEM) || cap.equals(DSCapabilities.GRAPPLING_HOOK_ENGINE)){
            return LazyOptional.of(() -> this).cast();
        }
        return LazyOptional.empty();
    }

	public static void getHoverText(ItemStack pStack, List<Component> pTooltipComponents) {
		pStack.getCapability(Pneumaticcraft.PRESSURE_ITEM).ifPresent((air) -> {
            pTooltipComponents.add(Component.translatable("dysonsphere.tooltip.pneumaticcraft.pressure", AssetUtil.FLOAT_FORMAT.format(air.getPressure()), AssetUtil.FLOAT_FORMAT.format(air.maxPressure())));
        });
	}

    public static int getBarWidth(ItemStack stack){
        return stack.getCapability(Pneumaticcraft.PRESSURE_ITEM).map((air) -> {
            return (int) (13f * air.getPressure() / air.maxPressure());
        }).orElse(0);
    }

    @Override
    public float getPressure() {
        return (float) getAir() / getVolume();
    }

    @Override
    public int getAir() {
        // if(containingStack.hasTag()){
            return containingStack.getOrCreateTag().getInt("air");
        // }
        // return 0;
    }

    @Override
    public void addAir(int amount) {
        amount += getAir();
        containingStack.getOrCreateTag().putInt("air", amount);
    }

    @Override
    public int getBaseVolume() {
        return CAPACITY;
    }

    @Override
    public void setBaseVolume(int newBaseVolume) {
    }

    @Override
    public int getVolume() {
        return getBaseVolume();
    }

    @Override
    public float maxPressure() {
        return MAX_PRESSURE;
    }

    @Override
    @Nonnull
    public ItemStack getContainer() {
        return containingStack;
    }

    @Override
    public float getLaunchForce(Level level, Player player) {
        return LAUNCH_FORCE * Math.max(1f, getPressure());
    }

    @Override
    public float getWinchForce(Level level, Player player) {
        return WINCH_FORCE + (float) Math.min(1, Math.max(0, player.getPosition(0).y / level.getMaxBuildHeight()));
    }

    @Override
    public void onHookLaunch(Level level, Player player, GrapplingHookEntity hook) {
        addAir(-LAUNCH_USAGE);
    }

    @Override
    public void onActiveWinchTick(Level level, Player player) {
        addAir(-WINCH_USAGE);
    }

    @Override
    public void onRappelTick(Level level, Player player) {
        
    }

    @Override
    public boolean canLaunch(Level level, Player player) {
        return getAir() >= LAUNCH_USAGE;
    }

    @Override
    public boolean canWinch(Level level, Player player) {
        return getAir() >= WINCH_USAGE;
    }

    @Override
    public boolean canRappel(Level level, Player player) {
        return true;
    }

    @Override
    public int getColor() {
        return 0x393433;
    }


}
