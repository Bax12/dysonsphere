package de.bax.dysonsphere.gui;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.constructs.ModConstructs;
import de.bax.dysonsphere.containers.DSControllerContainer;
import de.bax.dysonsphere.gui.components.ConstructDetails;
import de.bax.dysonsphere.gui.components.ConstructList;
import de.bax.dysonsphere.gui.components.DsDetails;
import de.bax.dysonsphere.gui.components.EnergyDisplay;
import de.bax.dysonsphere.tileentities.DSControllerTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DsControllerGui extends BaseGui<DSControllerContainer> {

    public static final ResourceLocation RES_LOC = LaserPatternControllerGui.RES_LOC; //some recycling

    private final DSControllerTile tile;
    private EnergyDisplay energy;

    private ConstructList constructInactiveList, constructActiveList;

    private ConstructDetails conDetails;

    private DsDetails dsDetails;

    private Button addButton, removeButton, enableButton, disableButton, confirmButton, refreshButton;

    public DsControllerGui(DSControllerContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);
        

        this.tile = container.tile;

        this.imageWidth = 265;
        this.imageHeight = 220;
    }

    @SuppressWarnings("null")
    @Override
    protected void init() {
        super.init();
        this.energy = new EnergyDisplay(this.leftPos - 25, this.topPos + 120, tile.energyStorage);
        this.constructInactiveList = new ConstructList(this.leftPos + 4, this.topPos + 22, 110, 172, 15);

        this.constructActiveList = new ConstructList(this.leftPos + 124, this.topPos + 22, 110, 172, 15);
        
        this.conDetails = new ConstructDetails(this.leftPos + 245, this.topPos, 105, 220);
        this.conDetails.setDsParts(tile.getDsParts());

        this.dsDetails = new DsDetails(tile, this.leftPos - 120, this.topPos + 5, 118, 105);

        loadConstructs();



        this.addRenderableOnly(constructInactiveList);
        this.addRenderableOnly(constructActiveList);
        this.addRenderableOnly(dsDetails);

        addButton = new PlainTextButton(this.leftPos + 116, this.topPos + 80, 6, 20, Component.translatable("tooltip.dysonsphere.ds_controller_add_button"), (button) -> addConstructButtonPress(), font);
        addButton.setTooltip(Tooltip.create(Component.translatable("tooltip.dysonsphere.ds_controller_add_button_desc")));
        removeButton = new PlainTextButton(this.leftPos + 116, this.topPos + 105, 6, 20, Component.translatable("tooltip.dysonsphere.ds_controller_remove_button"), (button) -> removeConstructButtonPress(), font);
        removeButton.setTooltip(Tooltip.create(Component.translatable("tooltip.dysonsphere.ds_controller_remove_button_desc")));
        
        enableButton = new Button.Builder(Component.translatable("tooltip.dysonsphere.ds_controller_enable_button"), (button) -> enableConstructButtonPress(true)).bounds(this.leftPos + 128, this.topPos + 198, 50, 15).
            tooltip(Tooltip.create(Component.translatable("tooltip.dysonsphere.ds_controller_enable_button_desc"))).build();
        disableButton = new Button.Builder(Component.translatable("tooltip.dysonsphere.ds_controller_disable_button"), (button) -> enableConstructButtonPress(false)).bounds(this.leftPos + 183, this.topPos + 198, 50, 15).
            tooltip(Tooltip.create(Component.translatable("tooltip.dysonsphere.ds_controller_disable_button_desc"))).build();

        confirmButton = new Button.Builder(Component.translatable("tooltip.dysonsphere.ds_controller_confirm_button"), (button) -> confirmButtonPress()).bounds(this.leftPos + 6, this.topPos + 198, 50, 15).
            tooltip(Tooltip.create(Component.translatable("tooltip.dysonsphere.ds_controller_confirm_button_desc", DSControllerTile.COMMAND_ENERGY))).build();

        refreshButton = new Button.Builder(Component.translatable("tooltip.dysonsphere.ds_controller_refresh_button"), (button) -> refreshButtonPress()).bounds(this.leftPos + 61, this.topPos + 198, 50, 15).
            tooltip(Tooltip.create(Component.translatable("tooltip.dysonsphere.ds_controller_refresh_button_desc"))).build();

        this.addRenderableWidget(addButton);
        this.addRenderableWidget(removeButton);
        this.addRenderableWidget(enableButton);
        this.addRenderableWidget(disableButton);
        this.addRenderableWidget(confirmButton);
        this.addRenderableWidget(refreshButton);
    }

    protected void addConstructButtonPress(){
        constructInactiveList.getSelectedEntry().ifPresent((entry) -> {
            constructActiveList.addEntry(entry.construct);
            constructInactiveList.removeEntry(entry.construct);
        });
    }

    protected void removeConstructButtonPress(){
        constructActiveList.getSelectedEntry().ifPresent((entry) -> {
            constructInactiveList.addEntry(entry.construct);
            constructActiveList.removeEntry(entry.construct);
        });
    }

    protected void enableConstructButtonPress(boolean enable){
        constructActiveList.getSelectedEntry().ifPresent((entry) -> {
            entry.setEnabled(enable);
        });
    }

    @SuppressWarnings("null")
    protected void confirmButtonPress(){
        tile.setConstructs(constructActiveList.getEnabledConstructs(), constructActiveList.getDisabledConstructs());

        tile.sendGuiUpdate();
    }

    protected void refreshButtonPress(){
        constructActiveList.clear();
        constructInactiveList.clear();
        loadConstructs();
    }

    protected void loadConstructs(){
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

    @SuppressWarnings("null")
    @Override
    protected void renderBg(@Nonnull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 240, 220);

        // pGuiGraphics.drawString(font, Component.literal("Available Constructs"), this.leftPos + 5, this.topPos + 5, 0xFFFFFFFF);
        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.translatable("tooltip.dysonsphere.ds_controller_available_header"), this.leftPos + 5, this.topPos + 5, 108, 0xFFFFFFFF, 0, 0xF000F0, true);
        // pGuiGraphics.drawString(font, Component.literal("Integrated Constructs"), this.leftPos + 125, this.topPos + 5, 0xFFFFFFFF);
        AssetUtil.renderMaxWidthString(pGuiGraphics, Component.translatable("tooltip.dysonsphere.ds_controller_integrated_header"), this.leftPos + 125, this.topPos + 5, 108, 0xFFFFFFFF, 0, 0xF000F0, true);

        ConstructList.Entry highlightedEntry = constructActiveList.getHighlightedEntry().orElse(constructInactiveList.getHighlightedEntry().orElse(ConstructList.EMPTY));
        if(highlightedEntry != ConstructList.EMPTY){
            imageWidth = 265 + 85;
            conDetails.setConstruct(highlightedEntry.construct);
            conDetails.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick, highlightedEntry.isEnabled());
            // pGuiGraphics.drawString(font, highlightedEntry.construct.getDisplayName(), this.leftPos + 240, this.topPos + 10, 0xFFFFFFFF);
            // AssetUtil.renderMaxWidthString(pGuiGraphics, highlightedEntry.construct.getDisplayName(), this.leftPos + 250, this.topPos + 10, 80f, 0xFFFFFFFF, 0, 0xF000F0, false);
        } else {
            imageWidth = 265;
        }

        // pGuiGraphics.drawString(font, highlightedEntry.construct.getDisplayName(), 0, 0, 0);

        energy.draw(pGuiGraphics);
        // constructInactiveList.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        // constructActiveList.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        confirmButton.active = tile.energyStorage.getEnergyStored() >= DSControllerTile.COMMAND_ENERGY;
        

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
