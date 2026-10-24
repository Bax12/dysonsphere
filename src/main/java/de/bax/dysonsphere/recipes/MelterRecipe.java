package de.bax.dysonsphere.recipes;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.util.SerializationUtil;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public record MelterRecipe(ResourceLocation id, Ingredient input, FluidStack output, double minHeat, double heatAbsorbed, int workTime) implements Recipe<RecipeWrapper> {

    @Override
    public boolean matches(@Nonnull RecipeWrapper pContainer, @Nonnull Level pLevel) {
        return false;
    }

    public boolean matches(ItemStack inputStack){
        return input().test(inputStack);
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

    public FluidStack output(){
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MELTER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MELTER_TYPE.get();
    }
    
    public static class Serializer implements RecipeSerializer<MelterRecipe>  {

        @Override
        public MelterRecipe fromJson(@Nonnull ResourceLocation pRecipeId, @Nonnull JsonObject pSerializedRecipe) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));
            FluidStack output = SerializationUtil.deserializeFluidStack(pSerializedRecipe.getAsJsonObject("output"));
            double minHeat = pSerializedRecipe.get("minHeat").getAsDouble();
            double heatAbsorbed = pSerializedRecipe.get("heatAbsorbed").getAsDouble();
            int workTime = pSerializedRecipe.get("workTime").getAsInt();
            return new MelterRecipe(pRecipeId, ingredient, output, minHeat, heatAbsorbed, workTime);
        }

        @Override
        public @Nullable MelterRecipe fromNetwork(@Nonnull ResourceLocation pRecipeId, @Nonnull FriendlyByteBuf pBuffer) {
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            FluidStack output = pBuffer.readFluidStack();
            double minHeat = pBuffer.readDouble();
            double heatAbsorbed = pBuffer.readDouble();
            int workTime = pBuffer.readInt();
            return new MelterRecipe(pRecipeId, ingredient, output, minHeat, heatAbsorbed, workTime);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf pBuffer, @Nonnull MelterRecipe pRecipe) {
            pRecipe.input().toNetwork(pBuffer);
            pBuffer.writeFluidStack(pRecipe.output());
            pBuffer.writeDouble(pRecipe.minHeat());
            pBuffer.writeDouble(pRecipe.heatAbsorbed());
            pBuffer.writeInt(pRecipe.workTime());
        }

    }

}
