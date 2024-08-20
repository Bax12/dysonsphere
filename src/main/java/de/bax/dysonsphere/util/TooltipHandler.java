package de.bax.dysonsphere.util;

import com.mojang.datafixers.util.Either;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DysonSphere.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TooltipHandler {

        @SubscribeEvent
        public static void addTooltips(RenderTooltipEvent.GatherComponents event){
            Minecraft mc = Minecraft.getInstance();
            if(Screen.hasControlDown()){
                event.getItemStack().getCapability(DSCapabilities.GRAPPLING_HOOK_HOOK).ifPresent((hook) -> {
                    event.getTooltipElements().add(Either.left(FormattedText.of(I18n.get("tooltip.dysonsphere.grappling_hook_max_hooks", hook.getMaxHookCount(mc.level, mc.player)))));
                    event.getTooltipElements().add(Either.left(FormattedText.of(I18n.get("tooltip.dysonsphere.grappling_hook_gravity", hook.getGravity(mc.level, mc.player)))));
                });
                event.getItemStack().getCapability(DSCapabilities.GRAPPLING_HOOK_ROPE).ifPresent((rope) -> {
                    event.getTooltipElements().add(Either.left(FormattedText.of(I18n.get("tooltip.dysonsphere.grappling_hook_max_distance", rope.getMaxDistance(mc.level, mc.player)))));
                    event.getTooltipElements().add(Either.left(FormattedText.of(I18n.get("tooltip.dysonsphere.grappling_hook_gravity_mult", rope.getHookGravityMultiplier(mc.level, mc.player)))));
                    event.getTooltipElements().add(Either.left(FormattedText.of(I18n.get("tooltip.dysonsphere.grappling_hook_launch_mult", rope.getLaunchForceMultiplier(mc.level, mc.player)))));
                    event.getTooltipElements().add(Either.left(FormattedText.of(I18n.get("tooltip.dysonsphere.grappling_hook_winch_mult", rope.getWinchForceMultiplier(mc.level, mc.player)))));
                });
                event.getItemStack().getCapability(DSCapabilities.GRAPPLING_HOOK_ENGINE).ifPresent((engine) -> {
                    event.getTooltipElements().add(Either.left(FormattedText.of(I18n.get("tooltip.dysonsphere.grappling_hook_launch_force", engine.getLaunchForce(mc.level, mc.player)))));
                    event.getTooltipElements().add(Either.left(FormattedText.of(I18n.get("tooltip.dysonsphere.grappling_hook_winch_force", engine.getWinchForce(mc.level, mc.player)))));
                });
            } else {
                if(event.getItemStack().getCapability(DSCapabilities.GRAPPLING_HOOK_HOOK).isPresent() || event.getItemStack().getCapability(DSCapabilities.GRAPPLING_HOOK_ROPE).isPresent() ||event.getItemStack().getCapability(DSCapabilities.GRAPPLING_HOOK_ENGINE).isPresent()){
                    event.getTooltipElements().add(Either.left(FormattedText.of(I18n.get("tooltip.dysonsphere.spoiler_stats"), Style.EMPTY.withColor(5636095))));
                }
                
            }
        }
    
}
