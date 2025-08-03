package de.bax.dysonsphere.gui.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.capabilities.inputHatch.IInputAcceptor;
import de.bax.dysonsphere.gui.BaseGui;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class AcceptorFluidDisplay extends BaseDisplay {

    protected IInputAcceptor acceptor;

    public AcceptorFluidDisplay(int x, int y, IInputAcceptor acceptor) {
        super(x, y);
        this.acceptor = acceptor;
    }

    @Override
    public void draw(GuiGraphics guiGraphics) {
        int capacity = acceptor.getFluidCapacity();
        if(capacity == 0){
            return; //not rendering for no storage.
        }
        
        //base
        guiGraphics.blit(BaseGui.GUI_INVENTORY_LOC, xPos, yPos, 0, 90, width, height);

        //meter
        int offset = 0;
        for(FluidStack fluid : acceptor.getFluidInputs() ){
            //prevents tank from rendering partially empty with multiple full tanks. simply rounding doesn't work
            int renderSize = (int) Math.min((83 * fluid.getAmount() / capacity)+1, 83);
            if(!fluid.isEmpty()){
                renderFluid(guiGraphics, xPos + 1, yPos + 84 - offset, renderSize, fluid);
                offset += renderSize;
            }
        }


        //overlay
        guiGraphics.blit(BaseGui.GUI_INVENTORY_LOC, xPos, yPos, 63, 90, width, height);
    }

    protected void renderFluid(GuiGraphics guiGraphics, int x, int y, int height, FluidStack fluid){
        IClientFluidTypeExtensions clientFluidType = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation LOC = new ResourceLocation(clientFluidType.getStillTexture().getNamespace(), "textures/" + clientFluidType.getStillTexture().getPath() + ".png");
        if(ResourceLocation.isValidResourceLocation(LOC.toString())) {
            int color = clientFluidType.getTintColor(fluid);
            final float red = ( color >> 16 & 255 ) / 255.0F;
            final float green = ( color >> 8 & 255 ) / 255.0F;
            final float blue = ( color & 255 ) / 255.0F;
            RenderSystem.setShaderColor(red, green, blue, 1.0F);
            guiGraphics.blit(LOC, x, y - height, 0, 0, 20, height, 16, 512);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            guiGraphics.fill(x, y - height, 19, height, 0xFFA0A0A0);
        }
    }

    @Override
    protected void addTooltip(List<Component> tooltip) {
        Map<Component, Integer> fluidMap = new HashMap<>();
        acceptor.getFluidInputs().stream().filter(fluid -> !fluid.isEmpty()).forEach(fluid -> {
            //add tooltips in index 0, as for the render we iterate bottom up, so we have the same order here.
            // tooltip.add(0, Component.translatable("tooltip.dysonsphere.fluid_display", fluid.getDisplayName(), AssetUtil.FLOAT_FORMAT.format(Math.round(fluid.getAmount())), AssetUtil.FLOAT_FORMAT.format(Math.round(acceptor.getFluidCapacity()))));
            fluidMap.merge(fluid.getDisplayName(), fluid.getAmount(), Integer::sum);
        });
        if(fluidMap.isEmpty()){
            tooltip.add(Component.translatable("tooltip.dysonsphere.fluid_display", FluidStack.EMPTY.getDisplayName(), AssetUtil.FLOAT_FORMAT.format(Math.round(FluidStack.EMPTY.getAmount())), AssetUtil.FLOAT_FORMAT.format(Math.round(acceptor.getFluidCapacity()))));
        } else {
            for (var entry : fluidMap.entrySet()) {
                tooltip.add(0, Component.translatable("tooltip.dysonsphere.fluid_display", entry.getKey(), AssetUtil.FLOAT_FORMAT.format(Math.round((entry.getValue()))), AssetUtil.FLOAT_FORMAT.format(Math.round(acceptor.getFluidCapacity()))));
            }
        }
    }
    
}
