package de.bax.dysonsphere.datagen.server;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.recipes.ModRecipes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LaserCraftingRecipeGenerator {

    public static final String basePath = "laser_crafting";


    public static void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        //energy: one mekansim laser: 10 000 full charged amplifier: 5 000 000 000 charge of 100 000 000Fe: 250 000 000 (amplifier has 2.5x higher peak when shooting a single pulse)
        // random orbital: 2 000 000
        RecipeBuilder.of(Items.DIAMOND).input(Ingredient.of(Tags.Items.STORAGE_BLOCKS_COAL)).energyRequired(10_000_000).save(pWriter);
        RecipeBuilder.of(ModItems.UNIVERSE_WHISPER).input(Ingredient.of(Items.ECHO_SHARD)).energyRequired(2_000_000).save(pWriter);
        RecipeBuilder.of(ModItems.COMPONENT_SMART_ALLOY).input(Ingredient.of(ModItems.INGOT_SMART_ALLOY.get())).energyRequired(500_000).save(pWriter);
        RecipeBuilder.of(Items.BUDDING_AMETHYST).input(Ingredient.of(Items.AMETHYST_BLOCK)).energyRequired(5_000_000).save(pWriter);
    }

    public static class RecipeBuilder {

        private Ingredient input;
        private ItemStack output;
        private long energyRequired;

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
            return new RecipeBuilder(new ItemStack(output.get()));
        }

        public RecipeBuilder input(Ingredient input){
            this.input = input;
            return this;
        }

        public RecipeBuilder energyRequired(long energyRequired){
            this.energyRequired = energyRequired;
            return this;
        }

        public void save(Consumer<FinishedRecipe> consumer){
            this.save(consumer, ForgeRegistries.ITEMS.getKey(output.getItem()));
        }

        public void save(Consumer<FinishedRecipe> consumer, ResourceLocation location){
            consumer.accept(new Recipe(getLocation(location), input, output, energyRequired));
        }

        public ResourceLocation getLocation(ResourceLocation location){
            return getLocation(location.getPath());
        }

        public ResourceLocation getLocation(String path){
            return new ResourceLocation(DysonSphere.MODID, basePath + "/" + path);
        }

    }

    public static record Recipe(ResourceLocation id, Ingredient input, ItemStack output, long inputEnergy) implements FinishedRecipe{

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            pJson.add("input", input.toJson());
            pJson.add("output", serializeItemStack(output));
            pJson.addProperty("inputEnergy", inputEnergy);
        }

        @Override
        public ResourceLocation getId() {
            return id();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.LASER_CRAFTING_SERIALIZER.get();
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

        private static JsonObject serializeItemStack(ItemStack stack){
            JsonObject json = new JsonObject();

            json.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
            if(stack.hasTag()){
                json.addProperty("tag", stack.getTag().getAsString());
            }
            if(stack.getCount() > 1){
                json.addProperty("count", stack.getCount());
            }           
            
            return json;
        }

    }
    
}
