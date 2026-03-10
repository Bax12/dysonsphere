package de.bax.dysonsphere.recipes;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.tileentities.ListenerTile;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public record ListenerRecipe(ResourceLocation id, ShapedRecipe internalRecipe) implements Recipe<RecipeWrapper> {

    @Override
    public boolean matches(@Nonnull RecipeWrapper pContainer, @Nonnull Level pLevel) {
        NonNullList<Ingredient> ingredients = internalRecipe().getIngredients();
        int shardSlot = -1;
        for(int i = ListenerTile.slotGridStart; i <= ListenerTile.slotGridEnd; i++){
            if(!ingredients.get(i).test(pContainer.getItem(i))){
                if(pContainer.getItem(i).is(ModItems.UNIVERSE_WHISPER.get())){
                    if(shardSlot != -1) return false; //more then one shard in recipe
                    shardSlot = i;
                } else {
                    return false; //wrong recipe
                }
            }
        }
        return (shardSlot != -1); //no shard in recipe or valid recipe
    }

    @Override
    public ItemStack assemble(@Nonnull RecipeWrapper pContainer, @Nonnull RegistryAccess pRegistryAccess) {
        return getResultItem(pRegistryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return internalRecipe().canCraftInDimensions(pWidth, pHeight);
    }

    @Override
    public ItemStack getResultItem(@Nonnull RegistryAccess pRegistryAccess) {
        return internalRecipe().getResultItem(pRegistryAccess);
    }

    @Override
    public ResourceLocation getId() {
        return id();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.LISTENER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.LISTENER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ListenerRecipe> {

        @Override
        public ListenerRecipe fromJson(@Nonnull ResourceLocation pRecipeId, @Nonnull JsonObject pSerializedRecipe) {
            return null;
        }

        @Override
        public @Nullable ListenerRecipe fromNetwork(@Nonnull ResourceLocation pRecipeId, @Nonnull FriendlyByteBuf pBuffer) {
            return new ListenerRecipe(pRecipeId, RecipeSerializer.SHAPED_RECIPE.fromNetwork(pBuffer.readResourceLocation(), pBuffer));
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf pBuffer, @Nonnull ListenerRecipe pRecipe) {
            pBuffer.writeResourceLocation(pRecipe.internalRecipe().getId());
            RecipeSerializer.SHAPED_RECIPE.toNetwork(pBuffer, pRecipe.internalRecipe());
        }

    }
    
}
