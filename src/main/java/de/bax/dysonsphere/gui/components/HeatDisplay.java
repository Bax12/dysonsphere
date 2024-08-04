package de.bax.dysonsphere.gui.components;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import de.bax.dysonsphere.gui.BaseGui;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeatDisplay extends BaseDisplay{

    
    protected IHeatContainer heat;

    public HeatDisplay(int x, int y, IHeatContainer heatHandler){
        super(x, y);

        this.heat = heatHandler;
    }

    @Override
    public void draw(GuiGraphics guiGraphics) {
        //base
        guiGraphics.blit(BaseGui.GUI_INVENTORY_LOC, xPos, yPos, 0, 90, width, height);

        //meter
        if(heat != null && heat.getHeatStored() > 0){
            int renderSize = (int) (83 * Math.min(1, heat.getHeatStored() / heat.getMaxHeatStored()));
            guiGraphics.blit(BaseGui.GUI_INVENTORY_LOC, xPos + 1, yPos + 84 - renderSize, 106, 91 + 83 - renderSize, 19, renderSize);
        }
        //overlay
        guiGraphics.blit(BaseGui.GUI_INVENTORY_LOC, xPos, yPos, 84, 90, width, height);
    }

    @Override
    protected void addTooltip(List<Component> tooltip) {
        if(heat != null){
            tooltip.add(Component.translatable("tooltip.dysonsphere.heat_display", AssetUtil.FLOAT_FORMAT.format(Math.round(heat.getHeatStored())), AssetUtil.FLOAT_FORMAT.format(Math.round(heat.getMaxHeatStored()))));
        } else {
            tooltip.add(Component.translatable("tooltip.dysonsphere.heat_display", "???", "???"));
        }
    }

    

    
}
