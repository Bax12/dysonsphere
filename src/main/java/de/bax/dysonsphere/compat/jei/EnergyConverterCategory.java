package de.bax.dysonsphere.compat.jei;

import java.util.List;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.gui.BaseGui;
import de.bax.dysonsphere.recipes.OrbitalLaunchRecipe;
import de.bax.dysonsphere.tileentities.EnergyConverterTile;
import de.bax.dysonsphere.util.AssetUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

public class EnergyConverterCategory implements IRecipeCategory<de.bax.dysonsphere.compat.jei.EnergyConverterCategory.Recipe> {
    
    protected static record Recipe(Ingredient output, Ingredient base) {}

    protected IDrawableStatic powerScale;

    @Override
    public RecipeType<de.bax.dysonsphere.compat.jei.EnergyConverterCategory.Recipe> getRecipeType() {
        return RecipeType.create(DysonSphere.MODID, "energy_converter", Recipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("dysonsphere.recipe.energy_converter");
    }

    @Override
    public IDrawable getBackground() {
        return DSJeiPlugin.guiHelper.drawableBuilder(BaseGui.GUI_INVENTORY_LOC, 57, 209, 18, 10).addPadding(10, 10, 35, 28).build();
    }

    @Override
    public IDrawable getIcon() {
        return DSJeiPlugin.guiHelper.createDrawableItemStack(ModBlocks.ENERGY_CONVERTER_BLOCK.get().asItem().getDefaultInstance());
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull de.bax.dysonsphere.compat.jei.EnergyConverterCategory.Recipe recipe, @Nonnull IFocusGroup focuses) {
        powerScale = DSJeiPlugin.guiHelper.drawableBuilder(BaseGui.GUI_INVENTORY_LOC, 43, 91, 9, 28).build();
        IRecipeSlotTooltipCallback callback = (view, tooltip) -> {
            tooltip.add(Component.translatable("tooltip.dysonsphere.energy_converter_base"));
        };
        builder.addSlot(RecipeIngredientRole.OUTPUT, 57, 8).addIngredients(recipe.output()).addTooltipCallback(callback);
        builder.addSlot(RecipeIngredientRole.CATALYST, 18, 8).addIngredients(recipe.base()).addTooltipCallback(callback);
    }

    @Override
    public void draw(@Nonnull Recipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        powerScale.draw(guiGraphics, 5, 1);
    }

    @Override
    public List<Component> getTooltipStrings(@Nonnull Recipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if(mouseX >= 5 && mouseX <= 14 && mouseY >= 1 && mouseY <= 29){
            // NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
            return List.of(Component.translatable("tooltip.dysonsphere.energy_converter_energy", AssetUtil.FLOAT_FORMAT.format(EnergyConverterTile.CONVERSION_RATE)));
        }
        return List.of();
    }
    
    
    //drop lookup only works server-side. Understand this as surrender
    public static List<Recipe> getAllRecipes(){
        Recipe recipe = new Recipe(Ingredient.of(Tags.Items.RAW_MATERIALS), Ingredient.of(Tags.Items.ORES));
        return List.of(recipe);
    }


}
