package de.bax.dysonsphere.recipes;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.util.FluidIngredient;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public record CrystallizerRecipe(ResourceLocation id, FluidIngredient input, ItemStack output, double maxHeat, double heatCapacity) implements Recipe<RecipeWrapper> {

    @Override
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        return false;
    }

    public boolean matches(FluidStack inputFluid){
        return input.test(inputFluid);
    }

    @Override
    public ItemStack assemble(RecipeWrapper pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output();
    }

    @Override
    public ResourceLocation getId() {
        return id();
    }

    public FluidIngredient input(){
        return input;
    }

    public ItemStack output(){
        return output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CRYSTALLIZER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CRYSTALLIZER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CrystallizerRecipe> {

        @Override
        public CrystallizerRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            FluidIngredient input = FluidIngredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));
            ItemStack output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"), false);
            double maxHeat = pSerializedRecipe.get("maxHeat").getAsDouble();
            double heatCapacity = pSerializedRecipe.get("heatCapacity").getAsDouble();
            return new CrystallizerRecipe(pRecipeId, input, output, maxHeat, heatCapacity);
        }

        @Override
        public @Nullable CrystallizerRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            ItemStack output = pBuffer.readItem();
            FluidIngredient input = FluidIngredient.readFromPacket(pBuffer);
            double maxHeat = pBuffer.readDouble();
            double heatCapacity = pBuffer.readDouble();
            return new CrystallizerRecipe(pRecipeId, input, output, maxHeat, heatCapacity);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, CrystallizerRecipe pRecipe) {
            pBuffer.writeItem(pRecipe.output());
            pRecipe.input().writeToPacket(pBuffer);
            pBuffer.writeDouble(pRecipe.maxHeat());
            pBuffer.writeDouble(pRecipe.heatCapacity());
        }

    }
    
}
