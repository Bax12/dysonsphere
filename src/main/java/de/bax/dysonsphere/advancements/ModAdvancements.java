package de.bax.dysonsphere.advancements;

import net.minecraft.advancements.CriteriaTriggers;

public class ModAdvancements {
    
    public static final DSProgressTrigger DS_PROGRESS_TRIGGER = new DSProgressTrigger();
    public static final LaserStrikeTrigger LASER_STRIKE_TRIGGER = new LaserStrikeTrigger();
    public static final PraiseTrigger PRAISE_TRIGGER = new PraiseTrigger();

    public static void register(){
        CriteriaTriggers.register(DS_PROGRESS_TRIGGER);
        CriteriaTriggers.register(LASER_STRIKE_TRIGGER);
        CriteriaTriggers.register(PRAISE_TRIGGER);
    }

}
