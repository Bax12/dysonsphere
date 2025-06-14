package de.bax.dysonsphere.recipes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public record LaserCraftingRecipe(ResourceLocation id, Ingredient input, List<Ingredient> extraInputs, ItemStack output, long inputEnergy) implements Recipe<RecipeWrapper> {

    @Override
    public boolean matches(@Nonnull RecipeWrapper pContainer, @Nonnull Level pLevel) {
        if(this.input.test(pContainer.getItem(0))){
            List<ItemStack> extraStacks = new ArrayList<>();
            for(int i = 1; i < pContainer.getContainerSize(); i++){
                extraStacks.add(pContainer.getItem(i));
            }
            return matches(pContainer.getItem(0), extraStacks);
        }
        return false;
    }

    // public boolean matches(Ingredient input, List<Ingredient> extraInputs){
    //     return this.input.equals(input) && extraInputs.containsAll(this.extraInputs) && this.extraInputs.containsAll(extraInputs);
    // }

    public boolean matches(ItemStack input, List<ItemStack> extraInputs){
        if(this.input.test(input)){
            // for (Ingredient extraIngredient : this.extraInputs) {
            //     if(!extraInputs.stream().anyMatch(extraIngredient)){
            //         return false;
            //     }
            // }
            extraInputs = new ArrayList<>(extraInputs); //ensure list is mutable
            for (Ingredient extraIngredient : this.extraInputs) {
                // if(extraStacks.stream().noneMatch(extraIngredient)){
                //     return false;
                // }
                ItemStack stack = extraInputs.stream().filter(extraIngredient).findFirst().orElse(ItemStack.EMPTY);
                if(stack.isEmpty()){
                    return false; //nothing matches the checked ingredient
                }
                extraInputs.remove(stack); //remove the match, so a single item cannot full fill multiple ingredients
            }
            return true;
        }
        return false;
        // return matches(Ingredient.of(input), extraInputs.stream().map(Ingredient::of).toList());
    }

    @Override
    public ItemStack assemble(@Nonnull RecipeWrapper pContainer, @Nonnull RegistryAccess pRegistryAccess) {
        return getResultItem(pRegistryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(@Nonnull RegistryAccess pRegistryAccess) {
        return output();
    }

    @Override
    public ResourceLocation getId() {
        return id();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.LASER_CRAFTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.LASER_CRAFTING_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public List<Ingredient> extraInputs(){
        return ImmutableList.copyOf(extraInputs);
    }

    public ItemStack output(){
        return output.copy();
    }

    public static class Serializer implements RecipeSerializer<LaserCraftingRecipe> {

        @Override
        public LaserCraftingRecipe fromJson(@Nonnull ResourceLocation pRecipeId, @Nonnull JsonObject pSerializedRecipe) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));
			ItemStack output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"), true);
            long inputEnergy = pSerializedRecipe.get("inputEnergy").getAsLong();
            List<Ingredient> extraInputs = new ArrayList<Ingredient>();
            JsonArray extraJson = pSerializedRecipe.get("extraInputs").getAsJsonArray();
            if(extraJson != null){
                extraJson.forEach((element) -> {
                    extraInputs.add(Ingredient.fromJson(element));
                });
            }
            return new LaserCraftingRecipe(pRecipeId, ingredient, extraInputs, output, inputEnergy);
        }

        @Override
        public @Nullable LaserCraftingRecipe fromNetwork(@Nonnull ResourceLocation pRecipeId, @Nonnull FriendlyByteBuf pBuffer) {
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            ItemStack output = pBuffer.readItem();
            long inputEnergy = pBuffer.readLong();
            int extraCount = pBuffer.readInt();
            List<Ingredient> extraInputs = new ArrayList<Ingredient>();
            for(int i = 0; i < extraCount; i++){
                extraInputs.add(Ingredient.fromNetwork(pBuffer));
            }
            return new LaserCraftingRecipe(pRecipeId, ingredient, extraInputs, output, inputEnergy);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf pBuffer, @Nonnull LaserCraftingRecipe pRecipe) {
            pRecipe.input().toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.output);
            pBuffer.writeLong(pRecipe.inputEnergy());
            pBuffer.writeInt(pRecipe.extraInputs.size());
            for(Ingredient extra : pRecipe.extraInputs){
                extra.toNetwork(pBuffer);
            }
        }

    }
    
}
