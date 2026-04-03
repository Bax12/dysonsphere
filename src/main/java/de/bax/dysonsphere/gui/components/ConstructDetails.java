package de.bax.dysonsphere.gui.components;

import java.util.Map;
import java.util.Map.Entry;

import de.bax.dysonsphere.constructs.Construct;
import de.bax.dysonsphere.constructs.Construct.ComponentCount;
import de.bax.dysonsphere.items.CapsuleItem;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

public class ConstructDetails {
    
    protected int x, y, width, height;
    protected Construct construct;
    protected Map<Item, Long> dsParts;

    public ConstructDetails(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setConstruct(Construct construct) {
        this.construct = construct;
    }

    public Construct getConstruct() {
        return construct;
    }

    public void setDsParts(Map<Item, Long> dsParts){
        this.dsParts = dsParts;
    }

    @SuppressWarnings("null")
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, boolean isEnabledComponent){
        if(construct == null) return;
        pGuiGraphics.fill(this.x, this.y, this.x + width, this.y + height, 0xFFAAAAAA);
        pGuiGraphics.renderOutline(this.x, this.y, width, height, 0xFF222222);
        MutableComponent header = construct.getDisplayName().copy().withStyle(ChatFormatting.UNDERLINE);

        Font font = Minecraft.getInstance().font; 

        if(font.width(header) > width -10){
            AssetUtil.renderMaxWidthString(pGuiGraphics, header, x + 5, y + 5, width - 10, 0xFFFFFFFF, 0, LightTexture.FULL_BRIGHT, true);
        } else {
            pGuiGraphics.drawCenteredString(font, header, x + (width/2), y + 5, 0xFFFFFFFF);
        }

        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.translatable("tooltip.dysonsphere.construct_details_status", (isEnabledComponent ? Component.translatable("tooltip.dysonsphere.construct_details_enabled").withStyle(ChatFormatting.GREEN) :  Component.translatable("tooltip.dysonsphere.construct_details_disabled").withStyle(ChatFormatting.RED))), x + 5, y + 20, width - 10, 0xFFF0F0F0, 0, LightTexture.FULL_BRIGHT, true);
        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.translatable("tooltip.dysonsphere.construct_details_tier", construct.tier), x + 5, y + 30, width - 10, 0xFFF0F0F0, 0, LightTexture.FULL_BRIGHT, true);
        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.translatable("tooltip.dysonsphere.construct_details_stability", AssetUtil.FLOAT_FORMAT.format(construct.stability)), x + 5, y + 40, width - 10, 0xFFF0F0F0, 0, LightTexture.FULL_BRIGHT, true);
        if(construct.energy > 0){
            AssetUtil.renderMaxWidthString(pGuiGraphics, Component.translatable("tooltip.dysonsphere.construct_details_energy_provided", construct.energy), x + 5, y + 50, width - 10, 0xFFF0F0F0, 0, LightTexture.FULL_BRIGHT, true);
        } else {
            AssetUtil.renderMaxWidthString(pGuiGraphics, Component.translatable("tooltip.dysonsphere.construct_details_energy_draw", -construct.energy), x + 5, y + 50, width - 10, 0xFFF0F0F0, 0, LightTexture.FULL_BRIGHT, true);
        }
        if(!construct.components.isEmpty()){
            pGuiGraphics.drawString(font, Component.translatable("tooltip.dysonsphere.construct_details_components"), x + 5, y + 60, 0xFFF0F0F0);
            int index = 0;
            for(Entry<Ingredient, ComponentCount> comp : construct.components.entrySet()){
                MutableComponent required = Component.literal(AssetUtil.FLOAT_FORMAT.format(comp.getValue().required()));
                MutableComponent foundation = Component.literal(AssetUtil.FLOAT_FORMAT.format(comp.getValue().foundation()));

                if(dsParts != null){
                    long count = dsParts.entrySet().stream().filter((entry) -> {return comp.getKey().test(entry.getKey().getDefaultInstance());}).mapToLong((entry) -> {return entry.getValue();}).sum();
                    if(count < comp.getValue().foundation()){
                        required.withStyle(ChatFormatting.RED);
                        foundation.withStyle(ChatFormatting.RED);
                    } else if(count < comp.getValue().required()){
                        required.withStyle(ChatFormatting.RED);
                        foundation.withStyle(ChatFormatting.GREEN);
                    } else {
                        required.withStyle(ChatFormatting.GREEN);
                        foundation.withStyle(ChatFormatting.GREEN);
                    }
                }
                

                AssetUtil.renderMaxWidthString(pGuiGraphics, CapsuleItem.getTypeName(comp.getKey().getItems()[0]).copy().append(" ").append(required).append("/").append(foundation), x + 10, y + 70 + (10 * index++), 90, 0xFFF0F0F0, 0, LightTexture.FULL_BRIGHT, true);
            }
        }

        pGuiGraphics.drawWordWrap(font, construct.getDescriptionName(), x + 5, y + 140, 95, 0xFFF0F0F0);
        
    }

}
