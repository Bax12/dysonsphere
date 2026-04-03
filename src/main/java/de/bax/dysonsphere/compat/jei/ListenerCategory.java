package de.bax.dysonsphere.compat.jei;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.gui.ListenerGui;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.recipes.ListenerRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ListenerCategory implements IRecipeCategory<ListenerRecipe>{

    @Override
    public RecipeType<ListenerRecipe> getRecipeType() {
        return RecipeType.create(DysonSphere.MODID, "listener", ListenerRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("dysonsphere.recipe.listener");
    }

    @Override
    public IDrawable getBackground() {
        return DSJeiPlugin.guiHelper.drawableBuilder(ListenerGui.RES_LOC, 24, 19, 138, 64).build();
    }

    @Override
    public IDrawable getIcon() {
        return DSJeiPlugin.guiHelper.createDrawableItemStack(ModBlocks.LISTENER_BLOCK.get().asItem().getDefaultInstance());
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull ListenerRecipe recipe, @Nonnull IFocusGroup focuses) {
        int offsetX = 0, offsetY = 0;
        ItemStack result = recipe.internalRecipe().getResultItem(Minecraft.getInstance().level.registryAccess()).copyWithCount(1);
        for(var ing : recipe.internalRecipe().getIngredients()){
            if(ing.test(result)){
                ing = Ingredient.of(ModItems.UNIVERSE_WHISPER.get());
            }
            builder.addSlot(RecipeIngredientRole.INPUT, 6 + offsetX, 7 + offsetY).addIngredients(ing);
            offsetX += 18;
            if(offsetX > 36){
                offsetX = 0;
                offsetY += 18;
            }
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 82, 7).addItemStack(ModItems.UNIVERSE_WHISPER.get().getDefaultInstance()).addTooltipCallback((slotView, tooltip) -> {
            tooltip.add(Component.translatable("tooltip.dysonsphere.listener_shard"));
        });
        builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 24).addItemStack(result).addTooltipCallback((slotView, tooltip) -> {
            tooltip.add(Component.translatable("tooltip.dysonsphere.listener_result"));
        });;
        
    }
    
}
