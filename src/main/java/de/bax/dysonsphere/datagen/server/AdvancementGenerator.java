package de.bax.dysonsphere.datagen.server;



import java.util.function.Consumer;

import de.bax.dysonsphere.advancements.DSProgressTrigger;
import de.bax.dysonsphere.advancements.LaserStrikeTrigger;
import de.bax.dysonsphere.advancements.PraiseTrigger;
import de.bax.dysonsphere.items.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

public class AdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
        
        Advancement ds_start = Advancement.Builder.advancement()
        .addCriterion("completion", DSProgressTrigger.TriggerInstance.instance(0.000001f))
        .display(display(ModItems.COIL_IRON.get(), "start"))
        .save(saver, "dysonsphere:ds_start");

        Advancement ds_half = Advancement.Builder.advancement()
        .parent(ds_start)
        .addCriterion("completion", DSProgressTrigger.TriggerInstance.instance(50))
        .display(display(ModItems.COIL_COPPER.get(), "half"))
        .save(saver, "dysonsphere:ds_half");

        Advancement ds_completed = Advancement.Builder.advancement()
        .parent(ds_half)
        .addCriterion("completion", DSProgressTrigger.TriggerInstance.instance(100))
        .display(display(ModItems.RAILGUN.get(), "completed"))
        .save(saver, "dysonsphere:ds_completed");
        
        Advancement laser_strike = Advancement.Builder.advancement()
        .parent(ds_start)
        .addCriterion("laser_strike", LaserStrikeTrigger.TriggerInstance.instance())
        .display(display(ModItems.CAPSULE_LASER.get(), "laser_strike"))
        .save(saver, "dysonsphere:laser_strike");
        
        Advancement praise = Advancement.Builder.advancement()
        .parent(laser_strike)
        .addCriterion("praise", PraiseTrigger.TriggerInstance.instance("praise", "wwddssaaww", 10f))
        .display(display(ModItems.TARGET_DESIGNATOR.get(), "praise", true))
        .save(saver, "dysonsphere:praise");
    }

    protected DisplayInfo display(Item item, String name){
        return display(item, name, false);
    }
    
    protected DisplayInfo display(Item item, String name, boolean hidden){
        return new DisplayInfo(item.getDefaultInstance(), Component.translatable("achievement.dysonsphere." + name), Component.translatable("achievement.dysonsphere."+ name + ".desc"), new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), FrameType.TASK, true, true, hidden);
    }

    
}
