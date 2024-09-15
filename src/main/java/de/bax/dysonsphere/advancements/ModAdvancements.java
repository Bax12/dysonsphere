package de.bax.dysonsphere.advancements;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.resources.ResourceLocation;

public class ModAdvancements {
    
    public static final PlayerTrigger AUTOMATIC = new PlayerTrigger(new ResourceLocation(DysonSphere.MODID, "automatic"));
    public static final DSProgressTrigger DS_PROGRESS_TRIGGER = new DSProgressTrigger();
    public static final LaserStrikeTrigger LASER_STRIKE_TRIGGER = new LaserStrikeTrigger();
    public static final PraiseTrigger PRAISE_TRIGGER = new PraiseTrigger();
    public static final HookDetachTrigger HOOK_DETACH_TRIGGER = new HookDetachTrigger();
    public static final HookHangingTrigger HOOK_HANGING_TRIGGER = new HookHangingTrigger();
    public static final HookSpeedTrigger HOOK_SPEED_TRIGGER = new HookSpeedTrigger();

    public static void register(){
        CriteriaTriggers.register(AUTOMATIC);
        CriteriaTriggers.register(DS_PROGRESS_TRIGGER);
        CriteriaTriggers.register(LASER_STRIKE_TRIGGER);
        CriteriaTriggers.register(PRAISE_TRIGGER);
        CriteriaTriggers.register(HOOK_DETACH_TRIGGER);
        CriteriaTriggers.register(HOOK_HANGING_TRIGGER);
        CriteriaTriggers.register(HOOK_SPEED_TRIGGER);
    }

}
