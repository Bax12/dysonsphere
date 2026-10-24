package de.bax.dysonsphere.recipes;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.schematic.ISchematicElement;
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

public record SchematicBaseInputRecipe(ResourceLocation id, Ingredient input, ISchematicElement baseElement) implements Recipe<RecipeWrapper> {

    @Override
    public boolean matches(@Nonnull RecipeWrapper pContainer, @Nonnull Level pLevel) {
        return false;
    }

    public boolean matches(ISchematicElement element){
        return element.equals(baseElement());
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
        return ModRecipes.SCHEMATIC_INPUT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SCHEMATIC_INPUT_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<SchematicBaseInputRecipe> {

        @Override
        public SchematicBaseInputRecipe fromJson(@Nonnull ResourceLocation pRecipeId, @Nonnull JsonObject pSerializedRecipe) {
            Ingredient input = Ingredient.fromJson(pSerializedRecipe.get("input"));
            ISchematicElement element = ISchematicElement.fromJson(pSerializedRecipe.getAsJsonObject("element"));
            return new SchematicBaseInputRecipe(pRecipeId, input, element);
        }

        @Override
        public @Nullable SchematicBaseInputRecipe fromNetwork(@Nonnull ResourceLocation pRecipeId, @Nonnull FriendlyByteBuf pBuffer) {
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            ISchematicElement element = ISchematicElement.readFromPacket(pBuffer);
            return new SchematicBaseInputRecipe(pRecipeId, input, element);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf pBuffer, @Nonnull SchematicBaseInputRecipe pRecipe) {
            pRecipe.input.toNetwork(pBuffer);
            pRecipe.baseElement.writeToPacket(pBuffer);
        }

    }
    
}
