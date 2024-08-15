package de.bax.dysonsphere.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.capabilities.fluid.FluidTankCustom;
import de.bax.dysonsphere.containers.GrapplingHookHarnessInventoryContainer;
import de.bax.dysonsphere.gui.components.EnergyDisplay;
import de.bax.dysonsphere.gui.components.FluidDisplay;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class GrapplingHookHarnessInventoryGui extends BaseGui<GrapplingHookHarnessInventoryContainer> {
    
    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_grappling_hook_harness_inventory");
    public final ItemStack containingStack;
    private EnergyDisplay energy;
    private FluidDisplay fluid;

    public GrapplingHookHarnessInventoryGui(GrapplingHookHarnessInventoryContainer container, Inventory inventory, Component title){
        super(container, inventory, title);
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
        containingStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent((fluid) -> {
            if(fluid instanceof FluidTankCustom custom){
                this.fluid = new FluidDisplay(this.leftPos + (this.energy != null ? 15 : 35), this.topPos + 5, custom);
            }
        });
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        pGuiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);//resourcename, onscreenX, onscreenY, pngStartX, pngStartY, pngEndX, pngEndY
        pGuiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);


        if(energy != null){
            energy.draw(pGuiGraphics);
        }
        if(fluid != null){
            fluid.draw(pGuiGraphics);
        }
        
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        if(energy != null){
            energy.drawOverlay(guiGraphics, mouseX, mouseY);
        }
        if(fluid != null){
            fluid.drawOverlay(guiGraphics, mouseX, mouseY);
        }
    }
}
