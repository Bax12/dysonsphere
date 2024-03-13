package de.bax.dysonsphere.datagen.server;

import static de.bax.dysonsphere.advancements.DSProgressTrigger.TriggerInstance.instance;

import java.util.function.Consumer;

import de.bax.dysonsphere.items.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
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
        .addCriterion("completion", instance(0.000001f))
        .display(display(ModItems.COIL_IRON.get(), "start"))
        .save(saver, "dysonsphere:ds_start");

        Advancement ds_half = Advancement.Builder.advancement()
        .parent(ds_start)
        .addCriterion("completion", instance(50))
        .display(display(ModItems.COIL_COPPER.get(), "half"))
        .save(saver, "dysonsphere:ds_half");

        Advancement ds_completed = Advancement.Builder.advancement()
        .parent(ds_half)
        .addCriterion("completion", instance(100))
        .display(display(ModItems.RAILGUN.get(), "completed"))
        .save(saver, "dysonsphere:ds_completed");
        
        

        
    }


    protected DisplayInfo display(Item item, String name){
        return new DisplayInfo(item.getDefaultInstance(), Component.translatable("achievement.dysonsphere." + name), Component.translatable("achievement.dysonsphere."+ name + ".desc"), new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), FrameType.TASK, true, true, false);
    }

    
}
