package de.bax.dysonsphere.compat.jei;

import java.util.List;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.gui.BaseGui;
import de.bax.dysonsphere.gui.RailgunGui;
import de.bax.dysonsphere.recipes.LaserCraftingRecipe;
import de.bax.dysonsphere.tileentities.RailgunTile;
import de.bax.dysonsphere.util.AssetUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;

public class LaserCrafterCategory implements IRecipeCategory<LaserCraftingRecipe> {

    @Override
    public RecipeType<LaserCraftingRecipe> getRecipeType() {
        return RecipeType.create(DysonSphere.MODID, "laser_crafting", LaserCraftingRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("dysonsphere.recipe.laser");
    }

    @Override
    public IDrawable getBackground() {
        return DSJeiPlugin.guiHelper.drawableBuilder(BaseGui.GUI_INVENTORY_LOC, 15, 180, 65, 28).build();
    }

    @Override
    public IDrawable getIcon() {
        return DSJeiPlugin.guiHelper.createDrawableItemStack(ModBlocks.LASER_CRAFTER_BLOCK.get().asItem().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LaserCraftingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 0, 12).addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 45, 6).addItemStack(recipe.output());
    }

    @Override
    public List<Component> getTooltipStrings(LaserCraftingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if(mouseX >= 0 && mouseX <= 16 && mouseY >= 0 && mouseY <= 12){
            return List.of(Component.translatable("tooltip.dysonsphere.laser_crafter_energy_needed", AssetUtil.FLOAT_FORMAT.format(recipe.inputEnergy())));
        }
        
        return List.of();
    }
    
}
