package de.bax.dysonsphere;

import java.util.Map;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import de.bax.dysonsphere.advancements.ModAdvancements;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.dysonSphere.DysonSphereContainer;
import de.bax.dysonsphere.capabilities.dysonSphere.DysonSphereProxyContainer;
import de.bax.dysonsphere.capabilities.grapplingHook.GrapplingHookChainRope;
import de.bax.dysonsphere.capabilities.grapplingHook.GrapplingHookPlayerContainer;
import de.bax.dysonsphere.capabilities.grapplingHook.GrapplingHookStringRope;
import de.bax.dysonsphere.capabilities.grapplingHook.GrapplingHookTripWireHook;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserPlayerContainer;
import de.bax.dysonsphere.compat.ModCompat;
import de.bax.dysonsphere.containers.ModContainers;
import de.bax.dysonsphere.entities.ModEntities;
import de.bax.dysonsphere.entityRenderer.GrapplingHookHarnessRenderLayer;
import de.bax.dysonsphere.entityRenderer.GrapplingHookRenderer;
import de.bax.dysonsphere.entityRenderer.LaserStrikeRenderer;
import de.bax.dysonsphere.entityRenderer.TargetDesignatorRenderer;
import de.bax.dysonsphere.fluids.ModFluids;
import de.bax.dysonsphere.gui.DSEnergyReceiverGui;
import de.bax.dysonsphere.gui.GrapplingHookHarnessInventoryGui;
import de.bax.dysonsphere.gui.HeatExchangerGui;
import de.bax.dysonsphere.gui.HeatGeneratorGui;
import de.bax.dysonsphere.gui.LaserControllerGui;
import de.bax.dysonsphere.gui.LaserControllerInventoryGui;
import de.bax.dysonsphere.gui.LaserPatternControllerGui;
import de.bax.dysonsphere.gui.LaserPatternControllerInventoryGui;
import de.bax.dysonsphere.gui.ModHuds;
import de.bax.dysonsphere.gui.RailgunGui;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.items.grapplingHook.GrapplingHookHarnessItem;
import de.bax.dysonsphere.keybinds.ModKeyBinds;
import de.bax.dysonsphere.network.DSLightSyncPackage;
import de.bax.dysonsphere.network.ModPacketHandler;
import de.bax.dysonsphere.recipes.ModRecipes;
import de.bax.dysonsphere.sounds.ModSounds;
import de.bax.dysonsphere.tabs.ModTabs;
import de.bax.dysonsphere.tileRenderer.DSMonitorRenderer;
import de.bax.dysonsphere.tileRenderer.HeatExchangerRenderer;
import de.bax.dysonsphere.tileRenderer.LaserControllerRenderer;
import de.bax.dysonsphere.tileRenderer.LaserCrafterRenderer;
import de.bax.dysonsphere.tileRenderer.LaserPatternControllerRenderer;
import de.bax.dysonsphere.tileRenderer.RailgunRenderer;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;

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
        ModEntities.ENTITIES.register(modEventBus);
        ModRecipes.TYPES.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.addListener(ModKeyBinds::handleKeyPress);

        // Register the item to a creative tab
        // modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DSConfig.getClientConfigSpec());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DSConfig.getCommonConfigSpec());
        ModPacketHandler.init();
    }

    public void commonSetup(FMLCommonSetupEvent event) {

        ModCompat.init();

        event.enqueueWork(() -> {
            ModAdvancements.register();
        });
    }

    @SubscribeEvent
    public void attachLevelCaps(AttachCapabilitiesEvent<Level> event){
        //the overworld is always loaded, so we put the dysonsphere there.
        //if the overworld is blacklisted we get creative in the DysonSphereContainer
        ResourceKey<Level> dimension = event.getObject().dimension();
        if(dimension.equals(Level.OVERWORLD)){ 
            DysonSphere.LOGGER.info("Attaching Level Capability to {}", dimension.location());
            event.addCapability(new ResourceLocation(DysonSphere.MODID, "dysonsphere"), new DysonSphereContainer());
            event.addListener(() -> {
                event.getObject().getCapability(DSCapabilities.DYSON_SPHERE).invalidate();
            });
        } else if(!(DSConfig.DYSON_SPHERE_DIM_BLACKLIST_VALUE.contains(dimension.location().toString()) ^ DSConfig.DYSON_SPHERE_IS_WHITELIST_VALUE)) {
            DysonSphere.LOGGER.info("Attaching Level Capability to {}", dimension.location());
            event.addCapability(new ResourceLocation(DysonSphere.MODID, "dysonsphere_proxy"), new DysonSphereProxyContainer(event.getObject()));
        }
        
    }

    @SubscribeEvent
    public void attachItemCaps(AttachCapabilitiesEvent<ItemStack> event){
        if(event.getObject().is(Items.TRIPWIRE_HOOK)){
            event.addCapability(new ResourceLocation(DysonSphere.MODID, "grapplinghook_hook"), new GrapplingHookTripWireHook());
            event.addListener(() -> {
                event.getObject().getCapability(DSCapabilities.GRAPPLING_HOOK_HOOK).invalidate();
            });
        }
        if(event.getObject().is(Items.STRING)){
            event.addCapability(new ResourceLocation(DysonSphere.MODID, "grapplinghook_rope"), new GrapplingHookStringRope());
            event.addListener(() -> {
                event.getObject().getCapability(DSCapabilities.GRAPPLING_HOOK_ROPE).invalidate();
            });
        }
        if(event.getObject().is(Items.CHAIN)){
            event.addCapability(new ResourceLocation(DysonSphere.MODID, "grapplinghook_rope"), new GrapplingHookChainRope());
            event.addListener(() -> {
                event.getObject().getCapability(DSCapabilities.GRAPPLING_HOOK_ROPE).invalidate();
            });
        }
    }

    @SubscribeEvent
    public void attachPlayerCaps(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof Player){
            event.addCapability(new ResourceLocation(DysonSphere.MODID, "orbitallaser"),  new OrbitalLaserPlayerContainer((Player) event.getObject()));
            event.addCapability(new ResourceLocation(DysonSphere.MODID, "grapplinghook"),  new GrapplingHookPlayerContainer((Player) event.getObject()));
            event.addListener(() -> {
                event.getObject().getCapability(DSCapabilities.ORBITAL_LASER).invalidate();
                event.getObject().getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).invalidate();
            });
            
        }
    }

    @SubscribeEvent
    public void syncPlayerCaps(EntityJoinLevelEvent event){
        if(event.getLevel().isClientSide) return;
        if(event.getEntity() instanceof ServerPlayer player){
            ModAdvancements.AUTOMATIC.trigger(player);
            player.getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((laser) -> {
                laser.putLasersOnCooldown(0, 0, 0);//call with zero laser to trigger sync
                laser.getLasersAvailable(0);
            });
            event.getLevel().getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((ds) -> {
                //Sync the current light to the clients on join. Completion is 0.0 - 100.0 while light is 0.0 - 1.0
                ModPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new DSLightSyncPackage(ds.getCompletionPercentage() / 100f));
            });
        }
        
    }

    @SubscribeEvent
    public void preservePlayerCaps(PlayerEvent.Clone event){
        if(!event.isWasDeath()) return;
        event.getOriginal().getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((originalLaser) -> {
            event.getEntity().getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((newLaser) -> {
                newLaser.load(originalLaser.save(0), 0);
            });
        });
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
                MenuScreens.register(ModContainers.HEAT_GENERATOR_CONTAINER.get(), HeatGeneratorGui::new);
                MenuScreens.register(ModContainers.HEAT_EXCHANGER_CONTAINER.get(), HeatExchangerGui::new);
                MenuScreens.register(ModContainers.LASER_PATTERN_CONTROLLER_CONTAINER.get(), LaserPatternControllerGui::new);
                MenuScreens.register(ModContainers.LASER_CONTROLLER_INVENTORY_CONTAINER.get(), LaserControllerInventoryGui::new);
                MenuScreens.register(ModContainers.LASER_PATTERN_CONTROLLER_INVENTORY_CONTAINER.get(), LaserPatternControllerInventoryGui::new);
                MenuScreens.register(ModContainers.LASER_CONTROLLER_CONTAINER.get(), LaserControllerGui::new);
                MenuScreens.register(ModContainers.GRAPPLING_HOOK_HARNESS_INVENTORY_CONTAINER.get(), GrapplingHookHarnessInventoryGui::new);


                // ItemProperties.register(ModItems.GRAPPLING_HOOK_HARNESS.get(), new ResourceLocation(MODID, "has_parts"), GrapplingHookHarnessItem.getItemPropertiesAllParts());
                ItemProperties.register(ModItems.GRAPPLING_HOOK_HARNESS.get(), new ResourceLocation(MODID, "has_hook"), GrapplingHookHarnessItem.getItemPropertiesHook());
                ItemProperties.register(ModItems.GRAPPLING_HOOK_HARNESS.get(), new ResourceLocation(MODID, "has_engine"), GrapplingHookHarnessItem.getItemPropertiesEngine());
            });
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event){
            event.registerBlockEntityRenderer(ModTiles.RAILGUN.get(), RailgunRenderer::new);
            event.registerBlockEntityRenderer(ModTiles.DS_MONITOR.get(), DSMonitorRenderer::new);
            event.registerBlockEntityRenderer(ModTiles.LASER_PATTERN_CONTROLLER.get(), LaserPatternControllerRenderer::new);
            event.registerBlockEntityRenderer(ModTiles.LASER_CONTROLLER.get(), LaserControllerRenderer::new);
            event.registerBlockEntityRenderer(ModTiles.LASER_CRAFTER.get(), LaserCrafterRenderer::new);
            event.registerBlockEntityRenderer(ModTiles.HEAT_EXCHANGER.get(), HeatExchangerRenderer::new);

            event.registerEntityRenderer(ModEntities.TARGET_DESIGNATOR.get(), TargetDesignatorRenderer::new);
            event.registerEntityRenderer(ModEntities.LASER_STRIKE.get(), LaserStrikeRenderer::new);
            event.registerEntityRenderer(ModEntities.GRAPPLING_HOOK.get(), GrapplingHookRenderer::new);
        }

        @SubscribeEvent
        public static void registerGuiOverlay(RegisterGuiOverlaysEvent event){
            event.registerAboveAll("orbital_laser_hud", ModHuds.ORBITAL_LASER_HUD::render);
            event.registerAboveAll("laser_crafter_hud", ModHuds.LASER_CRAFTER_HUD::render);
            event.registerAboveAll("heat_tile_hud", ModHuds.HEAT_HUD::render);
            event.registerBelowAll("grappling_hook_hud", ModHuds.GRAPPLING_HOOK_HUD::render);
        }

        @SubscribeEvent
        public static void registerKeyBindings(RegisterKeyMappingsEvent event){
            ModKeyBinds.registerKeyBindings(event);
        }

        @SubscribeEvent
        public static void registerReloadListener(RegisterClientReloadListenersEvent event){
            event.registerReloadListener((ResourceManagerReloadListener) resourceManager -> registerGrapplingHookLayer());
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static void registerGrapplingHookLayer(){
            EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
            Map<String, EntityRenderer<? extends Player>> skinMap = renderManager.getSkinMap();
            for (EntityRenderer<? extends Player> renderer : skinMap.values()) {
                if (renderer instanceof LivingEntityRenderer livingEntityRenderer) {
                    livingEntityRenderer.addLayer(new GrapplingHookHarnessRenderLayer(livingEntityRenderer));
                }
            }
            renderManager.renderers.forEach((e, r) -> {
                if (r instanceof LivingEntityRenderer livingEntityRenderer) {
                    livingEntityRenderer.addLayer(new GrapplingHookHarnessRenderLayer(livingEntityRenderer));
                }
            });
        }

    }
}
