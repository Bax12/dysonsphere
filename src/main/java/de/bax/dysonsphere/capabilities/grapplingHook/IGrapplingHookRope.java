package de.bax.dysonsphere.capabilities.grapplingHook;

import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IGrapplingHookRope {
    
    public float getMaxDistance(Level level, Player player);

    public float getLaunchForceMultiplier(Level level, Player player);

    public float getWinchForceMultiplier(Level level, Player player);

    public float getHookGravityMultiplier(Level level, Player player);

    public void onHookLaunch(Level level, Player player, GrapplingHookEntity hook);

    public void onHookDeploy(Level level, Player player, GrapplingHookEntity hook);

    public default void onHookOutOfRange(Level level, Player player, GrapplingHookEntity hook){
        hook.recall();
    }

    public void onActiveWinchTick(Level level, Player player); //using engine to pull rope into the winch

    public void onRappelTick(Level level, Player player); //using brake to slowly release rope from the winch

    public int getColor();

}
