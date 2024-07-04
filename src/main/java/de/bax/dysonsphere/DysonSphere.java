package de.bax.dysonsphere;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import de.bax.dysonsphere.advancements.ModAdvancements;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.dysonSphere.DysonSphereContainer;
import de.bax.dysonsphere.capabilities.dysonSphere.DysonSphereProxyContainer;
import de.bax.dysonsphere.containers.ModContainers;
import de.bax.dysonsphere.fluids.ModFluids;
import de.bax.dysonsphere.gui.DSEnergyReceiverGui;
import de.bax.dysonsphere.gui.HeatExchangerGui;
import de.bax.dysonsphere.gui.HeatGeneratorGui;
import de.bax.dysonsphere.gui.RailgunGui;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.network.ModPacketHandler;
import de.bax.dysonsphere.sounds.ModSounds;
import de.bax.dysonsphere.tabs.ModTabs;
import de.bax.dysonsphere.tileRenderer.DSMonitorRenderer;
import de.bax.dysonsphere.tileRenderer.RailgunRenderer;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DysonSphere.MODID)
public class DysonSphere
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "dysonsphere";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();




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
        ModTiles.TILES.register(modEventBus);
        ModContainers.CONTAINERS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        // modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DSConfig.SPEC);
        ModPacketHandler.init();
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModAdvancements.register();
        });
    }

    @SubscribeEvent
    public void attachCaps(AttachCapabilitiesEvent<Level> event){
        DysonSphere.LOGGER.info("Attaching Level Capability!");
        //the overworld is always loaded, so we put the dysonsphere there.
        //if the overworld is blacklisted we get creative in the DysonSphereContainer
        ResourceKey<Level> dimension = event.getObject().dimension();
        if(dimension.equals(Level.OVERWORLD)){ 
            event.addCapability(new ResourceLocation(DysonSphere.MODID, "dysonsphere"), new DysonSphereContainer());
            event.addListener(() -> {
                event.getObject().getCapability(DSCapabilities.DYSON_SPHERE).invalidate();
            });
        } else if(!(DSConfig.DYSON_SPHERE_DIM_BLACKLIST_VALUE.contains(dimension.location().toString()) ^ DSConfig.DYSON_SPHERE_IS_WHITELIST_VALUE)) {
            event.addCapability(new ResourceLocation(DysonSphere.MODID, "dysonsphere_proxy"), new DysonSphereProxyContainer(event.getObject()));
        }
        
    }


    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(() -> {
                MenuScreens.register(ModContainers.RAILGUN_CONTAINER.get(), RailgunGui::new);
                MenuScreens.register(ModContainers.DS_ENERGY_RECEIVER_CONTAINER.get(), DSEnergyReceiverGui::new);
                MenuScreens.register(ModContainers.HEAT_GENERATOR.get(), HeatGeneratorGui::new);
                MenuScreens.register(ModContainers.HEAT_EXCHANGER.get(), HeatExchangerGui::new);
            });
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event){
            event.registerBlockEntityRenderer(ModTiles.RAILGUN.get(), RailgunRenderer::new);
            event.registerBlockEntityRenderer(ModTiles.DS_MONITOR.get(), DSMonitorRenderer::new);
        }
    }
}
