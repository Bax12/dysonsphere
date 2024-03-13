package de.bax.dysonsphere.advancements;

import net.minecraft.advancements.CriteriaTriggers;

public class ModAdvancements {
    
    public static final DSProgressTrigger DS_PROGRESS_TRIGGER = new DSProgressTrigger();

    public static void register(){
        CriteriaTriggers.register(DS_PROGRESS_TRIGGER);
    }

}
