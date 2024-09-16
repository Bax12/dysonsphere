package de.bax.dysonsphere.advancements;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class HookSpeedTrigger extends SimpleCriterionTrigger<HookSpeedTrigger.TriggerInstance> {
    
    public static final ResourceLocation ID = new ResourceLocation(DysonSphere.MODID, "hook_speed");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(@Nonnull JsonObject pJson, @Nonnull ContextAwarePredicate pPredicate, @Nonnull DeserializationContext pDeserializationContext) {
        float speed = pJson.get("speed").getAsFloat();
        return new TriggerInstance(pPredicate, speed);
    }

    public void trigger(ServerPlayer pPlayer, float speed) {
        this.trigger(pPlayer, instance -> instance.check(speed));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        protected float speed;

        public TriggerInstance(ContextAwarePredicate player, float speed){
            super(ID, player);
            this.speed = speed;
        }

        public static TriggerInstance instance(float speed){
            return new TriggerInstance(ContextAwarePredicate.ANY, speed);
        }

        public boolean check(float speed){
            return this.speed <= speed;
        }

        @Override
        public JsonObject serializeToJson(@Nonnull SerializationContext pConditions) {
            JsonObject json = super.serializeToJson(pConditions);
            json.addProperty("speed", speed);
            return json;
        }

    }
}
