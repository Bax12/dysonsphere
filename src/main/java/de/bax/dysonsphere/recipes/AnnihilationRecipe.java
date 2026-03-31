package de.bax.dysonsphere.recipes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.util.FluidIngredient;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public record AnnihilationRecipe(ResourceLocation id, long energy, FluidIngredient fluidMatter, FluidIngredient fluidAnti) implements Recipe<RecipeWrapper> {

    @Override
    public boolean matches(@Nonnull RecipeWrapper pContainer, @Nonnull Level pLevel) {
        return false;
    }

    public boolean matches(FluidStack fluidMatter, FluidStack fluidAnti){
        return fluidMatter().test(fluidMatter) && fluidAnti().test(fluidAnti);
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
        return ModRecipes.ANNIHILATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ANNIHILATION_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<AnnihilationRecipe> {

        @Override
        public AnnihilationRecipe fromJson(@Nonnull ResourceLocation pRecipeId, @Nonnull JsonObject pSerializedRecipe) {
            long energy = pSerializedRecipe.get("producedEnergy").getAsLong();
            FluidIngredient fluidMatter = FluidIngredient.fromJson(pSerializedRecipe.getAsJsonObject("fluidMatter"));
            FluidIngredient fluidAnti = FluidIngredient.fromJson(pSerializedRecipe.getAsJsonObject("fluidAnti"));
            return new AnnihilationRecipe(pRecipeId, energy, fluidMatter, fluidAnti);
        }

        @Override
        public @Nullable AnnihilationRecipe fromNetwork(@Nonnull ResourceLocation pRecipeId, @Nonnull FriendlyByteBuf pBuffer) {
            long energy = pBuffer.readLong();
            FluidIngredient fluidMatter = FluidIngredient.readFromPacket(pBuffer);
            FluidIngredient fluidAnti = FluidIngredient.readFromPacket(pBuffer);
            return new AnnihilationRecipe(pRecipeId, energy, fluidMatter, fluidAnti);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf pBuffer, @Nonnull AnnihilationRecipe pRecipe) {
            pBuffer.writeLong(pRecipe.energy());
            pRecipe.fluidMatter.writeToPacket(pBuffer);
            pRecipe.fluidAnti.writeToPacket(pBuffer);
        }

    }

    protected static List<FluidIngredient> matterInputs;
    protected static List<FluidIngredient> antiInputs;

    public static List<FluidIngredient> getMatterInputFluids(){
        if(matterInputs == null){
            return List.of();
        }

        return matterInputs;
    }

    public static List<FluidIngredient> getAntiInputFluids(){
        if(antiInputs == null){
            return List.of();
        }

        return antiInputs;
    }

    //we have no level in the item method that needs the lists. Let's hope this always gets called first.
    public static void generateInputLists(Level level){
        matterInputs = new ArrayList<>();
        antiInputs = new ArrayList<>();

        level.getRecipeManager().getAllRecipesFor(ModRecipes.ANNIHILATION_TYPE.get()).forEach((recipe) -> {
            matterInputs.add(recipe.fluidMatter());
            antiInputs.add(recipe.fluidAnti());
        });
    }


    
}
