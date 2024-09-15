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

public class HookDetachTrigger extends SimpleCriterionTrigger<HookDetachTrigger.TriggerInstance> {

    public static final ResourceLocation ID = new ResourceLocation(DysonSphere.MODID, "hook_detach");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(@Nonnull JsonObject pJson, @Nonnull ContextAwarePredicate pPredicate, @Nonnull DeserializationContext pDeserializationContext) {
        return new HookDetachTrigger.TriggerInstance(pPredicate);
    }

    public void trigger(ServerPlayer player){
        this.trigger(player, instance -> instance.check());
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        public TriggerInstance(ContextAwarePredicate pPlayer) {
            super(ID, pPlayer);
            
        }

        public static TriggerInstance instance(){
            return new TriggerInstance(ContextAwarePredicate.ANY);
        }

        public boolean check(){
            return true;
        }

    }



    
}
