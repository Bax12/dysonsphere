package de.bax.dysonsphere.gui;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.containers.MelterContainer;
import de.bax.dysonsphere.gui.components.EnergyDisplay;
import de.bax.dysonsphere.gui.components.FluidDisplay;
import de.bax.dysonsphere.gui.components.HeatDisplay;
import de.bax.dysonsphere.tileentities.MelterTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MelterGui extends BaseGui<MelterContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_melter");
    private final MelterTile tile;
    private HeatDisplay heat;
    private EnergyDisplay energy;
    private FluidDisplay fluid;
    private final boolean isInduction;

    public MelterGui(MelterContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);
        

        this.tile = container.tile;

        isInduction = tile instanceof MelterTile.Induction;
        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void init() {
        super.init();
        this.heat = new HeatDisplay(this.leftPos + 7, this.topPos + 5, tile.heatHandler);
        this.fluid = new FluidDisplay(this.leftPos + 140, this.topPos + 5, tile.fluidStorage);
        if(isInduction){
            this.energy = new EnergyDisplay(this.leftPos + 31, this.topPos + 5, ((MelterTile.Induction) tile).energyStorage);
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);//resourcename, onscreenX, onscreenY, pngStartX, pngStartY, pngEndX, pngEndY
        pGuiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);
        heat.draw(pGuiGraphics);
        fluid.draw(pGuiGraphics);
        if(isInduction){
            energy.draw(pGuiGraphics);
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        heat.drawOverlay(guiGraphics, mouseX, mouseY);
        fluid.drawOverlay(guiGraphics, mouseX, mouseY);
        if(isInduction){
            energy.drawOverlay(guiGraphics, mouseX, mouseY);
        }

        if(tile.getProgressScaled(42) > 0){
            guiGraphics.blit(RES_LOC, this.leftPos + 98, this.topPos + 39, 0, 93, tile.getProgressScaled(42), 13);
        }
    }
    
}
