package de.bax.dysonsphere.capabilities.grapplingHook;

import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IGrapplingHookHook {
    
    public int getMaxHookCount(Level level, Player player);

    public boolean canDeployAt(Level level, Player player, GrapplingHookEntity hook, BlockHitResult hitResult);

    public float getGravity(Level level, Player player);

    public void onHookLaunch(Level level, Player player, GrapplingHookEntity hook);

    public void onHookDeploy(Level level, Player player, GrapplingHookEntity hook);

    public void onHookRecall(Level level, Player player, GrapplingHookEntity hook);

    public int getColor();

    public ItemStack getHookIcon(Level level, Player player);

}
