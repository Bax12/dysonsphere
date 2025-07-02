package de.bax.dysonsphere.gui;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import de.bax.dysonsphere.containers.InputHatchFluidContainer;
import de.bax.dysonsphere.gui.components.FluidDisplay;
import de.bax.dysonsphere.gui.components.HeatDisplay;
import de.bax.dysonsphere.tileentities.InputHatchTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InputHatchFluidGui extends BaseGui<InputHatchFluidContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_input_hatch_fluid");
    private final InputHatchTile.Fluid tile;
    private FluidDisplay fluid;
    private HeatDisplay heat;
    private final boolean isHeat;

    public InputHatchFluidGui(InputHatchFluidContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);
        
        tile = container.tile;
        isHeat = tile instanceof IHeatTile;

        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void init() {
        super.init();
        this.fluid = new FluidDisplay(this.leftPos + 33, this.topPos + 5, tile.fluidStorage);
        if(isHeat){
            this.heat = new HeatDisplay(this.leftPos + 7, this.topPos + 5, tile.heatHandler);
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);//resourcename, onscreenX, onscreenY, pngStartX, pngStartY, pngEndX, pngEndY
        pGuiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);
        fluid.draw(pGuiGraphics);
        if(isHeat){
            heat.draw(pGuiGraphics);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        fluid.drawOverlay(guiGraphics, mouseX, mouseY);
        if(isHeat){
            heat.drawOverlay(guiGraphics, mouseX, mouseY);
        }
    }    
}
