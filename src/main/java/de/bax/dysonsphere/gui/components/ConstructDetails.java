package de.bax.dysonsphere.gui.components;

import de.bax.dysonsphere.constructs.Construct;
import de.bax.dysonsphere.items.CapsuleItem;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class ConstructDetails {
    
    protected int x, y, width, height;
    protected Construct construct;

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

    @SuppressWarnings("null")
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick, boolean isEnabledComponent){
        if(construct == null) return;
        pGuiGraphics.fill(this.x, this.y, this.x + width, this.y + height, 0xFFAAAAAA);
        pGuiGraphics.renderOutline(this.x, this.y, width, height, 0xFF222222);
        MutableComponent header = construct.getDisplayName().copy().withStyle(ChatFormatting.UNDERLINE);

        Font font = Minecraft.getInstance().font; 

        if(font.width(header) > width -10){
            AssetUtil.renderMaxWidthString(pGuiGraphics, header, x + 5, y + 5, width - 10, 0xFFFFFFFF, 0, 0xF000F0, true);
        } else {
            pGuiGraphics.drawCenteredString(font, header, x + (width/2), y + 5, 0xFFFFFFFF);
        }

        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.literal("Status: " + (isEnabledComponent ? "Enabled" :  "Disabled")), x + 5, y + 20, width - 10, 0xFFF0F0F0, 0, 0xF000F0, true);
        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.literal("Tier: " + construct.tier), x + 5, y + 30, width - 10, 0xFFF0F0F0, 0, 0xF000F0, true);
        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.literal("Stability: " + AssetUtil.FLOAT_FORMAT.format(construct.stability)), x + 5, y + 40, width - 10, 0xFFF0F0F0, 0, 0xF000F0, true);
        if(construct.energy > 0){
            AssetUtil.renderMaxWidthString(pGuiGraphics, Component.literal("Energy Provided: " + construct.energy), x + 5, y + 50, width - 10, 0xFFF0F0F0, 0, 0xF000F0, true);
        } else {
            AssetUtil.renderMaxWidthString(pGuiGraphics, Component.literal("Energy Draw: " + -construct.energy), x + 5, y + 50, width - 10, 0xFFF0F0F0, 0, 0xF000F0, true);
        }
        if(!construct.components.isEmpty()){
            pGuiGraphics.drawString(font, Component.literal("Components: "), x + 5, y + 60, 0xFFF0F0F0);
            int index = 0;
            for(var comp : construct.components.entrySet()){
                AssetUtil.renderMaxWidthString(pGuiGraphics, CapsuleItem.getTypeName(comp.getKey().getItems()[0]).copy().append(" " + comp.getValue().required() + "/" + comp.getValue().foundation()), x + 10, y + 70 + (10 * index++), 90, 0xFFF0F0F0, 0, 0xF000F0, true);
            }
        }

        pGuiGraphics.drawWordWrap(font, construct.getDescriptionName(), x + 5, y + 140, 95, 0xFFF0F0F0);
        
    }

}
