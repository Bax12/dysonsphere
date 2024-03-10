package de.bax.dysonsphere.gui;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import de.bax.dysonsphere.containers.HeatGeneratorContainer;
import de.bax.dysonsphere.gui.components.EnergyDisplay;
import de.bax.dysonsphere.gui.components.HeatDisplay;
import de.bax.dysonsphere.tileentities.HeatGeneratorTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

public class HeatGeneratorGui extends BaseGui<HeatGeneratorContainer> {

    public static final ResourceLocation RES_LOC = AssetUtil.getGuiLocation("gui_heat_generator");
    private final HeatGeneratorTile tile;
    private EnergyDisplay energy;
    private HeatDisplay heat;
    private char axis;

    private HeatDisplay n1Heat;
    private HeatDisplay n2Heat;

    public HeatGeneratorGui(HeatGeneratorContainer container, Inventory inventory, Component pTitle) {
        super(container, inventory, pTitle);

        this.tile = container.tile;
        this.axis = container.axis;

        this.imageWidth = 176;
        this.imageHeight = 93 + 86;
    }

    @Override
    protected void init() {
        super.init();
        this.energy = new EnergyDisplay(this.leftPos + 148, this.topPos + 5, tile.energyStorage);
        this.heat = new HeatDisplay(this.leftPos + 7, this.topPos + 5, tile.heatHandler);

        IHeatContainer n1Handler = null, n2Handler = null;

        if(axis != 0){
            Direction.Axis dirAxis = Direction.Axis.byName((""+axis).toLowerCase());
            Direction dirPos = Direction.fromAxisAndDirection(dirAxis, Direction.AxisDirection.POSITIVE);
            Direction dirNeg = dirPos.getOpposite();
            BlockEntity tilePos = tile.getLevel().getBlockEntity(tile.getBlockPos().relative(dirPos));
            BlockEntity tileNeg = tile.getLevel().getBlockEntity(tile.getBlockPos().relative(dirNeg));
            if(tilePos != null && tileNeg != null){
                LazyOptional<IHeatContainer> heatNegContainer = tilePos.getCapability(DSCapabilities.HEAT, dirNeg);
                LazyOptional<IHeatContainer> heatPosContainer = tileNeg.getCapability(DSCapabilities.HEAT, dirPos);
                n1Handler = heatNegContainer.map((handler) -> {return handler;}).get();
                n2Handler = heatPosContainer.map((handler) -> {return handler;}).get();
            }
        }

        this.n1Heat = new HeatDisplay(this.leftPos + 37, this.topPos + 5, n1Handler){
            @Override
            protected void addTooltip(List<Component> tooltip) {
                super.addTooltip(tooltip);
                tooltip.add(Component.translatable("tooltip.dysonsphere.heat_generator_neighbor_pos", axis)); //for some reason the tiles in the gui are the swapped.
            }                                                                                                           //since it matters nowhere else the tooltips are just swapped as well.
        };
        this.n2Heat = new HeatDisplay(this.leftPos + 118, this.topPos + 5, n2Handler){
            @Override
            protected void addTooltip(List<Component> tooltip) {
                super.addTooltip(tooltip);
                tooltip.add(Component.translatable("tooltip.dysonsphere.heat_generator_neighbor_neg", axis));
            }
        };
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        guiGraphics.blit(GUI_INVENTORY_LOC, this.leftPos, this.topPos + 93, 0, 0, 176, 86);//resourcename, onscreenX, onscreenY, pngStartX, pngStartY, pngEndX, pngEndY
        guiGraphics.blit(RES_LOC, this.leftPos, this.topPos, 0, 0, 176, 93);

        if(this.tile.getHeatDifference() > this.tile.minHeatDifference){
            guiGraphics.blit(RES_LOC, this.leftPos + 82, this.topPos + 36, 176, 0, 12, 21);
        }
        
        // guiGraphics.drawCenteredString(font, Component.literal("Axis: " + (axis != 0 ? "" + axis : "NONE")), this.leftPos + 88, this.topPos + 23, 0xFFFFFFFF);
        // guiGraphics.drawCenteredString(font, Component.literal("Diff: " + tile.getHeatDifference() + "°K"), this.leftPos + 88, this.topPos + 63, 0xFFFFFFFF);

        guiGraphics.drawCenteredString(font, Component.translatable("tooltip.dysonsphere.heat_generator_axis", (axis != 0 ? "" + axis : "NONE")), this.leftPos + 88, this.topPos + 23, 0xFFFFFFFF);
        guiGraphics.drawCenteredString(font, Component.translatable("tooltip.dysonsphere.heat_generator_diff", tile.getHeatDifference()), this.leftPos + 88, this.topPos + 63, 0xFFFFFFFF);

        energy.draw(guiGraphics);
        heat.draw(guiGraphics);
        n1Heat.draw(guiGraphics);
        n2Heat.draw(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        energy.drawOverlay(guiGraphics, mouseX, mouseY);
        heat.drawOverlay(guiGraphics, mouseX, mouseY);
        n1Heat.drawOverlay(guiGraphics, mouseX, mouseY);
        n2Heat.drawOverlay(guiGraphics, mouseX, mouseY);
    }
    
}
