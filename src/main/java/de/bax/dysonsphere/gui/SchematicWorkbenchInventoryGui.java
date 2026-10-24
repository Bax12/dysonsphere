package de.bax.dysonsphere.gui;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.containers.SchematicWorkbenchInventoryContainer;
import de.bax.dysonsphere.tileentities.SchematicWorkbenchTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SchematicWorkbenchInventoryGui extends BaseGui<SchematicWorkbenchInventoryContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_schematic_workbench_inventory");
    protected final SchematicWorkbenchTile tile;

    public SchematicWorkbenchInventoryGui(SchematicWorkbenchInventoryContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);

        this.tile = container.tile;

        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);
        pGuiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);
    }
    
}
