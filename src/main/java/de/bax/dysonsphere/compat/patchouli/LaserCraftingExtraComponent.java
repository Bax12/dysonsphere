package de.bax.dysonsphere.compat.patchouli;

import java.util.function.UnaryOperator;

import de.bax.dysonsphere.recipes.LaserCraftingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

public class LaserCraftingExtraComponent implements ICustomComponent {

    private transient int x;
    private transient int y;
    private transient LaserCraftingRecipe recipe;
    public String lookup = "#recipe";

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
        String recipeName = lookup.apply(IVariable.wrap(this.lookup)).asString();
        if(!recipeName.isBlank()){
            recipe = (LaserCraftingRecipe) Minecraft.getInstance().level.getRecipeManager().byKey(new ResourceLocation(recipeName)).orElseThrow(IllegalArgumentException::new);
        }
    }

    @Override
    public void build(int componentX, int componentY, int pageNum) {
        x = componentX;
        y = componentY;
    }

    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        int extraCount = recipe.extraInputs().size();
        float radius = 20f;
        double rads = Math.PI * 2 / (extraCount + 1);
        for(int i = 0; i < extraCount; i++){
            int posX = (int) (Math.sin(rads * (i+1) + Math.PI) * radius + x); //without Math.PI offset the laser slot is on the bottom instead.
            int posY = (int) (Math.cos(rads * (i+1) + Math.PI) * radius + y);
            context.renderIngredient(graphics, posX, posY, mouseX, mouseY, recipe.extraInputs().get(i));
        }
        
        
    }
    

}
