package de.bax.dysonsphere.gui;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.containers.RailgunContainer;
import de.bax.dysonsphere.gui.components.EnergyDisplay;
import de.bax.dysonsphere.tileentities.RailgunTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RailgunGui extends BaseGui<RailgunContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_railgun");
    private final RailgunTile tile;
    private EnergyDisplay energy;

    public RailgunGui(RailgunContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);

        tile = container.tile;

        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void init() {
        super.init();
        this.energy = new EnergyDisplay(this.leftPos + 15, this.topPos + 5, tile.energyStorage){
            @Override
            protected void addTooltip(List<Component> tooltip) {
                super.addTooltip(tooltip);
                NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
                tooltip.add(Component.translatable("tooltip.dysonsphere.railgun_launch_energy", format.format(tile.LAUNCH_ENERGY)));
            }
            @Override
            public void draw(GuiGraphics guiGraphics) {
                super.draw(guiGraphics);
                guiGraphics.hLine(this.xPos + 1, this.xPos + 19, yPos + 85 - (85 * tile.LAUNCH_ENERGY / energy.getMaxEnergyStored()), 0xFF2e6dff);
            }
        };
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        guiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);//resourcename, onscreenX, onscreenY, pngStartX, pngStartY, pngEndX, pngEndY
        guiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);

        if(this.tile.energyStorage.getEnergyStored() > 0){
            int i = (int) Math.min((39f * tile.energyStorage.getEnergyStored() / tile.LAUNCH_ENERGY), 39);
            guiGraphics.blit(RES_LOC, this.leftPos + 78, this.topPos + 60 - i, 176, 39 - i, 20, 39);
        }

        energy.draw(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        energy.drawOverlay(guiGraphics, mouseX, mouseY);
    }

}
