package de.bax.dysonsphere.gui;

import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class BaseGui<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public static final ResourceLocation GUI_INVENTORY_LOC = AssetUtil.getGuiLocation("gui_lower");

    public BaseGui(T container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.drawString(this.font, this.title, titleLabelX, titleLabelY, 0xFFFFFF, false);
    }

    @Override
    protected void init() {
        super.init();

        titleLabelX = (int) (imageWidth / 2.0f - font.width(title) / 2.0f);
        titleLabelY = -10;
    }

}
