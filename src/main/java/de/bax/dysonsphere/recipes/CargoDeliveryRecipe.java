package de.bax.dysonsphere.recipes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.bax.dysonsphere.capabilities.dysonSphere.IDysonSphereContainer;
import de.bax.dysonsphere.constructs.Construct;
import de.bax.dysonsphere.util.SerializationUtil;
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

public record CargoDeliveryRecipe(ResourceLocation id, int energy, FluidStack fluidOutput, ItemStack itemOutput, List<Construct> requiredConstructs) implements Recipe<RecipeWrapper> {

    public boolean canWork(IDysonSphereContainer dysonSphere){
        return dysonSphere.getEnabledConstructs().stream().filter((con) -> {return con.canWork();}).toList().containsAll(requiredConstructs());
    }

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
        return itemOutput();
    }

    @Override
    public ResourceLocation getId() {
        return id();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CARGO_DELIVERY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CARGO_DELIVERY_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CargoDeliveryRecipe> {

        @Override
        public CargoDeliveryRecipe fromJson(@Nonnull ResourceLocation pRecipeId, @Nonnull JsonObject pSerializedRecipe) {
            int energy = pSerializedRecipe.get("energy").getAsInt();
            FluidStack fluidOutput = pSerializedRecipe.has("fluidOutput") ? SerializationUtil.deserializeFluidStack(pSerializedRecipe.getAsJsonObject("fluidOutput")) : FluidStack.EMPTY;
            ItemStack itemOutput = pSerializedRecipe.has("itemOutput") ? CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pSerializedRecipe, "itemOutput"), true) : ItemStack.EMPTY;
            List<Construct> constructs = new ArrayList<>();
            JsonArray conJson = pSerializedRecipe.get("requiredConstructs").getAsJsonArray();
            if(conJson != null){
                conJson.forEach((element) -> {
                    constructs.add(Construct.fromJson(element.getAsJsonObject()));
                });
            }
            return new CargoDeliveryRecipe(pRecipeId, energy, fluidOutput, itemOutput, constructs);
        }

        @Override
        public @Nullable CargoDeliveryRecipe fromNetwork(@Nonnull ResourceLocation pRecipeId, @Nonnull FriendlyByteBuf pBuffer) {
            int energy = pBuffer.readInt();
            FluidStack fluidOutput = FluidStack.readFromPacket(pBuffer);
            ItemStack itemOutput = pBuffer.readItem();
            int conCount = pBuffer.readInt();
            List<Construct> constructs = new ArrayList<>();
            for(int i = 0; i < conCount; i++){
                constructs.add(Construct.readFromPacket(pBuffer));
            }
            return new CargoDeliveryRecipe(pRecipeId, energy, fluidOutput, itemOutput, constructs);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf pBuffer, @Nonnull CargoDeliveryRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.energy());
            pRecipe.fluidOutput().writeToPacket(pBuffer);
            pBuffer.writeItem(pRecipe.itemOutput());
            pBuffer.writeInt(pRecipe.requiredConstructs().size());
            pRecipe.requiredConstructs().forEach((con) -> {
                con.writeToPacket(pBuffer);
            });
        }

    }
    
}
