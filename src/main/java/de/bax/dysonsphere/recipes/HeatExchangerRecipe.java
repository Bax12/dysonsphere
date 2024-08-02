package de.bax.dysonsphere.recipes;

import org.checkerframework.checker.units.qual.min;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

public record HeatExchangerRecipe(ResourceLocation id, FluidStack input, FluidStack output, double minHeat, double heatConsumption, float heatScaling, float scalingFactor) implements Recipe<RecipeWrapper> {

    @Override
    public boolean matches(RecipeWrapper pContainer, Level pLevel) {
        return false;
    }

    public boolean matches(FluidStack fluid){
        return input.isFluidEqual(fluid);
    }

    @Override
    public ItemStack assemble(RecipeWrapper pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.HEAT_EXCHANGER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.HEAT_EXCHANGER_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public FluidStack input() {
        return input.copy();
    }

    public FluidStack output() {
        return output.copy();
    }
    
    public static class Serializer implements RecipeSerializer<HeatExchangerRecipe> {

        @Override
        public HeatExchangerRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            FluidStack input = deserializeFluidStack(pSerializedRecipe.getAsJsonObject("input"));
            FluidStack output = deserializeFluidStack(pSerializedRecipe.getAsJsonObject("output"));
            double minHeat = pSerializedRecipe.get("minHeat").getAsDouble();
            double heatConsumption = pSerializedRecipe.get("heatConsumption").getAsDouble();
            float heatScaling = pSerializedRecipe.get("heatScaling").getAsFloat();
            float scalingFactor = pSerializedRecipe.get("scalingFactor").getAsFloat();


            return new HeatExchangerRecipe(pRecipeId, input, output, minHeat, heatConsumption, heatScaling, scalingFactor);
        }

        @Override
        public @Nullable HeatExchangerRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            FluidStack input = FluidStack.readFromPacket(pBuffer);
            FluidStack output = FluidStack.readFromPacket(pBuffer);
            double minHeat = pBuffer.readDouble();
            double heatConsumption = pBuffer.readDouble();
            float heatScaling = pBuffer.readFloat();
            float scalingFactor = pBuffer.readFloat();
            return new HeatExchangerRecipe(pRecipeId, input, output, minHeat, heatConsumption, heatScaling, scalingFactor);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, HeatExchangerRecipe pRecipe) {
            pRecipe.input().writeToPacket(pBuffer);
            pRecipe.output().writeToPacket(pBuffer);
            pBuffer.writeDouble(pRecipe.minHeat());
            pBuffer.writeDouble(pRecipe.heatConsumption());
            pBuffer.writeFloat(pRecipe.heatScaling());
            pBuffer.writeFloat(pRecipe.scalingFactor());
        }

        protected FluidStack deserializeFluidStack(JsonObject json){
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(json.get("fluid").getAsString()));
            int amount = json.get("amount").getAsInt();
            return new FluidStack(fluid, amount);
        }

    }

}
