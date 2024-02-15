package de.bax.dysonsphere;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.fluids.ModFluids;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.tabs.ModTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DysonSphere.MODID)
public class DysonSphere
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "dysonsphere";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();




    public DysonSphere()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEM_BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ModItems.ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        ModTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        // modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    // private void addCreative(BuildCreativeModeTabContentsEvent event)
    // {
    //     if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
    //         event.accept(ModBlocks.HEAT_PIPE_BLOCK);
    // }

    // // You can use SubscribeEvent and let the Event Bus discover methods to call
    // @SubscribeEvent
    // public void onServerStarting(ServerStartingEvent event)
    // {
    //     // Do something when the server starts
    //     LOGGER.info("HELLO from server starting");
    // }

    // // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    // @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    // public static class ClientModEvents
    // {
    //     @SubscribeEvent
    //     public static void onClientSetup(FMLClientSetupEvent event)
    //     {
    //         // Some client setup code
    //         LOGGER.info("HELLO FROM CLIENT SETUP");
    //         LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    //     }
    // }
}
