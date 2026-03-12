package de.bax.dysonsphere.gui;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.containers.ListenerContainer;
import de.bax.dysonsphere.gui.components.EnergyDisplay;
import de.bax.dysonsphere.tileentities.ListenerTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ListenerGui extends BaseGui<ListenerContainer> {

    @Nonnull
    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_listener");
    private final ListenerTile tile;
    private EnergyDisplay energy;
    private Button stateButton;

    public ListenerGui(ListenerContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);
        
        tile = container.tile;

        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void init() {
        super.init();
        this.energy = new EnergyDisplay(this.leftPos + 6, this.topPos + 5, tile.energyStorage);
        this.stateButton = Button.builder(Component.literal("run"), (button) -> onStateButtonPress()).bounds(this.leftPos + 106, this.topPos + 62, 35, 18).build();
        stateButton.setMessage(Component.literal(tile.running ? "Stop" : "Run"));
        stateButton.setTooltip(Tooltip.create(Component.literal(tile.running ? "Current State: Running" : "Current State: Stopped")));

        addRenderableWidget(stateButton);
    }

    protected void onStateButtonPress(){
        tile.running = !tile.running;
        stateButton.setMessage(Component.literal(tile.running ? "Stop" : "Run"));
        stateButton.setTooltip(Tooltip.create(Component.literal(tile.running ? "Current State: Running" : "Current State: Stopped")));
        tile.sendGuiUpdate();
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);//resourcename, onscreenX, onscreenY, pngStartX, pngStartY, pngEndX, pngEndY
        pGuiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);

        energy.draw(pGuiGraphics);
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        energy.drawOverlay(guiGraphics, mouseX, mouseY);
        if(tile.getProgressScaled(63) > 0){
            guiGraphics.blit(RES_LOC, this.leftPos + 83, this.topPos + 43, 0, 93, tile.getProgressScaled(63), 13);
        }
        
    }
    
}
