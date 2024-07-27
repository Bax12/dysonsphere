package de.bax.dysonsphere.gui;

import java.util.Locale;
import java.util.Optional;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.keybinds.ModKeyBinds;
import de.bax.dysonsphere.network.LaserPatternActivatedPackage;
import de.bax.dysonsphere.network.ModPacketHandler;
import de.bax.dysonsphere.tileentities.LaserCrafterTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class ModHuds {


    public static final IGuiOverlay ORBITAL_LASER_HUD = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        if(Minecraft.getInstance().player.getInventory().hasAnyMatching((stack) -> {
            return stack.is(ModItems.LASER_CONTROLLER.get()) || stack.is(ModItems.TARGET_DESIGNATOR.get());
        })){
            renderOrbitalLaserHUD(gui, guiGraphics, partialTick, screenWidth, screenHeight);
        }
    };

    public static final IGuiOverlay LASER_CRAFTER_HUD = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        if(Minecraft.getInstance().hitResult instanceof BlockHitResult hit){
            if(Minecraft.getInstance().level.getBlockEntity(hit.getBlockPos()) instanceof LaserCrafterTile tile){
                renderLaserCrafterHud(tile, gui, guiGraphics, partialTick, screenWidth, screenHeight);
            }
        }
    };

    public static final IGuiOverlay HEAT_HUD = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        if(Minecraft.getInstance().hitResult instanceof BlockHitResult hit){
            if(Minecraft.getInstance().level.getBlockEntity(hit.getBlockPos()) instanceof IHeatTile tile){
                renderHeatHud(tile, gui, guiGraphics, partialTick, screenWidth, screenHeight);
            }
        }
    };

    protected static int offset = 0;
    public static void renderOrbitalLaserHUD(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight){
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



    public static ResourceLocation HUD_LOC = AssetUtil.getGuiLocation("gui_lower");
    public static void renderLaserCrafterHud(LaserCrafterTile tile, ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight){
        if(Minecraft.getInstance().screen != null) return;
        Font font = Minecraft.getInstance().font;
        ItemStack input = tile.input.getStackInSlot(0);
        ItemStack result = tile.getRecipe().map((recipe) -> {
            return recipe.output().copy();
        }).orElse(ItemStack.EMPTY);
        if(Minecraft.getInstance().player.isShiftKeyDown()){
            Component msg = Component.literal("Energy: " + AssetUtil.FLOAT_FORMAT.format(tile.getCharge()) + "/" + tile.getRecipe().map((recipe) -> {return recipe.inputEnergy();}).orElse(0l));
            font.drawInBatch(msg, screenWidth * 0.5f - (font.width(msg)/2), screenHeight * 0.6f, -1, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
        }
        
        if(!input.isEmpty()){
            int posX = (int) (screenWidth * 0.5f) - 20;
            int posY = (int) (screenHeight * 0.54f);
            guiGraphics.blit(HUD_LOC, posX, posY, 15, 180, 16, 28);
            guiGraphics.renderItem(input, posX, posY + 12);
            if(!result.isEmpty()){
                guiGraphics.blit(HUD_LOC, posX + 16, posY, 31, 180, 24, 28);
                guiGraphics.renderItem(result, posX + 41, posY + 6);
            }
        }
        
    }

    
    public static void renderHeatHud(IHeatTile tile, ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight){
        if(Minecraft.getInstance().screen != null) return;
        if(!Minecraft.getInstance().player.isShiftKeyDown()) return;
        Font font = Minecraft.getInstance().font;
        Component msg = Component.literal("Heat: " + AssetUtil.FLOAT_FORMAT.format(tile.getHeat()) + "°K");
        font.drawInBatch(msg, screenWidth * 0.5f - (font.width(msg)/2), screenHeight * 0.6f + 10, -1, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
    }
    
}
