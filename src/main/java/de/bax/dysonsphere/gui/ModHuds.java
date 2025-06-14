package de.bax.dysonsphere.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.joml.Matrix4f;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;

import de.bax.dysonsphere.DSConfig;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import de.bax.dysonsphere.capabilities.inputHatch.IInputProvider.ProviderType;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import de.bax.dysonsphere.compat.ModCompat;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.keybinds.ModKeyBinds;
import de.bax.dysonsphere.network.LaserPatternActivatedPackage;
import de.bax.dysonsphere.network.ModPacketHandler;
import de.bax.dysonsphere.tileentities.LaserCrafterTile;
import de.bax.dysonsphere.util.AssetUtil;
import de.bax.dysonsphere.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class ModHuds {


    public static final IGuiOverlay ORBITAL_LASER_HUD = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        if(DSConfig.GUI_ORBITAL_LASER_ENABLED_VALUE && InventoryUtil.isInExtendedPlayerInventory(Minecraft.getInstance().player, (stack) -> {
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
        if(DSConfig.GUI_HEAT_OVERLAY_ENABLED_VALUE && !ModCompat.isLoaded(ModCompat.MODID.JADE) && Minecraft.getInstance().hitResult instanceof BlockHitResult hit){
            if(Minecraft.getInstance().level.getBlockEntity(hit.getBlockPos()) instanceof IHeatTile tile){
                renderHeatHud(tile, gui, guiGraphics, partialTick, screenWidth, screenHeight);
            }
        }
    };

    public static final IGuiOverlay GRAPPLING_HOOK_HUD = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        if(DSConfig.GUI_GRAPPLING_HOOK_ENABLED_VALUE){
            renderGrapplingHookHUD(gui, guiGraphics, partialTick, screenWidth, screenHeight);
        }
    };

    protected static int offset = 0;
    public static void renderOrbitalLaserHUD(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight){
        if(Minecraft.getInstance().screen != null) return;
        Font font = Minecraft.getInstance().font;
        var player = Minecraft.getInstance().player;
        String inputSequence = player.getCapability(DSCapabilities.ORBITAL_LASER).map((laser) -> {
            font.drawInBatch(Component.translatable("tooltip.dysonsphere.orbital_laser_hud_lasers_available", laser.getLasersAvailable(player.tickCount)), 10, 5, -1, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
            font.drawInBatch(Component.translatable("tooltip.dysonsphere.orbital_laser_hud_lasers_cooldown", laser.getLasersOnCooldown(player.tickCount)), 10, 15, -1, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
            font.drawInBatch(Component.translatable("tooltip.dysonsphere.orbital_laser_hud_next_cooldown", laser.getTimeToNextCooldown(player.tickCount) / 20), 10, 25, -1, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);

            
            // font.drawInBatch(Component.literal("Current Sequence: " + OrbitalLaserAttackPattern.replaceWithArrows(laser.getCurrentInputSequence())), 150, 5, -1, false, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
            
            return laser.getCurrentInputSequence();
        }).orElse("");


        if(ModKeyBinds.ORBITAL_LASER_CONTROL_MAPPING.get().isDown()){
            offset = 40;
            // NonNullList<ItemStack> itemList = NonNullList.create();
            // itemList.addAll(player.getInventory().items);
            // itemList.addAll(player.getInventory().offhand);//offhand is not a part of all items...
            // List<ItemStack> controllerItems = itemList.stream().filter((stack) -> {return stack.is(ModItems.LASER_CONTROLLER.get());}).toList();  
            List<ItemStack> controllerItems = InventoryUtil.getAllInExtendedPlayerInventory(player, (stack) -> stack.is(ModItems.LASER_CONTROLLER.get()));
            if(controllerItems.size() > 1){
                font.drawInBatch(Component.translatable("tooltip.dysonsphere.laser_controller_to_many"), 10, offset, -1, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
            } else if (controllerItems.size() == 1){
                controllerItems.forEach((stack) -> {
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
                                    font.drawInBatch(Component.translatable("tooltip.dysonsphere.orbital_laser_hud_pattern_name", pattern.getPattern().name, patternSequenceComponent), 10, offset, -1, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
                                    // /20 /60 -> ticks to minutes --- /20 % 60 -> ticks to seconds without minutes
                                    font.drawInBatch(Component.translatable("tooltip.dysonsphere.orbital_laser_hud_pattern_lasers", pattern.getPattern().getLasersRequired(), pattern.getPattern().getRechargeTime() / 20 / 60 , String.format(Locale.ENGLISH, "%02d", (pattern.getPattern().getRechargeTime() / 20) % 60)), 10, offset + 10, -1, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
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



    public static ResourceLocation HUD_LOC = AssetUtil.getGuiLocation("gui_lower");
    protected static int deltaTime = 0; //its client side so should be fine?
    protected static long lastGameTime = 0;
    public static void renderLaserCrafterHud(LaserCrafterTile tile, ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight){
        if(Minecraft.getInstance().screen != null) return;
        Font font = Minecraft.getInstance().font;
        ItemStack input = tile.input.getStackInSlot(0);
        ItemStack result = tile.getRecipe().map((recipe) -> {
            return recipe.output().copy();
        }).orElse(ItemStack.EMPTY);
        if(Minecraft.getInstance().player.isShiftKeyDown()){
            Component msg = Component.translatable("tooltip.dysonsphere.energy_display", AssetUtil.FLOAT_FORMAT.format(tile.getCharge()), AssetUtil.FLOAT_FORMAT.format(tile.getRecipe().map((recipe) -> {return recipe.inputEnergy();}).orElse(0l)));
            font.drawInBatch(msg, screenWidth * 0.5f - (font.width(msg)/2), screenHeight * 0.6f + 20f, -1, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
        }

        if(!input.isEmpty()){
            int posX = (int) (screenWidth * 0.5f) - 20;
            int posY = (int) (screenHeight * 0.6f) - 30;
            guiGraphics.blit(HUD_LOC, posX, posY, 15, 180, 16, 28);
            guiGraphics.renderItem(input, posX, posY + 12);
            if(!result.isEmpty()){
                guiGraphics.blit(HUD_LOC, posX + 16, posY, 31, 180, 24, 28);
                guiGraphics.renderItem(result, posX + 41, posY + 6);
            }
        }

        List<ItemStack> parallelInputs = tile.acceptorHandler.getInputs(ProviderType.PARALLEL);
        //draw a rotation circle of the parallel Input around the main gui
        if(!parallelInputs.isEmpty()){
            int centerX = (int) (screenWidth * 0.5f);
            int centerY = (int) (screenHeight * 0.6f) - 30;
            double rads = Math.PI / parallelInputs.size() * 2;
            double radius = 35d;
            
            if(!Minecraft.getInstance().player.isShiftKeyDown()){
                deltaTime += tile.getLevel().getGameTime() - lastGameTime;
                //use and freeze deltatime instead of using global time to stop rotation in place instead of snapping to default pos
            }
            lastGameTime = tile.getLevel().getGameTime();


            double offset = ((deltaTime) / 100d ) % (2 * Math.PI);
            for(int i = 0; i < parallelInputs.size(); i++){
                double posX = (centerX + (Math.cos(i * rads + offset) * radius));
                double posY = (centerY + (Math.sin(i * rads + offset) * radius));
                // guiGraphics.renderItem(parallelInputs.get(i), posX, posY);
                
                //basically guiGraphics.renderItem(), but not limited to int positions
                BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getModel(parallelInputs.get(i), tile.getLevel(), Minecraft.getInstance().player, 0);
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate((float)(posX + 8), (float)(posY + 8), 150f);
                guiGraphics.pose().mulPoseMatrix((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
                guiGraphics.pose().scale(16.0F, 16.0F, 16.0F);
                boolean flatLighting = !bakedModel.usesBlockLight();
                if (flatLighting) {
                    Lighting.setupForFlatItems();
                }
                Minecraft.getInstance().getItemRenderer().render(parallelInputs.get(i), ItemDisplayContext.GUI, false, guiGraphics.pose(), guiGraphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
                guiGraphics.flush();
                if (flatLighting) {
                    Lighting.setupFor3DItems();
                }
                guiGraphics.pose().popPose();
            }
        }

        List<ItemStack> serialInputs = tile.acceptorHandler.getInputs(ProviderType.SERIAL);
        //draw a straight line of (over)stacked inputs, below the main ui, centered
        if(!serialInputs.isEmpty()){
            Map<Item,Integer> stacks = serialInputs.stream().collect(HashMap<Item,Integer>::new, (map, stack) -> {
                map.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }, (mainMap, mergeMap) -> {
                mergeMap.entrySet().forEach((entry) -> {//straight from the map isn't working. with the entrySet there is no issue.
                    mainMap.merge(entry.getKey(), entry.getValue(), Integer::sum);
                });
            });

            // DysonSphere.LOGGER.info("ModHuds renderLaserCrafterHud: stacks: " + Arrays.toString(stacks.entrySet().toArray()));

            int halfOffset = 8 - (stacks.size() / 10);
            int centerX = (int) (screenWidth * 0.5f) - (stacks.size() * halfOffset);
            int centerY = (int) (screenHeight * 0.6f) + 30;


            int i = 0;
            for(Entry<Item,Integer> entry : stacks.entrySet()){
                guiGraphics.renderItem(entry.getKey().getDefaultInstance(), centerX + (halfOffset*2*i), centerY);
                guiGraphics.renderItemDecorations(font, entry.getKey().getDefaultInstance(), centerX + (halfOffset*2*i), centerY, entry.getValue().toString());
                i++;
                // guiGraphics.renderItem(itemStack, centerX + (halfOffset*2*i), centerY);
            }
            
        }


        
        
    }

    
    public static void renderHeatHud(IHeatTile tile, ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight){
        if(Minecraft.getInstance().screen != null) return;
        if(!Minecraft.getInstance().player.isShiftKeyDown()) return;
        Font font = Minecraft.getInstance().font;
        Component msg = Component.translatable("tooltip.dysonsphere.heat_current", AssetUtil.FLOAT_FORMAT.format(Math.round(tile.getHeat())));
        font.drawInBatch(msg, screenWidth * 0.5f - (font.width(msg)/2), screenHeight * 0.6f + 10, -1, true, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
    }

    public static void renderGrapplingHookHUD(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight){
        if(Minecraft.getInstance().screen != null) return;
        Font font = Minecraft.getInstance().font;
        var player = Minecraft.getInstance().player;

        player.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
            hookContainer.getGrapplingHookFrame().ifPresent((frame) -> {
                PoseStack ps = guiGraphics.pose();
                ps.pushPose();
                ps.translate(10, screenHeight - (ModCompat.isLoaded(ModCompat.MODID.MEKANISM) ? 60 : 20), 0);
                ps.scale(0.8f, 0.8f, 0.8f);
                font.drawInBatch(Component.translatable("tooltip.dysonsphere.grappling_hook_status", Component.translatable(hookContainer.isPulling() ? "tooltip.dysonsphere.grappling_hook_status_pull" : hookContainer.isUnwinding() ? "tooltip.dysonsphere.grappling_hook_status_unwind" : "tooltip.dysonsphere.grappling_hook_status_stop")), 0, 0, -1, true, ps.last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
                font.drawInBatch(Component.translatable("tooltip.dysonsphere.grappling_hook_deployed", hookContainer.getDeployedHooks().size(), frame.getMaxHooks(player.level(), player).orElse(0)), 0, 10, -1, true, ps.last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, 0, 255);
                ps.popPose();
            });
        });
    }
    
    
}
