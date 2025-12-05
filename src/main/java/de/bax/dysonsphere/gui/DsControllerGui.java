package de.bax.dysonsphere.gui;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.constructs.ModConstructs;
import de.bax.dysonsphere.containers.DSControllerContainer;
import de.bax.dysonsphere.gui.components.ConstructDetails;
import de.bax.dysonsphere.gui.components.ConstructList;
import de.bax.dysonsphere.gui.components.EnergyDisplay;
import de.bax.dysonsphere.tileentities.DSControllerTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DsControllerGui extends BaseGui<DSControllerContainer> {

    public static final ResourceLocation RES_LOC = LaserPatternControllerGui.RES_LOC; //some recycling

    private final DSControllerTile tile;
    private EnergyDisplay energy;

    private ConstructList constructInactiveList, constructActiveList;

    private ConstructDetails details;

    private Button activateButton, deactivateButton, confirmButton, cancelButton;

    public DsControllerGui(DSControllerContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);
        

        this.tile = container.tile;

        this.imageWidth = 265;
        this.imageHeight = 220;
    }

    @Override
    protected void init() {
        super.init();
        this.energy = new EnergyDisplay(this.leftPos - 25, this.topPos + 5, tile.energyStorage);
        this.constructInactiveList = new ConstructList(this.leftPos + 4, this.topPos + 22, 110, 172, 15);

        this.constructActiveList = new ConstructList(this.leftPos + 124, this.topPos + 22, 110, 172, 15);
        
        this.details = new ConstructDetails(this.leftPos + 245, this.topPos, 105, 220);

        ModConstructs.registry().forEach((con) -> {
            constructInactiveList.addEntry(con);
        });
        tile.getEnabledConstructs().forEach((con) -> {
            constructActiveList.addEntry(con, true);
            constructInactiveList.removeEntry(con);
        });

        tile.getDisabledConstructs().forEach((con) -> {
            constructActiveList.addEntry(con, false);
            constructInactiveList.removeEntry(con);
        });
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 240, 220);

        pGuiGraphics.drawString(font, Component.literal("Available"), this.leftPos + 5, this.topPos + 5, 0xFFFFFFFF);
        pGuiGraphics.drawString(font, Component.literal("Integrated"), this.leftPos + 125, this.topPos + 5, 0xFFFFFFFF);

        ConstructList.Entry highlightedEntry = constructActiveList.getHighlightedEntry().orElse(constructInactiveList.getHighlightedEntry().orElse(ConstructList.EMPTY));
        if(highlightedEntry != ConstructList.EMPTY){
            imageWidth = 265 + 85;
            details.setConstruct(highlightedEntry.construct);
            details.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick, highlightedEntry.isEnabled());
            // pGuiGraphics.drawString(font, highlightedEntry.construct.getDisplayName(), this.leftPos + 240, this.topPos + 10, 0xFFFFFFFF);
            // AssetUtil.renderMaxWidthString(pGuiGraphics, highlightedEntry.construct.getDisplayName(), this.leftPos + 250, this.topPos + 10, 80f, 0xFFFFFFFF, 0, 0xF000F0, false);
        } else {
            imageWidth = 265;
        }

        pGuiGraphics.drawString(font, highlightedEntry.construct.getDisplayName(), 0, 0, 0);

        energy.draw(pGuiGraphics);
        constructInactiveList.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        constructActiveList.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        energy.drawOverlay(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(constructInactiveList.isHovered((int) pMouseX, (int) pMouseY)){
            if(constructInactiveList.onMouseClicked((int) pMouseX, (int) pMouseY, pButton)){
                constructActiveList.removeHighlighted();
            }
            return true;
        }
        if(constructActiveList.isHovered((int) pMouseX, (int) pMouseY)){
            if(constructActiveList.onMouseClicked((int) pMouseX, (int) pMouseY, pButton)){
                constructInactiveList.removeHighlighted();
            }
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if(constructInactiveList.isHovered((int) pMouseX, (int) pMouseY)){
            constructInactiveList.onMouseScrolled((int) pMouseX, (int) pMouseY, pDelta);
            return true;
        }
        if(constructActiveList.isHovered((int) pMouseX, (int) pMouseY)){
            constructActiveList.onMouseScrolled((int) pMouseX, (int) pMouseY, pDelta);
            return true;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }
    
}
