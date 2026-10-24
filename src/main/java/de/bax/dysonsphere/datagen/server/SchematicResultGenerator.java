package de.bax.dysonsphere.datagen.server;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.recipes.ModRecipes;
import de.bax.dysonsphere.util.SerializationUtil;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SchematicResultGenerator {
    
    public static final String basePath = "schematic_output";

    public static void buildRecipes(Consumer<FinishedRecipe> pWriter){
        RecipeBuilder.of(ModItems.CIRCUIT_0).inputCount(2).outputCount(2).minDontCare(0).maxDontCare(1).save(pWriter);
        RecipeBuilder.of(ModItems.CIRCUIT_1).inputCount(4).outputCount(4).minDontCare(2).maxDontCare(4).save(pWriter);
        RecipeBuilder.of(ModItems.CIRCUIT_2).inputCount(8).outputCount(8).minDontCare(4).maxDontCare(8).save(pWriter);
        RecipeBuilder.of(ModItems.CIRCUIT_3).inputCount(16).outputCount(16).minDontCare(16).maxDontCare(64).save(pWriter);
    }

    public static class RecipeBuilder {
        private ItemStack output;
        private byte outputCount;
        private byte inputCount;
        private byte minDontCare;
        private byte maxDontCare;

        private RecipeBuilder(ItemStack output){
            this.output = output;
        }

        public static RecipeBuilder of(ItemStack output){
            return new RecipeBuilder(output);
        }

        public static RecipeBuilder of(ItemLike output){
            return new RecipeBuilder(new ItemStack(output.asItem()));
        }

        public static RecipeBuilder of(RegistryObject<Item> output){
            return new RecipeBuilder(new ItemStack(output.get().asItem()));
        }

        public RecipeBuilder outputCount(int outputCount){
            this.outputCount = (byte) outputCount;
            return this;
        }

        public RecipeBuilder inputCount(int inputCount){
            this.inputCount = (byte) inputCount;
            return this;
        }

        public RecipeBuilder minDontCare(int minDontCare){
            this.minDontCare = (byte) minDontCare;
            return this;
        }

        public RecipeBuilder maxDontCare(int maxDontCare){
            this.maxDontCare = (byte) maxDontCare;
            return this;
        }

        public void save(Consumer<FinishedRecipe> consumer){
            this.save(consumer, ForgeRegistries.ITEMS.getKey(output.getItem()));
        }

        public void save(Consumer<FinishedRecipe> consumer, ResourceLocation location){
            this.save(consumer, location.getPath());
        }

        public void save(Consumer<FinishedRecipe> consumer, String location){
            consumer.accept(new Recipe(getLocation(location), outputCount, inputCount, minDontCare, maxDontCare, output));
        }

        public static ResourceLocation getLocation(ResourceLocation location){
            return getLocation(location.getPath());
        }

        public static ResourceLocation getLocation(String path){
            return new ResourceLocation(DysonSphere.MODID, basePath + "/" + path);
        }

    }

    public static record Recipe(ResourceLocation id, byte outputCount, byte inputCount, byte minDontCare, byte maxDontCare, ItemStack output) implements FinishedRecipe {

        @Override
        public void serializeRecipeData(@Nonnull JsonObject pJson) {
            pJson.add("output", SerializationUtil.serializeItemStack(output()));
            pJson.addProperty("outputCount", outputCount());
            pJson.addProperty("inputCount", inputCount());
            pJson.addProperty("minDontCare", minDontCare());
            pJson.addProperty("maxDontCare", maxDontCare());
        }

        @Override
        public ResourceLocation getId() {
            return id();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.SCHEMATIC_RESULT_SERIALIZER.get();
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
