package de.bax.dysonsphere.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.containers.LaserControllerContainer;
import de.bax.dysonsphere.gui.components.EnergyDisplay;
import de.bax.dysonsphere.gui.components.NumberInput;
import de.bax.dysonsphere.network.GuiButtonPressedPackage;
import de.bax.dysonsphere.network.ModPacketHandler;
import de.bax.dysonsphere.tileentities.LaserControllerTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FittingMultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class LaserControllerGui extends BaseGui<LaserControllerContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_laser_controller");
    private final LaserControllerTile tile;
    private EnergyDisplay energy;
    protected int ticksPassed = 0;

    protected NumberInput xInput, yInput, zInput;
    protected Button launchButton;
    protected Button claimButton;
    protected FittingMultiLineTextWidget ownerLabel;


    public LaserControllerGui(LaserControllerContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);

        tile = container.tile;

        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void init() {
        super.init();
        this.energy = new EnergyDisplay(this.leftPos - 25, this.topPos + 5, tile.energyStorage);

        xInput = new NumberInput(getGuiLeft() + 65, getGuiTop() + 5, Component.literal("x"), font, true, Integer.MIN_VALUE);
        yInput = new NumberInput(getGuiLeft() + 65, getGuiTop() + 35, Component.literal("y"), font, true, Integer.MIN_VALUE);
        zInput = new NumberInput(getGuiLeft() + 65, getGuiTop() + 65, Component.literal("z"), font, true, Integer.MIN_VALUE);

        xInput.setValue(tile.getTargetX());
        yInput.setValue(tile.getTargetY());
        zInput.setValue(tile.getTargetZ());

        launchButton = Button.builder(Component.literal("Launch"), (button) -> {
            ModPacketHandler.INSTANCE.sendToServer(new GuiButtonPressedPackage(tile.getBlockPos(), 0));
            launchButton.active = false;
            launchButton.setTooltip(Tooltip.create(Component.literal("Launching...")));
        }).bounds(getGuiLeft() + 5, getGuiTop() + 62, 39, 20).build();

        claimButton = Button.builder(Component.literal("Claim"), (button) -> {
            ModPacketHandler.INSTANCE.sendToServer(new GuiButtonPressedPackage(tile.getBlockPos(), 1));
        }).bounds(getGuiLeft() + 5, getGuiTop() + 29, 39, 20).build();

        ownerLabel = new FittingMultiLineTextWidget(getGuiLeft() + 5, getGuiTop() + 10, 60, 20, Component.literal("Owner: " + tile.getOwner().getName().getString()), font);
        updateComponents();
        


        this.addRenderableWidget(xInput);
        this.addRenderableWidget(yInput);
        this.addRenderableWidget(zInput);
        this.addRenderableWidget(launchButton);
        this.addRenderableWidget(claimButton);
        this.addRenderableWidget(ownerLabel);
    }

    protected void updateComponents(){
        if(tile.getOwner() != null) {
            tile.getOwner().getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((orbitalLaser) -> {
                int laserCount = orbitalLaser.getLasersAvailable(tile.getOwner().tickCount);
                ownerLabel.setTooltip(Tooltip.create(Component.literal("Lasers Available: " + laserCount)));
                if(tile.isWorking() || tile.isOnCooldown()){
                    launchButton.active = false;
                    launchButton.setTooltip(Tooltip.create(tile.isWorking() ? Component.literal("Launching...") : Component.literal("On Cooldown")));
                } else {
                    if(laserCount < tile.getPattern().getLasersRequired()){
                        launchButton.setTooltip(Tooltip.create(Component.literal("Not enough Lasers!")));
                        launchButton.active = false;
                    } else {
                        launchButton.setTooltip(null);
                        launchButton.active = true;
                    }
                }
                
            });
            if(tile.getOwner().is(this.getMinecraft().player)){
                claimButton.active = false;
            } else {
                claimButton.active = true;
            }
            
        } else {
            launchButton.active = false;
            launchButton.setTooltip(Tooltip.create(Component.literal("No owner!")));
        }

    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);//resourcename, onscreenX, onscreenY, pngStartX, pngStartY, pngEndX, pngEndY
        pGuiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);

        pGuiGraphics.blit(RES_LOC, this.leftPos - 26, this.topPos + 4, 10, 5, 23, 87);

        energy.draw(pGuiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        energy.drawOverlay(guiGraphics, mouseX, mouseY);
    }

    protected void updateTargetCoords(){
        if(xInput.getValue() != tile.getTargetX() || yInput.getValue() != tile.getTargetY() || zInput.getValue() != tile.getTargetZ()){
            tile.setTargetX(xInput.getValueInt());
            tile.setTargetY(yInput.getValueInt());
            tile.setTargetZ(zInput.getValueInt());
            tile.sendGuiUpdate();
        }
    }

    @Override
    protected void containerTick() {
        if(ticksPassed++ % 20 == 0){
            updateTargetCoords();
            updateComponents();
        }

    }

    @Override
    public void onClose() {
        updateTargetCoords();
        super.onClose();
    }
    
}
