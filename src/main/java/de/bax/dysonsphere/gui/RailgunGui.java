package de.bax.dysonsphere.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.containers.RailgunContainer;
import de.bax.dysonsphere.gui.components.EnergyDisplay;
import de.bax.dysonsphere.tileentities.RailgunTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RailgunGui extends BaseGui<RailgunContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_railgun");
    private final RailgunTile tile;
    private EnergyDisplay energy;
    // private EnergyDisplay energyInput;
    private int ticksPassed = 0;

    public RailgunGui(RailgunContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);

        tile = container.tile;

        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void init() {
        super.init();

        // boolean extraEnergy = tile.acceptorStorage.getEnergyStored() > 0;
        // boolean extraEnergy = false;
        // this.energy = new EnergyDisplay(this.leftPos + (extraEnergy ? 35 : 15), this.topPos + 5, tile.energyStorage){
        //     @Override
        //     protected void addTooltip(List<Component> tooltip) {
        //         if(extraEnergy) {
        //             tooltip.add(Component.translatable("tooltip.dysonsphere.energy_internal"));
        //         }
        //         super.addTooltip(tooltip);
        //         tooltip.add(Component.translatable("tooltip.dysonsphere.railgun_launch_energy", AssetUtil.FLOAT_FORMAT.format(tile.getLaunchEnergy())));
        //     }
        //     @Override
        //     public void draw(GuiGraphics guiGraphics) {
        //         super.draw(guiGraphics);
        //         if(!extraEnergy){
        //             guiGraphics.hLine(this.xPos + 1, this.xPos + 19, yPos + 85 - (85 * tile.getLaunchEnergy() / energy.getMaxEnergyStored()), 0xFF2e6dff);
        //         }
        //     }
        // };
        // if(extraEnergy){
        //     this.energyInput = new EnergyDisplay(this.leftPos + 10, this.topPos + 5, tile.acceptorStorage){
        //         @Override
        //         protected void addTooltip(List<Component> tooltip) {
        //             tooltip.add(Component.translatable("tooltip.dysonsphere.energy_external"));
        //             super.addTooltip(tooltip);
        //             tooltip.add(Component.translatable("tooltip.dysonsphere.railgun_launch_energy", AssetUtil.FLOAT_FORMAT.format(tile.getLaunchEnergy())));
        //         }
        //         @Override
        //         public void draw(GuiGraphics guiGraphics) {
        //             super.draw(guiGraphics);
        //             guiGraphics.hLine(this.xPos + 1, this.xPos + 19, yPos + 85 - (85 * tile.getLaunchEnergy() / energy.getMaxEnergyStored()), 0xFF2e6dff);
        //         }
        //     };
        // }

        this.energy = new EnergyDisplay(this.leftPos + 10, this.topPos + 5, tile.acceptorStorage){
            @Override
            protected void addTooltip(List<Component> tooltip) {
                super.addTooltip(tooltip);
                tooltip.add(Component.translatable("tooltip.dysonsphere.railgun_launch_energy", AssetUtil.FLOAT_FORMAT.format(tile.getLaunchEnergy())));
                tooltip.add(Component.translatable("tooltip.dysonsphere.railgun_launch_mult", AssetUtil.FLOAT_FORMAT.format(tile.getLaunchMult())));
            }
            @Override
            public void draw(GuiGraphics guiGraphics) {
                super.draw(guiGraphics);
                int launchEnergy = tile.getLaunchEnergy();
                if(launchEnergy > 0){
                    guiGraphics.hLine(this.xPos + 1, this.xPos + 19, yPos + 85 - (85 * launchEnergy / energy.getMaxEnergyStored()), 0xFF2e6dff);
                }
                
            }
        };
        
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float f, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        guiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);//resourcename, onscreenX, onscreenY, pngStartX, pngStartY, pngEndX, pngEndY
        guiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);

        if(this.tile.acceptorStorage.getEnergyStored() > 0 && tile.getLaunchEnergy() > 0){
            int i = (int) Math.min(tile.getEnergyScaled(39f), 39);
            guiGraphics.blit(RES_LOC, this.leftPos + 78, this.topPos + 60 - i, 176, 39 - i, 20, 39);
        }

        if(!tile.canSeeSky()){
            guiGraphics.blit(RES_LOC, this.leftPos + 73, this.topPos + 5, 196, 0, 30, 4);

            if(ticksPassed++ % 240 <= 120){
                guiGraphics.blit(RES_LOC, this.leftPos + 87, this.topPos + 4, 196, 4, 2, 10);
            }
        }

        energy.draw(guiGraphics);
        // if(energyInput != null){
        //     energyInput.draw(guiGraphics);
        // }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        energy.drawOverlay(guiGraphics, mouseX, mouseY);
        // if(energyInput != null){
        //     energyInput.drawOverlay(guiGraphics, mouseX, mouseY);
        // }
        if(mouseX >= this.leftPos + 77 && mouseY >= this.topPos + 2 && mouseX < this.leftPos + 100 && mouseY < this.topPos + 20){
            List<Component> tooltip = new ArrayList<>();
            if((!tile.canSeeSky())){
                tooltip.add(Component.translatable("tooltip.dysonsphere.railgun_nosky"));
            }
            if(!tile.canAddToDS()){
                tooltip.add(Component.translatable("tooltip.dysonsphere.railgun_dsfull"));
            }
            if(!tooltip.isEmpty()){
                Minecraft mc = Minecraft.getInstance();
                guiGraphics.renderComponentTooltip(mc.font, tooltip, mouseX, mouseY);
            }
        }
    }

}
