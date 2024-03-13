package de.bax.dysonsphere.advancements;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class DSProgressTrigger extends SimpleCriterionTrigger<DSProgressTrigger.TriggerInstance> {
    
    public static final ResourceLocation ID = new ResourceLocation(DysonSphere.MODID, "ds_progress");


    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected DSProgressTrigger.TriggerInstance createInstance(JsonObject json,ContextAwarePredicate player, DeserializationContext context) {
        float progress = json.get("progress").getAsFloat();
        return new DSProgressTrigger.TriggerInstance(player, progress);
    }
    

    public void trigger(ServerPlayer player, float completion){
        this.trigger(player, instance -> instance.check(completion));
    }


    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        protected float progress;

        public TriggerInstance(ContextAwarePredicate player, float progress) {
            super(ID, player);
            this.progress = progress;
        }

        public static TriggerInstance instance(float progress){
            return new TriggerInstance(ContextAwarePredicate.ANY, progress);
        }

        public boolean check(float completion){
            return this.progress <= completion;
        }
        
        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            json.addProperty("progress", progress);
            return json;
        }

    }

}
