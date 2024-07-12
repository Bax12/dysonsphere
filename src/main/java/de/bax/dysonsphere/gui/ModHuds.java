package de.bax.dysonsphere.gui;

import java.util.Locale;
import java.util.Optional;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.keybinds.ModKeyBinds;
import de.bax.dysonsphere.network.LaserPatternActivatedPackage;
import de.bax.dysonsphere.network.ModPacketHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class ModHuds {


    public static final IGuiOverlay ORBITAL_LASER_HUD = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        if(Minecraft.getInstance().player.getInventory().hasAnyMatching((stack) -> {
            return stack.is(ModItems.LASER_CONTROLLER.get()) || stack.is(ModItems.TARGET_DESIGNATOR.get());
        })){
            renderOrbitalLaserCooldown(gui, guiGraphics, partialTick, screenWidth, screenHeight);
        }
    };

    protected static int offset = 0;
    public static void renderOrbitalLaserCooldown(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight){
        if(Minecraft.getInstance().screen != null) return;
        Font font = Minecraft.getInstance().font;
        var player = Minecraft.getInstance().player;
        String inputSequence = player.getCapability(DSCapabilities.ORBITAL_LASER).map((laser) -> {
            font.drawInBatch(Component.translatable("tooltip.dysonsphere.orbital_laser_hud_lasers_available", laser.getLasersAvailable(player.tickCount)).withStyle(ChatFormatting.RED), 10, 5, -1, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
            font.drawInBatch(Component.translatable("tooltip.dysonsphere.orbital_laser_hud_lasers_cooldown", laser.getLasersOnCooldown(player.tickCount)).withStyle(ChatFormatting.BLUE), 10, 15, -1, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
            font.drawInBatch(Component.translatable("tooltip.dysonsphere.orbital_laser_hud_next_cooldown", laser.getTimeToNextCooldown(player.tickCount) / 20), 10, 25, 0xFFDD0000, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);

            
            // font.drawInBatch(Component.literal("Current Sequence: " + OrbitalLaserAttackPattern.replaceWithArrows(laser.getCurrentInputSequence())), 150, 5, -1, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
            
            return laser.getCurrentInputSequence();
        }).orElse("");


        if(ModKeyBinds.ORBITAL_LASER_CONTROL_MAPPING.get().isDown()){
            offset = 35;
            player.getInventory().items.forEach((stack) -> {
                if(stack.is(ModItems.LASER_CONTROLLER.get())){
                    stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((container) -> {
                        boolean shouldReset = true;
                        for(int i = 0; i <= container.getSlots(); i++){
                            if(!container.getStackInSlot(i).getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER).map((pattern) -> {
                                String patternString = pattern.getPattern().getCallInSequenceArrows();
                                String inputSequenceArrows = OrbitalLaserAttackPattern.replaceWithArrows(inputSequence);
                                Component patternSequenceComponent;
                                boolean couldReset = true;
                                if(inputSequenceArrows != "" && patternString.startsWith(inputSequenceArrows)){
                                    if(patternString.equals(inputSequenceArrows)){

                                        //inform server
                                        // DysonSphere.LOGGER.info("ModHuds renderOrbitalLaserCooldown sequence success: {}", inputSequence);
                                        ModPacketHandler.INSTANCE.sendToServer(new LaserPatternActivatedPackage(pattern.getPattern()));

                                    } else {
                                        couldReset = false;
                                    }
                                    patternString = patternString.substring(inputSequenceArrows.length());
                                    patternSequenceComponent = Component.literal(inputSequenceArrows).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RED).append(Component.literal(patternString).withStyle(ChatFormatting.WHITE));
                                } else {
                                    patternSequenceComponent = Component.literal(patternString).withStyle(ChatFormatting.WHITE);
                                }
                                font.drawInBatch(Component.translatable("tooltip.dysonsphere.orbital_laser_hud_pattern_name", pattern.getPattern().name, patternSequenceComponent), 10, offset, -1, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
                                // /20 /60 -> ticks to minutes --- /20 % 60 -> ticks to seconds without minutes
                                font.drawInBatch(Component.translatable("tooltip.dysonsphere.orbital_laser_hud_pattern_lasers", pattern.getPattern().getLasersRequired(), pattern.getPattern().getRechargeTime() / 20 / 60 , String.format(Locale.ENGLISH, "%02d", (pattern.getPattern().getRechargeTime() / 20) % 60)), 10, offset + 10, -1, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
                                // font.drawInBatch(Component.literal(" Cooldown " + pattern.getPattern().getRechargeTime()), 10, offset + 20, -1, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
                                offset += 25;
                                return couldReset;
                            }).orElse(true)){
                                shouldReset = false;
                            }
                        }
                        if(shouldReset){
                            player.getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((laser) -> {
                                laser.setCurrentInputSequence("");
                            });
                        }
                    });
                }
            });            
        }
        
        
        
    }

    
    
    
}
