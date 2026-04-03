package de.bax.dysonsphere.compat.jei;

import java.util.List;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.gui.BaseGui;
import de.bax.dysonsphere.recipes.CargoDeliveryRecipe;
import de.bax.dysonsphere.util.AssetUtil;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class CargoDeliveryCategory implements IRecipeCategory<CargoDeliveryRecipe> {
    
    protected IDrawableStatic powerScale;

    @Override
    public RecipeType<CargoDeliveryRecipe> getRecipeType() {
        return RecipeType.create(DysonSphere.MODID, "cargo_delivery", CargoDeliveryRecipe.class);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("dysonsphere.recipe.cargo_delivery");
    }

    @Override
    public IDrawable getBackground() {
        // return DSJeiPlugin.guiHelper.createBlankDrawable(80, 30);
        return DSJeiPlugin.guiHelper.drawableBuilder(BaseGui.GUI_INVENTORY_LOC, 57, 209, 18, 10).addPadding(10, 10, 32, 30).build();
    }

    @Override
    public IDrawable getIcon() {
        return DSJeiPlugin.guiHelper.createDrawableItemStack(ModBlocks.CARGO_RECEIVER_BLOCK.get().asItem().getDefaultInstance());
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull CargoDeliveryRecipe recipe, @Nonnull IFocusGroup focuses) {
        powerScale = DSJeiPlugin.guiHelper.drawableBuilder(BaseGui.GUI_INVENTORY_LOC, 43, 91, 9, 26).build();
        IDrawable overlay = DSJeiPlugin.guiHelper.drawableBuilder(BaseGui.GUI_INVENTORY_LOC, 0, 180, 12, 28).build();

        if(!recipe.fluidOutput().isEmpty()){
            IRecipeSlotBuilder fluidOutput = builder.addSlot(RecipeIngredientRole.OUTPUT, 65, 1).setFluidRenderer(500, false, 10, 26).setOverlay(overlay, -1, -1);
            fluidOutput.addFluidStack(recipe.fluidOutput().getFluid(), recipe.fluidOutput().getAmount());
        }
        if(!recipe.itemOutput().isEmpty()){
            builder.addSlot(RecipeIngredientRole.OUTPUT, 55, 10).addItemStack(recipe.itemOutput());
        }
        
    }

    @Override
    public void draw(@Nonnull CargoDeliveryRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, @Nonnull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        powerScale.draw(guiGraphics, 5, 2);
    }

    @Override
    public List<Component> getTooltipStrings(@Nonnull CargoDeliveryRecipe recipe, @Nonnull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if(mouseX >= 5 && mouseX <= 14 && mouseY >= 5 && mouseY <= 26){
            return List.of(Component.translatable("tooltip.dysonsphere.cargo_receiver_energy", AssetUtil.FLOAT_FORMAT.format(recipe.energy())));
        }
        return List.of();
    }
}
