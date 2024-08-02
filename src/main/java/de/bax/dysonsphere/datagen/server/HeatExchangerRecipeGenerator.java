package de.bax.dysonsphere.datagen.server;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.fluids.ModFluids;
import de.bax.dysonsphere.recipes.ModRecipes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class HeatExchangerRecipeGenerator {
    
    public static final String basePath = "heat_exchanging";

    public static void buildRecipes(Consumer<FinishedRecipe> pWriter){
        RecipeBuilder.of(new FluidStack(ModFluids.STEAM.get(), 50)).input(new FluidStack(Fluids.WATER, 5)).minHeat(450).heatConsumption(0.05d).heatScaling(50).scalingFactor(0.2f).save(pWriter);
    }



    public static class RecipeBuilder {
        private FluidStack input;
        private FluidStack output;
        private double minHeat;
        private double heatConsumption;
        private float heatScaling = 0;
        private float scalingFactor = 0;

        private RecipeBuilder(FluidStack output){
            this.output = output;
        }
        /**
        * @param output the fluidStack that gets produced every 5 working ticks. The amount is used as baseAmount for scaling
        */
        public static RecipeBuilder of(FluidStack output){
            return new RecipeBuilder(output);
        }

        /**
         * @param input the fluidStack that get consumed every 5 working ticks.
         */
        public RecipeBuilder input(FluidStack input){
            this.input = input;
            return this;
        }

        /**
         * @param minHeat the minimum heat required to start working on the recipe
         */
        public RecipeBuilder minHeat(double minHeat){
            this.minHeat = minHeat;
            return this;
        }

        /**
         * @param heatConsumption the heat consumed per generated mB of output when the recipe is crafted
         * should be less the minHeat / output.amount
         */
        public RecipeBuilder heatConsumption(double heatConsumption){
            this.heatConsumption = heatConsumption;
            return this;
        }

        /**
         * @param heatScaling the amount of heat required above minHeat per added scalingFactor
         * every heatScaling heat above minHeat increases the output amount by baseAmount * scalingFactor
         */
        public RecipeBuilder heatScaling(float heatScaling){
            this.heatScaling = heatScaling;
            return this;
        }

        /**
         * @param scalingFactor multiplier applied to the baseAmount when enough heat is available
         * every heatScaling heat above minHeat increases the output amount by baseAmount * scalingFactor
         */
        public RecipeBuilder scalingFactor(float scalingFactor){
            this.scalingFactor = scalingFactor;
            return this;
        }

        public void save(Consumer<FinishedRecipe> consumer){
            this.save(consumer, ForgeRegistries.FLUIDS.getKey(output.getFluid()));
        }

        public void save(Consumer<FinishedRecipe> consumer, ResourceLocation location){
            if(input == null || output == null) throw new NullPointerException("Recipe " + location + " tried to save a null recipe");
            consumer.accept(new Recipe(getLocation(location), input, output, minHeat, heatConsumption, heatScaling, scalingFactor));
        }


        public ResourceLocation getLocation(ResourceLocation location){
            return getLocation(location.getPath());
        }

        public ResourceLocation getLocation(String path){
            return new ResourceLocation(DysonSphere.MODID, basePath + "/" + path);
        }
    }

    public static record  Recipe(ResourceLocation id, FluidStack input, FluidStack output, double minHeat, double heatConsumption, float heatScaling, float scalingFactor) implements FinishedRecipe {

        @Override
        public void serializeRecipeData(@Nonnull JsonObject pJson) {
            pJson.add("input", serializeFluidStack(input));
            pJson.add("output", serializeFluidStack(output));
            pJson.addProperty("minHeat", minHeat);
            pJson.addProperty("heatConsumption", heatConsumption);
            pJson.addProperty("heatScaling", heatScaling);
            pJson.addProperty("scalingFactor", scalingFactor);
        }

        @Override
        public ResourceLocation getId() {
            return id();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.HEAT_EXCHANGER_SERIALIZER.get();
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

        private static JsonObject serializeFluidStack(FluidStack stack){
            JsonObject json = new JsonObject();

            json.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
            json.addProperty("amount", stack.getAmount());
            return json;
        }

    }

}
