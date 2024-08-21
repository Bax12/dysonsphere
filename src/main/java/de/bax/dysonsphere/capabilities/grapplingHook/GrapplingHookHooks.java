package de.bax.dysonsphere.capabilities.grapplingHook;

import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class GrapplingHookHooks {
    
    
    

    public static IGrapplingHookHook makeSimpleHook(ItemStack stack, int count, float gravity, int color){
        return new IGrapplingHookHook() {

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
                level.playSound(null, player, SoundEvents.DISPENSER_DISPENSE, SoundSource.PLAYERS, 0.4f, 1.5f);
            }

            @Override
            public void onHookDeploy(Level level, Player player, GrapplingHookEntity hook) {
                level.playSound(player, hook.getPosition(0).x, hook.getPosition(0).y, hook.getPosition(0).z, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.PLAYERS, 0.5f, 1.2f);
            }

            @Override
            public void onHookRecall(Level level, Player player, GrapplingHookEntity hook) {
                level.playSound(player, hook.getPosition(0).x, hook.getPosition(0).y, hook.getPosition(0).z, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.PLAYERS, 0.5f, 1.2f);
            }

            @Override
            public int getColor() {
                return color;
            }

            @Override
            public ItemStack getHookIcon(Level level, Player player) {
                return stack;
            }
        };
    }

    

}