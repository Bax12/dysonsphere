package de.bax.dysonsphere.compat.jei;

import java.util.List;

import javax.annotation.Nonnull;

import org.joml.Math;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.gui.BaseGui;
import de.bax.dysonsphere.recipes.LaserCraftingRecipe;
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
        return DSJeiPlugin.guiHelper.drawableBuilder(BaseGui.GUI_INVENTORY_LOC, 12, 175, 111, 78).build();
    }

    @Override
    public IDrawable getIcon() {
        return DSJeiPlugin.guiHelper.createDrawableItemStack(ModBlocks.LASER_CRAFTER_BLOCK.get().asItem().getDefaultInstance());
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull LaserCraftingRecipe recipe, @Nonnull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 23, 37).addIngredients(recipe.input());
        int extraCount = recipe.extraInputs().size();
        float radius = 20f;
        double rads = Math.PI * 2 / (extraCount + 1); //always leave above input slot empty. Thats where the laser lives.
        for(int i = 0; i < extraCount; i++){
            int posX = (int) (Math.sin(rads * (i+1) + Math.PI) * radius + 23); //without Math.PI offset the laser slot is on the bottom instead.
            int posY = (int) (Math.cos(rads * (i+1) + Math.PI) * radius + 37);
            builder.addSlot(RecipeIngredientRole.INPUT, posX, posY).addIngredients(recipe.extraInputs().get(i));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 68, 31).addItemStack(recipe.output());
    }

    @Override
    public List<Component> getTooltipStrings(@Nonnull LaserCraftingRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if(mouseX >= 23 && mouseX <= 39 && mouseY >= 12 && mouseY <= 27){
            return List.of(Component.translatable("tooltip.dysonsphere.laser_crafter_energy_needed", AssetUtil.FLOAT_FORMAT.format(recipe.inputEnergy())));
        }
        
        return List.of();
    }
    
}
