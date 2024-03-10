package de.bax.dysonsphere.gui.components;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.fluid.FluidTankCustom;
import de.bax.dysonsphere.gui.BaseGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

public class FluidDisplay extends BaseDisplay {

    protected FluidTankCustom fluid;

    public FluidDisplay(int x, int y, FluidTankCustom fluidTank) {
        super(x, y);

        this.fluid = fluidTank;
    }

    @Override
    public void draw(GuiGraphics guiGraphics) {
        //base
        guiGraphics.blit(BaseGui.GUI_INVENTORY_LOC, xPos, yPos, 0, 90, width, height);

        //meter
        if(fluid.getFluidAmount() > 0){
            int renderSize = (int) (83 * fluid.getFluidAmount() / fluid.getCapacity());
            ResourceLocation LOC = new ResourceLocation(IClientFluidTypeExtensions.of(fluid.getFluid().getFluid()).getStillTexture().getNamespace(), "textures/" + IClientFluidTypeExtensions.of(fluid.getFluid().getFluid()).getStillTexture().getPath() + ".png");
            DysonSphere.LOGGER.info("FluidDisplay ResourceLocation: {}", LOC);
            if(ResourceLocation.isValidResourceLocation(LOC.toString())) {
                guiGraphics.blit(LOC, xPos + 1, yPos + 84 - renderSize, 0, 0, 16, renderSize, 16, 512);
            } else {
                guiGraphics.fill(xPos + 1, yPos + 84 -renderSize, 19, renderSize, 0xFFA0A0A0);
            }
        }
        //overlay
        guiGraphics.blit(BaseGui.GUI_INVENTORY_LOC, xPos, yPos, 63, 90, width, height);
    }

    @Override
    protected void addTooltip(List<Component> tooltip) {    
        NumberFormat format = NumberFormat.getNumberInstance(Locale.ENGLISH);

        tooltip.add(Component.translatable("tooltip.dysonsphere.fluid_display", fluid.getFluid().getDisplayName(), format.format(Math.round(fluid.getFluidAmount())), format.format(Math.round(fluid.getCapacity()))));
    }
    


}
