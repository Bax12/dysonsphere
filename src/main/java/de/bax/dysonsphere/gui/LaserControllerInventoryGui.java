package de.bax.dysonsphere.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.containers.LaserControllerInventoryContainer;
import de.bax.dysonsphere.gui.components.EnergyDisplay;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class LaserControllerInventoryGui extends BaseGui<LaserControllerInventoryContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_laser_controller_inventory");
    public final ItemStack containingStack;
    private EnergyDisplay energy;

    public LaserControllerInventoryGui(LaserControllerInventoryContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);

        containingStack = container.containingStack;

        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void init() {
        super.init();

        containingStack.getCapability(ForgeCapabilities.ENERGY).ifPresent((energy) -> {
            this.energy = new EnergyDisplay(this.leftPos + 15, this.topPos + 5, energy);
        });

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
