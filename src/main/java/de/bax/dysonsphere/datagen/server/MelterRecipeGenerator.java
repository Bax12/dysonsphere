package de.bax.dysonsphere.datagen.server;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.fluids.ModFluids;
import de.bax.dysonsphere.recipes.ModRecipes;
import de.bax.dysonsphere.tags.DSTags;
import de.bax.dysonsphere.util.SerializationUtil;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class MelterRecipeGenerator {
    
    public static final String basePath = "melting";

    public static void buildRecipes(Consumer<FinishedRecipe> pWriter){
        RecipeBuilder.of(ModFluids.MOLTEN_SILICON.get(), 250).input(Ingredient.of(DSTags.itemSilicon)).minHeat(1475).heatAbsorbed(25).workTime(100).save(pWriter);
        RecipeBuilder.of(ModFluids.MOLTEN_SILICON.get(), 4000).input(Ingredient.of(DSTags.itemSiliconBoule)).minHeat(1475).heatAbsorbed(25).workTime(1600).save(pWriter, "molten_silicon_from_boule");
        RecipeBuilder.of(ModFluids.MOLTEN_SILICON.get(), 50).input(Ingredient.of(DSTags.itemSiliconWafer)).minHeat(1475).heatAbsorbed(25).workTime(20).save(pWriter, "molten_silicon_from_waver");
        RecipeBuilder.of(Fluids.WATER, 250).input(Ingredient.of(Blocks.ICE)).minHeat(250).heatAbsorbed(50).workTime(20).save(pWriter);
    }

    public static class RecipeBuilder {

        private Ingredient input;
        private FluidStack output;
        private double minHeat;
        private double heatAbsorbed;
        private int workTime;

        private RecipeBuilder(FluidStack output){
            this.output = output;
        }

        public static RecipeBuilder of(FluidStack output){
            return new RecipeBuilder(output);
        }

        public static RecipeBuilder of(Fluid fluid, int amount){
            return new RecipeBuilder(new FluidStack(fluid, amount));
        }

        public RecipeBuilder input(Ingredient input){
            this.input = input;
            return this;
        }

        public RecipeBuilder minHeat(double minHeat){
            this.minHeat = minHeat;
            return this;
        }

        public RecipeBuilder heatAbsorbed(double heatAbsorbed){
            this.heatAbsorbed = heatAbsorbed;
            return this;
        }

        public RecipeBuilder workTime(int workTime){
            this.workTime = workTime;
            return this;
        }

        public void save(Consumer<FinishedRecipe> consumer){
            this.save(consumer, ForgeRegistries.FLUIDS.getKey(output.getFluid()));
        }

        public void save(Consumer<FinishedRecipe> consumer, ResourceLocation location){
            consumer.accept(new Recipe(getLocation(location), input, output, minHeat, heatAbsorbed, workTime));
        }

        public void save(Consumer<FinishedRecipe> consumer, String location){
            consumer.accept(new Recipe(getLocation(location), input, output, minHeat, heatAbsorbed, workTime));
        }

        public static ResourceLocation getLocation(ResourceLocation location){
            return getLocation(location.getPath());
        }

        public static ResourceLocation getLocation(String path){
            return new ResourceLocation(DysonSphere.MODID, basePath + "/" + path);
        }
    }

    public static record Recipe(ResourceLocation id, Ingredient input, FluidStack output, double minHeat, double heatAbsorbed, int workTime) implements FinishedRecipe {

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            pJson.add("input", input.toJson());
            pJson.add("output", SerializationUtil.serializeFluidStack(output));
            pJson.addProperty("minHeat", minHeat);
            pJson.addProperty("heatAbsorbed", heatAbsorbed);
            pJson.addProperty("workTime", workTime);
        }

        @Override
        public ResourceLocation getId() {
            return id();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.MELTER_SERIALIZER.get();
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
