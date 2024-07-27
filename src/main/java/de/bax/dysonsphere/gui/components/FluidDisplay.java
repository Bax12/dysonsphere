package de.bax.dysonsphere.gui.components;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.fluid.FluidTankCustom;
import de.bax.dysonsphere.gui.BaseGui;
import de.bax.dysonsphere.util.AssetUtil;
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
            IClientFluidTypeExtensions clientFluidType = IClientFluidTypeExtensions.of(fluid.getFluid().getFluid());
            ResourceLocation LOC = new ResourceLocation(clientFluidType.getStillTexture().getNamespace(), "textures/" + clientFluidType.getStillTexture().getPath() + ".png");
            if(ResourceLocation.isValidResourceLocation(LOC.toString())) {
                int color = clientFluidType.getTintColor(fluid.getFluid());
                final float red = ( color >> 16 & 255 ) / 255.0F;
                final float green = ( color >> 8 & 255 ) / 255.0F;
                final float blue = ( color & 255 ) / 255.0F;
                RenderSystem.setShaderColor(red, green, blue, 1.0F);
                guiGraphics.blit(LOC, xPos + 1, yPos + 84 - renderSize, 0, 0, 20, renderSize, 16, 512);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                guiGraphics.fill(xPos + 1, yPos + 84 -renderSize, 19, renderSize, 0xFFA0A0A0);
            }
        }
        //overlay
        guiGraphics.blit(BaseGui.GUI_INVENTORY_LOC, xPos, yPos, 63, 90, width, height);
    }

    @Override
    protected void addTooltip(List<Component> tooltip) {    
        tooltip.add(Component.translatable("tooltip.dysonsphere.fluid_display", fluid.getFluid().getDisplayName(), AssetUtil.FLOAT_FORMAT.format(Math.round(fluid.getFluidAmount())), AssetUtil.FLOAT_FORMAT.format(Math.round(fluid.getCapacity()))));
    }
    


}
