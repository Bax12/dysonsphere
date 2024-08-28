package de.bax.dysonsphere.compat.patchouli;

import de.bax.dysonsphere.recipes.LaserCraftingRecipe;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class LaserCraftingRecipeProcessor implements IComponentProcessor {

    private LaserCraftingRecipe recipe;
    private LaserCraftingRecipe recipe2;
    private String text;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        String recipeId = variables.get("recipe").asString();
        recipe = (LaserCraftingRecipe) level.getRecipeManager().byKey(new ResourceLocation(recipeId)).orElseThrow(IllegalArgumentException::new);

        if(variables.has("recipe2")){
            String recipeId2 = variables.get("recipe2").asString();
            recipe2 = (LaserCraftingRecipe) level.getRecipeManager().byKey(new ResourceLocation(recipeId2)).orElseThrow(IllegalArgumentException::new);
        }
        if(variables.has("text")){
            text = variables.get("text").asString();
        }
    }

    @Override
    public IVariable process(Level level, String key) {
        if(key.equals("input")){
            return IVariable.from(recipe.input().getItems()[0]);
        }
        if(key.equals("output")){
            return IVariable.from(recipe.output());
        }
        if(key.equals("energy")){
            return IVariable.wrap(AssetUtil.FLOAT_FORMAT.format(recipe.inputEnergy()));
        }
        boolean hasRecipe2 = recipe2 != null;
        if(key.equals("hasRecipe2")){
            return IVariable.wrap(hasRecipe2);
        }
        if(hasRecipe2){
            if(key.equals("input2")){
                return IVariable.from(recipe2.input().getItems()[0]);
            }
            if(key.equals("output2")){
                return IVariable.from(recipe2.output());
            }
            if(key.equals("energy2")){
                return IVariable.wrap(AssetUtil.FLOAT_FORMAT.format(recipe2.inputEnergy()));
            }
        } else {
            if(key.equals("text")){
                return IVariable.wrap(text);
            }
        }

        return null;
    }
    
}
