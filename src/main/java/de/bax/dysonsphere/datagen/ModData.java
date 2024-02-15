package de.bax.dysonsphere.datagen;

import java.util.Collections;
import java.util.List;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.datagen.client.lang.en_US.en_usGenerator;
import de.bax.dysonsphere.datagen.client.model.BlockStateGenerator;
import de.bax.dysonsphere.datagen.client.model.ItemModelGenerator;
import de.bax.dysonsphere.datagen.server.BlockLootGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = DysonSphere.MODID, bus = Bus.MOD)
public class ModData {
    

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var helper = event.getExistingFileHelper();
        //client
        generator.addProvider(event.includeClient(), new en_usGenerator(output));
        generator.addProvider(event.includeClient(), new BlockStateGenerator(output, helper));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(output, helper));
        //server
        generator.addProvider(event.includeServer(), new BlockLootGenerator(output));
    }

}
