package de.bax.dysonsphere.gui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.containers.DSEnergyReceiverContainer;
import de.bax.dysonsphere.gui.components.HeatDisplay;
import de.bax.dysonsphere.tileentities.DSEnergyReceiverTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DSEnergyReceiverGui extends BaseGui<DSEnergyReceiverContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_ds_energy_receiver");
    private final DSEnergyReceiverTile tile;
    private HeatDisplay heat;
    private EditBox inputBox;

    public DSEnergyReceiverGui(DSEnergyReceiverContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);
        
        tile = container.tile;

        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void init() {
        super.init();
        this.heat = new HeatDisplay(this.leftPos + 15, this.topPos + 5, tile.heatHandler);
        inputBox = new EditBox(font, this.leftPos + 124, this.topPos + 44, 40, 16, Component.translatable("tooltip.dysonsphere.ds_energy_receiver_wanted"));

        inputBox.setValue(Integer.toString(tile.getDsPowerDraw()));
        inputBox.setResponder((text) -> {
            int target = 0;
            if(!text.isEmpty()){
                target = Integer.parseInt(text);
            }
            tile.setDsPowerDraw(target); //screw send and resync
            // ModPacketHandler.INSTANCE.sendToServer(new DSEnergyReceiverGuiUpdatePackage(target, tile.getBlockPos()));
            tile.sendGuiUpdate();
        });
        inputBox.setFilter((String text) -> {
            return StringUtils.isNumeric(text) || text.isEmpty();
        });
        inputBox.setEditable(true);
        inputBox.active = true;
        addRenderableWidget(inputBox);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        guiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);//resourcename, onscreenX, onscreenY, pngStartX, pngStartY, pngEndX, pngEndY
        guiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);

        if(!tile.dsReceiver.canReceive() || !tile.getLevel().getCapability(DSCapabilities.DYSON_SPHERE).isPresent()){
            guiGraphics.blit(RES_LOC, this.leftPos + 71, this.topPos + 28, 176, 0, 34, 34);
        }

        heat.draw(guiGraphics);
        guiGraphics.drawCenteredString(font, Component.translatable("tooltip.dysonsphere.ds_energy_receiver_energy"), this.leftPos + 144, this.topPos + 28, 0xFFFFFFFF);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        heat.drawOverlay(guiGraphics, mouseX, mouseY);
        if(mouseX >= this.leftPos + 73 && mouseY >= this.topPos + 29 && mouseX < this.leftPos + 73 + 34 && mouseY < this.topPos + 29 + 34 && (!tile.dsReceiver.canReceive() || !tile.getLevel().getCapability(DSCapabilities.DYSON_SPHERE).isPresent())){
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("tooltip.dysonsphere.ds_energy_receiver_nosky"));
            Minecraft mc = Minecraft.getInstance();
            guiGraphics.renderComponentTooltip(mc.font, tooltip, mouseX, mouseY);
        }
    }
    
}
