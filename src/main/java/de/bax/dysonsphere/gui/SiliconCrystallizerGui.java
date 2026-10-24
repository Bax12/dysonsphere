package de.bax.dysonsphere.gui;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.containers.SiliconCrystallizerContainer;
import de.bax.dysonsphere.gui.components.FluidDisplay;
import de.bax.dysonsphere.gui.components.HeatDisplay;
import de.bax.dysonsphere.tileentities.SiliconCrystallizerTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SiliconCrystallizerGui extends BaseGui<SiliconCrystallizerContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_silicon_crystallizer");
    protected final SiliconCrystallizerTile tile;
    private HeatDisplay heat;
    private FluidDisplay fluid;

    public SiliconCrystallizerGui(SiliconCrystallizerContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);
        

        this.tile = container.tile;

        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void init() {
        super.init();
        this.fluid = new FluidDisplay(this.leftPos + 30, this.topPos + 5, tile.inputTank);
        this.heat = new HeatDisplay(this.leftPos + 7, this.topPos + 5, tile.heatHandler);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);
        pGuiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);
        heat.draw(pGuiGraphics);
        fluid.draw(pGuiGraphics);
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        heat.drawOverlay(guiGraphics, mouseX, mouseY);
        fluid.drawOverlay(guiGraphics, mouseX, mouseY);

        if(tile.getProgressScaled(63) > 0){
            guiGraphics.blit(RES_LOC, this.leftPos + 52, this.topPos + 41, 0, 93, tile.getProgressScaled(63), 13);
        }
    }
    
}
