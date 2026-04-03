package de.bax.dysonsphere.gui;

import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.containers.HeatConverterContainer;
import de.bax.dysonsphere.gui.components.EnergyDisplay;
import de.bax.dysonsphere.gui.components.HeatDisplay;
import de.bax.dysonsphere.tileentities.HeatConverterTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class HeatConverterGui extends BaseGui<HeatConverterContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_heat_converter");
    private final HeatConverterTile tile;
    private EnergyDisplay energy;
    private HeatDisplay heat;
    
    public HeatConverterGui(HeatConverterContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);
        this.tile = container.tile;

        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void init() {
        super.init();

        this.energy = new EnergyDisplay(this.leftPos + 148, this.topPos + 5, tile.energyStorage){
            @Override
            protected void addTooltip(List<Component> tooltip) {
                super.addTooltip(tooltip);
                tooltip.add(Component.translatable("tooltip.dysonsphere.heat_generator_production", AssetUtil.FLOAT_FORMAT.format((tile.getCurrentProductionRate()))));
            }
        };
        this.heat = new HeatDisplay(this.leftPos + 7, this.topPos + 5, tile.heatHandler);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        energy.drawOverlay(guiGraphics, mouseX, mouseY);
        heat.drawOverlay(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);
        pGuiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);

        energy.draw(pGuiGraphics);
        heat.draw(pGuiGraphics);
    }
    
}
