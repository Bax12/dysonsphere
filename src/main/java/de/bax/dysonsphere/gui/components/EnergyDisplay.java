package de.bax.dysonsphere.gui.components;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import de.bax.dysonsphere.gui.BaseGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.energy.IEnergyStorage;

@OnlyIn(Dist.CLIENT)
public class EnergyDisplay extends BaseDisplay{
    
    protected IEnergyStorage energy;

    public EnergyDisplay(int x, int y, IEnergyStorage energyStorage) {
        super(x, y);

        this.energy = energyStorage;
    }

    public void draw(GuiGraphics guiGraphics) {
        //base
        guiGraphics.blit(BaseGui.GUI_INVENTORY_LOC, xPos, yPos, 0, 90, width, height);

        //meter
        if(energy.getEnergyStored() > 0){
            int renderSize = energy.getEnergyStored() * 83 / energy.getMaxEnergyStored();
            guiGraphics.blit(BaseGui.GUI_INVENTORY_LOC, xPos + 1, yPos + 84 - renderSize, 43, 91, 19, renderSize);
        }
        //overlay
        guiGraphics.blit(BaseGui.GUI_INVENTORY_LOC, xPos, yPos, 21, 90, width, height);
    }


    protected void addTooltip(List<Component> tooltip){
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);

        tooltip.add(Component.translatable("tooltip.dysonsphere.energy_display", format.format(energy.getEnergyStored()), format.format(energy.getMaxEnergyStored())));
    }




}
