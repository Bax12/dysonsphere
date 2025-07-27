package de.bax.dysonsphere.datagen.server;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.recipes.ModRecipes;
import de.bax.dysonsphere.tags.DSTags;
import de.bax.dysonsphere.util.FluidIngredient;
import de.bax.dysonsphere.util.SerializationUtil;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OrbitalLaunchRecipeGenerator {
    
    public static final String basePath = "orbital_launch";

    public static void buildRecipes(Consumer<FinishedRecipe> pWriter){
        RecipeBuilder.of(ModItems.CAPSULE_SOLAR_0).baseEnergy(90_000).save(pWriter);
        RecipeBuilder.of(ModItems.CAPSULE_LASER_0).baseEnergy(110_000).save(pWriter);
        RecipeBuilder.of(ModItems.CAPSULE_STRUCTURE_0).baseEnergy(160_000).save(pWriter);

        RecipeBuilder.of(ModItems.CAPSULE_SOLAR_1).baseEnergy(900_000).save(pWriter);
        RecipeBuilder.of(ModItems.CAPSULE_LASER_1).baseEnergy(1_100_000).save(pWriter);
        RecipeBuilder.of(ModItems.CAPSULE_STRUCTURE_1).baseEnergy(1_600_000).save(pWriter);

        RecipeBuilder.of(ModItems.CAPSULE_SOLAR_1).baseEnergy(90_000).addFluidInput(FluidIngredient.of(DSTags.fluidHelium, 50)).save(pWriter, "capsule_solar_1_helium");
        RecipeBuilder.of(ModItems.CAPSULE_LASER_1).baseEnergy(110_000).addFluidInput(FluidIngredient.of(DSTags.fluidHelium, 50)).save(pWriter, "capsule_laser_1_helium");
        RecipeBuilder.of(ModItems.CAPSULE_STRUCTURE_1).baseEnergy(160_000).addFluidInput(FluidIngredient.of(DSTags.fluidHelium, 50)).save(pWriter, "capsule_structure_1_helium");
    }

    public static class RecipeBuilder {
        private Ingredient input;
        private ItemStack launchStack;
        private List<Ingredient> extraInputs = new ArrayList<>();
        private List<FluidIngredient> fluidInputs = new ArrayList<>();
        private int baseEnergy;

        private RecipeBuilder(ItemStack launchStack){
            this.launchStack = launchStack;
        }

        public static RecipeBuilder of(ItemStack launchStack){
            return new RecipeBuilder(launchStack);
        }

        public static RecipeBuilder of(ItemLike launchStack){
            return new RecipeBuilder(new ItemStack(launchStack.asItem()));
        }

        public static RecipeBuilder of(RegistryObject<Item> launchStack){
            return new RecipeBuilder(new ItemStack(launchStack.get()));
        }

        public RecipeBuilder input(Ingredient input){
            this.input = input;
            return this;
        }

        public RecipeBuilder baseEnergy(int baseEnergy){
            this.baseEnergy = baseEnergy;
            return this;
        }

        public RecipeBuilder addExtraInput(Ingredient extraInput){
            extraInputs.add(extraInput);
            return this;
        }

        public RecipeBuilder addExtraInput(Ingredient extraInput, int amount){
            for(int i = 0; i < amount; i++){
                extraInputs.add(extraInput);
            }
            return this;
        }

        public RecipeBuilder addFluidInput(FluidIngredient fluid){
            fluidInputs.add(fluid);
            return this;
        }

        public void save(Consumer<FinishedRecipe> consumer){
            this.save(consumer, ForgeRegistries.ITEMS.getKey(launchStack.getItem()));
        }

        public void save(Consumer<FinishedRecipe> consumer, ResourceLocation location){
            this.save(consumer, location.getPath());
        }

        public void save(Consumer<FinishedRecipe> consumer, String location){
            if(input == null){
                input = Ingredient.of(launchStack);
            }
            consumer.accept(new Recipe(getLocation(location), input, launchStack, extraInputs, fluidInputs, baseEnergy));
        }

        public static ResourceLocation getLocation(ResourceLocation location){
            return getLocation(location.getPath());
        }

        public static ResourceLocation getLocation(String path){
            return new ResourceLocation(DysonSphere.MODID, basePath + "/" + path);
        }


    }

    public static record Recipe(ResourceLocation id, Ingredient input, ItemStack launchStack, List<Ingredient> extraInputs, List<FluidIngredient> fluidInputs, int baseEnergy) implements FinishedRecipe {

        @Override
        public void serializeRecipeData(@Nonnull JsonObject pJson) {
            pJson.add("input", input().toJson());
            pJson.add("launchStack", SerializationUtil.serializeItemStack(launchStack()));
            pJson.addProperty("baseEnergy", baseEnergy());
            JsonArray jsonExtras = new JsonArray();
            for(Ingredient extra : extraInputs){
                jsonExtras.add(extra.toJson());
            }
            pJson.add("extraInputs", jsonExtras);
            JsonArray jsonFluids = new JsonArray();
            for(FluidIngredient fluid : fluidInputs()){
                jsonFluids.add(fluid.toJson());
            }
            pJson.add("fluidInputs", jsonFluids);
        }

        @Override
        public ResourceLocation getId() {
            return id();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.ORBITAL_LAUNCH_SERIALIZER.get();
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
