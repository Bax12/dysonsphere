package de.bax.dysonsphere.capabilities.grapplingHook;

import org.jetbrains.annotations.NotNull;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class GrapplingHookTripWireHook implements IGrapplingHookHook, ICapabilityProvider {

    public static int count = 2;
    public static float gravity = 0.04f;

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
        if (cap.equals(DSCapabilities.GRAPPLING_HOOK_HOOK)) {
            return LazyOptional.of(() -> this).cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public int getMaxHookCount(Level level, Player player) {
        return count;
    }

    @Override
    public boolean canDeployAt(Level level, Player player, GrapplingHookEntity hook, BlockHitResult hitResult) {
        return true;
    }

    @Override
    public float getGravity(Level level, Player player) {
        return gravity;
    }

    @Override
    public void onHookLaunch(Level level, Player player, GrapplingHookEntity hook) {
    }

    @Override
    public void onHookDeploy(Level level, Player player, GrapplingHookEntity hook) {
        level.playSound(player, hook.position().x, hook.position().y, hook.position().z, SoundEvents.TRIPWIRE_ATTACH, SoundSource.PLAYERS, 0.5f, 0.8f);
    }

    @Override
    public void onHookRecall(Level level, Player player, GrapplingHookEntity hook) {
        level.playSound(player, hook.position().x, hook.position().y, hook.position().z, SoundEvents.TRIPWIRE_DETACH, SoundSource.PLAYERS, 0.5f, 0.8f);
    }

    @Override
    public int getColor() {
        return 0x888888;
    }

    @Override
    public ItemStack getHookIcon(Level level, Player player) {
        return Items.TRIPWIRE_HOOK.getDefaultInstance();
    }
}
