package de.bax.dysonsphere.recipes;

import org.jetbrains.annotations.Nullable;

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

public record LaserCraftingRecipe(ResourceLocation id, Ingredient input, ItemStack output, long inputEnergy) implements Recipe<RecipeWrapper> {

    @Override
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        return input().test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeWrapper pContainer, RegistryAccess pRegistryAccess) {
        return getResultItem(pRegistryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
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

    public ItemStack output(){
        return output.copy();
    }

    public static class Serializer implements RecipeSerializer<LaserCraftingRecipe> {

        @Override
        public LaserCraftingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));
			ItemStack output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"), true);
            long inputEnergy = pSerializedRecipe.get("inputEnergy").getAsLong();
            return new LaserCraftingRecipe(pRecipeId, ingredient, output, inputEnergy);
        }

        @Override
        public @Nullable LaserCraftingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            ItemStack output = pBuffer.readItem();
            long inputEnergy = pBuffer.readLong();
            return new LaserCraftingRecipe(pRecipeId, ingredient, output, inputEnergy);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, LaserCraftingRecipe pRecipe) {
            pRecipe.input().toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.output);
            pBuffer.writeLong(pRecipe.inputEnergy());
        }

    }
    
}
