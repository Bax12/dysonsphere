package de.bax.dysonsphere.compat.jei;

import java.util.List;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.gui.BaseGui;
import de.bax.dysonsphere.gui.HeatExchangerGui;
import de.bax.dysonsphere.util.AssetUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;

public class HeatExchangerCategory implements IRecipeCategory<HeatExchangerCategory.HeatExchangerRecipe> {
    
    @Override
    public RecipeType<HeatExchangerRecipe> getRecipeType() {
        return RecipeType.create(DysonSphere.MODID, "heat_exchanger", HeatExchangerCategory.HeatExchangerRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("dysonsphere.recipe.heat_exchanger");
    }

    @Override
    public IDrawable getBackground() {
        return DSJeiPlugin.guiHelper.drawableBuilder(HeatExchangerGui.RES_LOC, 50, 4, 72, 36).build();
    }

    @Override
    public IDrawable getIcon() {
        return DSJeiPlugin.guiHelper.createDrawableItemStack(ModBlocks.HEAT_EXCHANGER_BLOCK.get().asItem().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HeatExchangerRecipe recipe, IFocusGroup focuses) {
        IDrawable overlay = DSJeiPlugin.guiHelper.drawableBuilder(BaseGui.GUI_INVENTORY_LOC, 0, 180, 12, 28).build();
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 5).addFluidStack(recipe.input.getFluid(), recipe.input.getAmount()).setFluidRenderer(100, false, 10, 26).setOverlay(overlay, -1, -1);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 52, 5).addFluidStack(recipe.output.getFluid(), recipe.output.getAmount()).setFluidRenderer(100, false, 10, 26).setOverlay(overlay, -1, -1);
    }

    @Override
    public void draw(HeatExchangerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IDrawableStatic heatScale = DSJeiPlugin.guiHelper.drawableBuilder(BaseGui.GUI_INVENTORY_LOC, 106, 142, 9, 32).build();
        heatScale.draw(guiGraphics, 29, 2);
    }

    @Override
    public List<Component> getTooltipStrings(HeatExchangerRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if(mouseX >= 29 && mouseX <= 38 && mouseY >= 2 && mouseY <= 34){
            // NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
            return List.of(Component.translatable("tooltip.dysonsphere.heat_exchanger_min_heat", AssetUtil.FLOAT_FORMAT.format(recipe.heat)));
        }
        return List.of();
    }

    public static class HeatExchangerRecipe {
        FluidStack input;
        FluidStack output;
        double heat;

        public HeatExchangerRecipe(FluidStack input, FluidStack output, double heat){
            this.input = input;
            this.output = output;
            this.heat = heat;
        }
    }

}
