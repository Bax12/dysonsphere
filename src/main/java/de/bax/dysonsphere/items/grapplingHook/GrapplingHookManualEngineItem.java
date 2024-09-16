package de.bax.dysonsphere.items.grapplingHook;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookEngine;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class GrapplingHookManualEngineItem extends Item {

    public static float LAUNCH_FORCE = 2.0f;
    public static float WINCH_FORCE = 2.4f;
    public static float LAUNCH_USAGE = 0.1f;
    public static float WINCH_USAGE = 0.2f;
    public static float RAPPEL_USAGE = 0.05f;

    
    public GrapplingHookManualEngineItem(){
        super(new Item.Properties());
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if(cap.equals(DSCapabilities.GRAPPLING_HOOK_ENGINE)){
                    return LazyOptional.of(() -> new GrapplingHookManualEngineWrapper(stack)).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    public static class GrapplingHookManualEngineWrapper implements IGrapplingHookEngine {

        protected final ItemStack containingStack;

        public GrapplingHookManualEngineWrapper(ItemStack stack){
            this.containingStack = stack;
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
            player.getFoodData().addExhaustion(LAUNCH_USAGE);
        }

        @Override
        public void onActiveWinchTick(Level level, Player player) {
            player.getFoodData().addExhaustion(WINCH_USAGE);
        }

        @Override
        public void onRappelTick(Level level, Player player) {
            player.getFoodData().addExhaustion(RAPPEL_USAGE);
        }

        @Override
        public boolean canLaunch(Level level, Player player) {
            return true;
        }

        @Override
        public boolean canWinch(Level level, Player player) {
            return true;
        }

        @Override
        public boolean canRappel(Level level, Player player) {
            return true;
        }

        @Override
        public int getColor() {
            return 0x7d6137;
        }

    }

}
