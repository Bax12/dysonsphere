package de.bax.dysonsphere.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.containers.LaserPatternControllerInventoryContainer;
import de.bax.dysonsphere.gui.components.EnergyDisplay;
import de.bax.dysonsphere.tileentities.LaserPatternControllerTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LaserPatternControllerInventoryGui extends BaseGui<LaserPatternControllerInventoryContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_laser_pattern_controller_inventory");
    private final LaserPatternControllerTile tile;
    private EnergyDisplay energy;

    public LaserPatternControllerInventoryGui(LaserPatternControllerInventoryContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);

        this.tile = container.tile;

        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void init() {
        super.init();

        this.energy = new EnergyDisplay(this.leftPos + 15, this.topPos + 5, tile.energyStorage);

        Button editButton = Button.builder(Component.translatable("tooltip.dysonsphere.laser_pattern_controller_edit"), (button) -> {
            if(!tile.inventory.getStackInSlot(0).isEmpty()){
                // ModPacketHandler.INSTANCE.sendToServer(new LaserPatternControllerGuiSwapPackage(false, tile.getBlockPos()));
                tile.sendGuiSwap(false);
            }
        }).bounds(getGuiLeft() + 92, this.getGuiTop() + 50, 36, 20).build();

        this.addRenderableWidget(editButton);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);//resourcename, onscreenX, onscreenY, pngStartX, pngStartY, pngEndX, pngEndY
        pGuiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);

        energy.draw(pGuiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        energy.drawOverlay(guiGraphics, mouseX, mouseY);
    }
    
}
