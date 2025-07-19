package de.bax.dysonsphere.recipes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
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
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public record OrbitalLaunchRecipe(ResourceLocation id, Ingredient input, ItemStack launchStack, List<Ingredient> extraInputs, List<FluidStack> fluidInputs, int baseEnergy) implements Recipe<RecipeWrapper> {

    @Override
    public boolean matches(@Nonnull RecipeWrapper pContainer, @Nonnull Level pLevel) {
        return false;
    }

    @Override
    public ItemStack assemble(@Nonnull RecipeWrapper pContainer, @Nonnull RegistryAccess pRegistryAccess) {
        return launchStack;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(@Nonnull RegistryAccess pRegistryAccess) {
        return launchStack;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ORBITAL_LAUNCH_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ORBITAL_LAUNCH_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<OrbitalLaunchRecipe> {

		@Override
		public OrbitalLaunchRecipe fromJson(@Nonnull ResourceLocation pRecipeId, @Nonnull JsonObject pSerializedRecipe) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));
			ItemStack launchStack = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pSerializedRecipe, "launchStack"), true);
            int baseEnergy = pSerializedRecipe.get("baseEnergy").getAsInt();
            List<Ingredient> extraInputs = new ArrayList<Ingredient>();
            JsonArray extraJson = pSerializedRecipe.get("extraInputs").getAsJsonArray();
            if(extraJson != null){
                extraJson.forEach((element) -> {
                    extraInputs.add(Ingredient.fromJson(element));
                });
            }
            List<FluidStack> fluidInputs = new ArrayList<FluidStack>();
            JsonArray fluidJson = pSerializedRecipe.get("fluidInputs").getAsJsonArray();
            if(fluidJson != null){
                fluidJson.forEach((element) -> {
                    fluidInputs.add(SerializationUtil.deserializeFluidStack(element.getAsJsonObject()));
                });
            }
            return new OrbitalLaunchRecipe(pRecipeId, ingredient, launchStack, extraInputs, fluidInputs, baseEnergy);
		}

		@Override
		public @Nullable OrbitalLaunchRecipe fromNetwork(@Nonnull ResourceLocation pRecipeId, @Nonnull FriendlyByteBuf pBuffer) {
			Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            ItemStack launchStack = pBuffer.readItem();
            int baseEnergy = pBuffer.readInt();
            int extraCount = pBuffer.readInt();
            List<Ingredient> extraInputs = new ArrayList<Ingredient>();
            for (int i = 0; i < extraCount; i++) {
                extraInputs.add(Ingredient.fromNetwork(pBuffer));
            }
            int fluidCount = pBuffer.readInt();
            List<FluidStack> fluidInputs = new ArrayList<FluidStack>();
            for (int i = 0; i < fluidCount; i++) {
                fluidInputs.add(FluidStack.readFromPacket(pBuffer));
            }
            return new OrbitalLaunchRecipe(pRecipeId, ingredient, launchStack, extraInputs, fluidInputs, baseEnergy);
		}

		@Override
		public void toNetwork(@Nonnull FriendlyByteBuf pBuffer, @Nonnull OrbitalLaunchRecipe pRecipe) {
			pRecipe.input().toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.launchStack());
            pBuffer.writeInt(pRecipe.baseEnergy());
            pBuffer.writeInt(pRecipe.extraInputs().size());
            for(Ingredient extra : pRecipe.extraInputs()){
                extra.toNetwork(pBuffer);
            }
            pBuffer.writeInt(pRecipe.fluidInputs().size());
            for(FluidStack fluid : pRecipe.fluidInputs()){
                fluid.writeToPacket(pBuffer);
            }
		}

    }
}
