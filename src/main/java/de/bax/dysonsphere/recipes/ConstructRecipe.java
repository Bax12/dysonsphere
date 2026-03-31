package de.bax.dysonsphere.recipes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.bax.dysonsphere.constructs.Construct;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public record ConstructRecipe(ResourceLocation id, List<Ingredient> inputs, Construct output) implements Recipe<RecipeWrapper> {

    @Override
    public boolean matches(@Nonnull RecipeWrapper pContainer, @Nonnull Level pLevel) {
        return false;
    }

    @Override
    public ItemStack assemble(@Nonnull RecipeWrapper pContainer, @Nonnull RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(@Nonnull RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CONSTRUCT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CONSTRUCT_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
    

    public static class Serializer implements RecipeSerializer<ConstructRecipe> {

        @Override
        public ConstructRecipe fromJson(@Nonnull ResourceLocation pRecipeId, @Nonnull JsonObject pSerializedRecipe) {
            List<Ingredient> inputs = new ArrayList<Ingredient>();
            JsonArray inputJson = pSerializedRecipe.get("inputs").getAsJsonArray();
            if(inputJson != null){
                inputJson.forEach((element) -> {
                    inputs.add(Ingredient.fromJson(element));
                });
            }
            Construct output = Construct.fromJson(pSerializedRecipe.getAsJsonObject("construct"));
            return new ConstructRecipe(pRecipeId, inputs, output);
        }

        @Override
        public @Nullable ConstructRecipe fromNetwork(@Nonnull ResourceLocation pRecipeId, @Nonnull FriendlyByteBuf pBuffer) {
            int inputCount = pBuffer.readInt();
            List<Ingredient> inputs = new ArrayList<Ingredient>();
            for(int i = 0; i < inputCount; i++){
                inputs.add(Ingredient.fromNetwork(pBuffer));
            }
            Construct output = Construct.readFromPacket(pBuffer);
            return new ConstructRecipe(pRecipeId, inputs, output);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf pBuffer, @Nonnull ConstructRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.inputs().size());
            for(Ingredient input : pRecipe.inputs()){
                input.toNetwork(pBuffer);
            }
            pRecipe.output().writeToPacket(pBuffer);
        }

    }
}
