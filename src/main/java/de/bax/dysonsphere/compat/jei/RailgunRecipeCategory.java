package de.bax.dysonsphere.compat.jei;

import java.util.List;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.gui.BaseGui;
import de.bax.dysonsphere.gui.RailgunGui;
import de.bax.dysonsphere.recipes.OrbitalLaunchRecipe;
import de.bax.dysonsphere.util.AssetUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class RailgunRecipeCategory implements IRecipeCategory<OrbitalLaunchRecipe> {
    
    @Override
    public RecipeType<OrbitalLaunchRecipe> getRecipeType() {
        return RecipeType.create(DysonSphere.MODID, "railgun", OrbitalLaunchRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("dysonsphere.recipe.railgun");
    }

    @Override
    public IDrawable getBackground() {
        return DSJeiPlugin.guiHelper.drawableBuilder(RailgunGui.RES_LOC, 50, 11, 65, 73).build();
    }

    @Override
    public IDrawable getIcon() {
        return DSJeiPlugin.guiHelper.createDrawableItemStack(ModBlocks.RAILGUN_BLOCK.get().asItem().getDefaultInstance());
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull OrbitalLaunchRecipe recipe, @Nonnull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 53).addIngredients(recipe.input());
    }

    @Override
    public void draw(@Nonnull OrbitalLaunchRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IDrawableStatic powerScale = DSJeiPlugin.guiHelper.drawableBuilder(BaseGui.GUI_INVENTORY_LOC, 43, 91, 9, 64).build();
        powerScale.draw(guiGraphics, 5, 5);
    }

    @Override
    public List<Component> getTooltipStrings(@Nonnull OrbitalLaunchRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if(mouseX >= 5 && mouseX <= 14 && mouseY >= 5 && mouseY <= 69){
            // NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
            return List.of(Component.translatable("tooltip.dysonsphere.railgun_base_launch_energy", AssetUtil.FLOAT_FORMAT.format(recipe.baseEnergy())));
        }
        return List.of();
    }



}
