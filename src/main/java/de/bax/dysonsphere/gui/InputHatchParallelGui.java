package de.bax.dysonsphere.gui;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import de.bax.dysonsphere.containers.InputHatchParallelContainer;
import de.bax.dysonsphere.gui.components.HeatDisplay;
import de.bax.dysonsphere.tileentities.InputHatchTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InputHatchParallelGui extends BaseGui<InputHatchParallelContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_input_hatch_parallel");
    private final InputHatchTile.Parallel tile;
    private HeatDisplay heat;
    private final boolean isHeat;

    public InputHatchParallelGui(InputHatchParallelContainer container, Inventory inventory, Component pTitle){
        super(container, inventory, pTitle);

        tile = container.tile;
        isHeat = tile instanceof IHeatTile;

        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void init() {
        super.init();
        if(isHeat){
            this.heat = new HeatDisplay(this.leftPos + 7, this.topPos + 5, tile.heatHandler);
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);//resourcename, onscreenX, onscreenY, pngStartX, pngStartY, pngEndX, pngEndY
        pGuiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);
        if(isHeat){
            heat.draw(pGuiGraphics);
        }
        
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        if(isHeat){
            heat.drawOverlay(guiGraphics, mouseX, mouseY);
        }
    }

}
