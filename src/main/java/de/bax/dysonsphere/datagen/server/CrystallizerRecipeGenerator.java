package de.bax.dysonsphere.datagen.server;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.fluids.ModFluids;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.recipes.ModRecipes;
import de.bax.dysonsphere.util.FluidIngredient;
import de.bax.dysonsphere.util.SerializationUtil;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public class CrystallizerRecipeGenerator {
    
    public static final String basePath = "crystallizing";

    public static void buildRecipes(Consumer<FinishedRecipe> pWriter){
        RecipeBuilder.of(ModItems.SILICON_BOULE.get()).input(ModFluids.MOLTEN_SILICON.get(), 4000).maxHeat(700).heatCapacity(5000).save(pWriter);
    }

    public static class RecipeBuilder {
        private FluidIngredient input;
        private ItemStack output;
        private double maxHeat;
        private double heatCapacity;

        private RecipeBuilder(ItemStack output){
            this.output = output;
        }

        public static RecipeBuilder of(ItemStack output){
            return new RecipeBuilder(output);
        }

        public static RecipeBuilder of(Item output){
            return new RecipeBuilder(new ItemStack(output));
        }

        public static RecipeBuilder of(Item output, int amount){
            return new RecipeBuilder(new ItemStack(output, amount));
        }

        public RecipeBuilder input(FluidIngredient input){
            this.input = input;
            return this;
        }

        public RecipeBuilder input(Fluid input, int amount){
            this.input = FluidIngredient.of(input, amount);
            return this;
        }

        public RecipeBuilder maxHeat(double maxHeat){
            this.maxHeat = maxHeat;
            return this;
        }

        public RecipeBuilder heatCapacity(double heatCapacity){
            this.heatCapacity = heatCapacity;
            return this;
        }

        public void save(Consumer<FinishedRecipe> consumer){
            this.save(consumer, ForgeRegistries.ITEMS.getKey(output.getItem()));
        }

        public void save(Consumer<FinishedRecipe> consumer, ResourceLocation location){
            consumer.accept(new Recipe(getLocation(location), input, output, maxHeat, heatCapacity));
        }

        public static ResourceLocation getLocation(ResourceLocation location){
            return getLocation(location.getPath());
        }

        public static ResourceLocation getLocation(String path){
            return new ResourceLocation(DysonSphere.MODID, basePath + "/" + path);
        }
    }

    public static record Recipe(ResourceLocation id, FluidIngredient input, ItemStack output, double maxHeat, double heatCapacity) implements FinishedRecipe{

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            pJson.add("input", input.toJson());
            pJson.add("output", SerializationUtil.serializeItemStack(output));
            pJson.addProperty("maxHeat", maxHeat);
            pJson.addProperty("heatCapacity", heatCapacity);
        }

        @Override
        public ResourceLocation getId() {
            return id();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.CRYSTALLIZER_SERIALIZER.get();
        }

        @Override
        @Nullable
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        @Nullable
        public ResourceLocation getAdvancementId() {
            return null;
        }

    }

}
