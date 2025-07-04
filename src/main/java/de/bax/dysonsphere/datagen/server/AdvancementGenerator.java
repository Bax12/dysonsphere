package de.bax.dysonsphere.datagen.server;



import java.util.function.Consumer;

import de.bax.dysonsphere.advancements.DSProgressTrigger;
import de.bax.dysonsphere.advancements.HookDetachTrigger;
import de.bax.dysonsphere.advancements.HookHangingTrigger;
import de.bax.dysonsphere.advancements.HookSpeedTrigger;
import de.bax.dysonsphere.advancements.LaserStrikeTrigger;
import de.bax.dysonsphere.advancements.ModAdvancements;
import de.bax.dysonsphere.advancements.PraiseTrigger;
import de.bax.dysonsphere.items.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

public class AdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
        
        
        Advancement root = Advancement.Builder.advancement()
        .addCriterion("root", new PlayerTrigger.TriggerInstance(ModAdvancements.AUTOMATIC.getId(), ContextAwarePredicate.ANY))
        .display(new DisplayInfo(ModItems.COMPONENT_SMART_ALLOY.get().getDefaultInstance(), Component.translatable("achievement.dysonsphere.root"), Component.translatable("achievement.dysonsphere.root.desc"), new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), FrameType.GOAL, false, false, false))
        .save(saver, "dysonsphere:root");

        Advancement ds_start = Advancement.Builder.advancement()
        .addCriterion("completion", DSProgressTrigger.TriggerInstance.instance(0.000001f))
        .display(display(ModItems.COIL_IRON.get(), "start"))
        .parent(root)
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
        .display(display(ModItems.CAPSULE_LASER_0.get(), "laser_strike"))
        .save(saver, "dysonsphere:laser_strike");
        
        Advancement praise = Advancement.Builder.advancement()
        .parent(laser_strike)
        .addCriterion("praise", PraiseTrigger.TriggerInstance.instance("praise", "wwddssaaww", 10f))
        .display(display(ModItems.TARGET_DESIGNATOR.get(), "praise", true))
        .save(saver, "dysonsphere:praise");


        ItemPredicate[] validHarnesses = {ItemPredicate.Builder.item().of(ModItems.GRAPPLING_HOOK_HARNESS.get()).build()};
        Advancement grapplingHook = Advancement.Builder.advancement()
        .addCriterion("obtainHarness", new InventoryChangeTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY,  validHarnesses))
        .display(display(ModItems.GRAPPLING_HOOK_HARNESS.get(), "get_hook_harness"))
        .parent(root)
        .save(saver, "dysonsphere:hook_obtained");

        Advancement hookHanging = Advancement.Builder.advancement()
        .parent(grapplingHook)
        .addCriterion("hookHanging", HookHangingTrigger.TriggerInstance.instance())
        .display(display(ModItems.GRAPPLING_HOOK_HOOK_WOOD.get(), "hook_hanging"))
        .save(saver, "dysonsphere:hook_hanging");
        //move faster then 50 while in a hook
        Advancement hookSpeed50 = Advancement.Builder.advancement()
        .parent(grapplingHook)
        .addCriterion("hookSpeed50", HookSpeedTrigger.TriggerInstance.instance(50f))
        .display(display(ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC_2.get(), "hook_speed_50"))
        .save(saver, "dysonsphere:hook_speed50");

        Advancement hookDetach = Advancement.Builder.advancement()
        .parent(grapplingHook)
        .addCriterion("detach", HookDetachTrigger.TriggerInstance.instance())
        .display(display(Items.STRING, "hook_detach"))
        .save(saver, "dysonsphere:hook_detach");
    }

    protected DisplayInfo display(Item item, String name){
        return display(item, name, false);
    }
    
    protected DisplayInfo display(Item item, String name, boolean hidden){
        return new DisplayInfo(item.getDefaultInstance(), Component.translatable("achievement.dysonsphere." + name), Component.translatable("achievement.dysonsphere."+ name + ".desc"), new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), FrameType.TASK, true, true, hidden);
    }

    
}
