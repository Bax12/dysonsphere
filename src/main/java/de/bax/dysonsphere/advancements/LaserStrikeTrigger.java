package de.bax.dysonsphere.advancements;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class LaserStrikeTrigger extends SimpleCriterionTrigger<LaserStrikeTrigger.TriggerInstance> {
    
    public static final ResourceLocation ID = new ResourceLocation(DysonSphere.MODID, "laser_strike");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected LaserStrikeTrigger.TriggerInstance createInstance(@Nonnull JsonObject json, @Nonnull ContextAwarePredicate player, @Nonnull DeserializationContext context) {
        return new LaserStrikeTrigger.TriggerInstance(player);
    }

    public void trigger(ServerPlayer player){
        this.trigger(player, instance -> instance.check());
    }


    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        public TriggerInstance(ContextAwarePredicate player) {
            super(ID, player);
        }

        public static TriggerInstance instance(){
            return new TriggerInstance(ContextAwarePredicate.ANY);
        }

        public boolean check(){
            return true;
        }

    }


}
