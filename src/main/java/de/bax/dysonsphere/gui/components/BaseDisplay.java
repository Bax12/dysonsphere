package de.bax.dysonsphere.gui.components;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public abstract class BaseDisplay {
    
    protected final int xPos;
    protected final int yPos;

    protected int height = 85;
    protected int width = 21;

    public BaseDisplay(int x, int y){
        this.xPos = x;
        this.yPos = y;
    }

    public void drawOverlay(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(isHovered(mouseX, mouseY)){
            
            List<Component> tooltip = new ArrayList<>();
            addTooltip(tooltip);
            Minecraft mc = Minecraft.getInstance();
            guiGraphics.renderComponentTooltip(mc.font, tooltip, mouseX, mouseY);
        }
    }

    public abstract void draw(GuiGraphics guiGraphics);

    protected abstract void addTooltip(List<Component> tooltip);

    protected boolean isHovered(int mouseX, int mouseY){
        return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
    }
}
