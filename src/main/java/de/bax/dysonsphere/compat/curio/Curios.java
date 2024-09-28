package de.bax.dysonsphere.compat.curio;

import java.util.List;

import com.google.common.base.Predicate;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.compat.IModCompat;
import de.bax.dysonsphere.compat.curio.renderer.GrapplingHookHarnessRenderer;
import de.bax.dysonsphere.compat.curio.renderer.OrbitalLaserControllerRenderer;
import de.bax.dysonsphere.items.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class Curios implements IModCompat {
    
    public static final Capability<ICurioItem> CURIO_ITEM = CapabilityManager.get(new CapabilityToken<>() {});


    @Override
    public void init() {
        DysonSphere.LOGGER.info("Curio Compat Loading");
        // MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, this::attachItemCaps);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    // public void attachItemCaps(final AttachCapabilitiesEvent<ItemStack> event){
    // }

    protected void clientSetup(final FMLClientSetupEvent event){
        CuriosRendererRegistry.register(ModItems.GRAPPLING_HOOK_HARNESS.get(), () -> new GrapplingHookHarnessRenderer());
        CuriosRendererRegistry.register(ModItems.LASER_CONTROLLER.get(), () -> new OrbitalLaserControllerRenderer());
    }

    public static List<ItemStack> getMatchingCurios(Player player, Predicate<ItemStack> filter){
        return CuriosApi.getCuriosInventory(player).map((inv) -> {
            return inv.findCurios(filter).stream().map((a) -> a.stack()).toList();
        }).orElse(List.of());
    }

}
