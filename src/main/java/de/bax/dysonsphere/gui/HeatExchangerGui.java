package de.bax.dysonsphere.gui;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.containers.HeatExchangerContainer;
import de.bax.dysonsphere.gui.components.FluidDisplay;
import de.bax.dysonsphere.gui.components.HeatDisplay;
import de.bax.dysonsphere.tileentities.HeatExchangerTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class HeatExchangerGui extends BaseGui<HeatExchangerContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_heat_exchanger");
    private final HeatExchangerTile tile;
    private HeatDisplay heat;
    private FluidDisplay input;
    private FluidDisplay output;

    public HeatExchangerGui(HeatExchangerContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);

        this.tile = container.tile;

        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void init() {
        super.init();
        this.heat = new HeatDisplay(this.leftPos + 77, this.topPos + 5, tile.heatHandler){
            @Override
            protected void addTooltip(List<Component> tooltip) {
                super.addTooltip(tooltip);
                int produce = 0;
                var recipe = tile.getCurrentRecipe();
                if(recipe.isPresent() && tile.heatHandler.getHeatStored() >= recipe.get().minHeat()){
                    produce = tile.getCurrentProduce();
                }
                tooltip.add(Component.translatable("tooltip.dysonsphere.heat_exchanger_producing", produce)); 
            }
        };
        this.input = new FluidDisplay(this.leftPos + 15, this.topPos + 5, tile.inputTank);
        this.output = new FluidDisplay(this.leftPos + 140, this.topPos + 5, tile.outputTank);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        guiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);//resourcename, onscreenX, onscreenY, pngStartX, pngStartY, pngEndX, pngEndY
        guiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);
        
        // if(tile.heatHandler.getHeatStored() >= tile.getCurrentRecipe().map((recipe) -> {
        //         return recipe.minHeat();
        //         }).orElse(HeatExchangerTile.maxHeat + 1)){
        //     guiGraphics.blit(RES_LOC, this.leftPos + 36, this.topPos + 39, 0, 93, 104, 17);
        // }

        tile.getCurrentRecipe().ifPresent((recipe) -> {
            if(tile.heatHandler.getHeatStored() >= recipe.minHeat()){
                guiGraphics.blit(RES_LOC, this.leftPos + 36, this.topPos + 39, 0, 93, 104, 17);
            }
        });


        heat.draw(guiGraphics);
        input.draw(guiGraphics);
        output.draw(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        heat.drawOverlay(guiGraphics, mouseX, mouseY);
        input.drawOverlay(guiGraphics, mouseX, mouseY);
        output.drawOverlay(guiGraphics, mouseX, mouseY);
    }
    
}
