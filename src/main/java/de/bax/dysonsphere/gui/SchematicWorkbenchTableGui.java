package de.bax.dysonsphere.gui;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.containers.SchematicWorkbenchTableContainer;
import de.bax.dysonsphere.tileentities.SchematicWorkbenchTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SchematicWorkbenchTableGui extends BaseGui<SchematicWorkbenchTableContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_schematic_workbench_table");
    protected final SchematicWorkbenchTile tile;

    public SchematicWorkbenchTableGui(SchematicWorkbenchTableContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);

        this.tile = container.tile;

        this.imageWidth = 385;
        this.imageHeight = 220;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 385, 220);

    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
    
}
