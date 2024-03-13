package de.bax.dysonsphere.datagen;

import java.util.List;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.datagen.client.lang.en_US.en_usGenerator;
import de.bax.dysonsphere.datagen.client.model.BlockStateGenerator;
import de.bax.dysonsphere.datagen.client.model.ItemModelGenerator;
import de.bax.dysonsphere.datagen.client.sound.SoundDefinitionGenerator;
import de.bax.dysonsphere.datagen.server.AdvancementGenerator;
import de.bax.dysonsphere.datagen.server.BlockLootGenerator;
import de.bax.dysonsphere.datagen.server.BlockTagGenerator;
import de.bax.dysonsphere.datagen.server.FluidTagGenerator;
import de.bax.dysonsphere.datagen.server.ItemTagGenerator;
import de.bax.dysonsphere.datagen.server.RecipeGenerator;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
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
        var provider = event.getLookupProvider();
        //client
        generator.addProvider(event.includeClient(), new en_usGenerator(output));
        generator.addProvider(event.includeClient(), new BlockStateGenerator(output, helper));
        generator.addProvider(event.includeClient(), new ItemModelGenerator(output, helper));
        generator.addProvider(event.includeClient(), new SoundDefinitionGenerator(output, helper));
        //server
        generator.addProvider(event.includeServer(), new BlockLootGenerator(output));
        generator.addProvider(event.includeServer(), new FluidTagGenerator(output, provider, helper));
        var blockGenerator = new BlockTagGenerator(output, provider, helper);
        generator.addProvider(event.includeServer(), blockGenerator);
        generator.addProvider(event.includeServer(), new ItemTagGenerator(output, provider, blockGenerator.contentsGetter(), helper));
        generator.addProvider(event.includeServer(), new RecipeGenerator(output));
        generator.addProvider(event.includeServer(), new ForgeAdvancementProvider(output, provider, helper, List.of(new AdvancementGenerator())));
    }

}
