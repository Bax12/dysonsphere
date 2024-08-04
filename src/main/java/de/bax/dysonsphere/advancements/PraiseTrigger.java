package de.bax.dysonsphere.advancements;

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

public class PraiseTrigger extends SimpleCriterionTrigger<PraiseTrigger.TriggerInstance>{
    
    public static final ResourceLocation ID = new ResourceLocation(DysonSphere.MODID, "laser_praise");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected PraiseTrigger.TriggerInstance createInstance(@Nonnull JsonObject json, @Nonnull ContextAwarePredicate player, @Nonnull DeserializationContext context) {
        String name = json.get("name").getAsString();
        String sequence = json.get("sequence").getAsString();
        float size = json.get("size").getAsFloat();
        return new TriggerInstance(player, name, sequence, size);
    }

        public void trigger(ServerPlayer player, String name, String sequence, float size){
        this.trigger(player, instance -> instance.check(name, sequence, size));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        protected String name;
        protected String sequence;
        protected float size;

        public TriggerInstance(ContextAwarePredicate player, String name, String sequence, float size) {
            super(ID, player);
            this.name = name;
            this.sequence = sequence;
            this.size = size;
        }

        public static TriggerInstance instance(String name, String sequence, float size){
            return new TriggerInstance(ContextAwarePredicate.ANY, name, sequence, size);
        }

        public boolean check(String name, String sequence, float size){
            return name.toLowerCase().contains(this.name) && this.sequence.equals(sequence) && this.size <= size;
        }

        @Override
        public JsonObject serializeToJson(@Nonnull SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            json.addProperty("name", name);
            json.addProperty("sequence", sequence);
            json.addProperty("size", size);
            return json;
        }

    }

}
