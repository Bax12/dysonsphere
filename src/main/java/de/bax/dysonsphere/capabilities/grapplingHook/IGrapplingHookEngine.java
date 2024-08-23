package de.bax.dysonsphere.capabilities.grapplingHook;

import de.bax.dysonsphere.entities.GrapplingHookEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IGrapplingHookEngine {
    public float getLaunchForce(Level level, Player player);

    public float getWinchForce(Level level, Player player);

    public void onHookLaunch(Level level, Player player, GrapplingHookEntity hook);

    public void onActiveWinchTick(Level level, Player player); //using engine to pull rope into the winch

    public void onRappelTick(Level level, Player player); //using brake to slowly release rope from the winch

    public boolean canLaunch(Level level, Player player);

    public boolean canWinch(Level level, Player player);

    public boolean canRappel(Level level, Player player);

    public default boolean isFreeMoving(){
        return false;
    }

    public int getColor();
}
