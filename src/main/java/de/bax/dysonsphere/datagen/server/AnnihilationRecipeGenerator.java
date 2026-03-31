package de.bax.dysonsphere.datagen.server;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.recipes.ModRecipes;
import de.bax.dysonsphere.tags.DSTags;
import de.bax.dysonsphere.util.FluidIngredient;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class AnnihilationRecipeGenerator {
    
    public static final String basePath = "matter_annihilation";

    public static void buildRecipes(Consumer<FinishedRecipe> pWriter){
        // 1mb Helium + 1mb Anti-Helium: 2.6 * 10^16 RF -- Math by LLM. Don't judge me
        createRecipe(26_000_000_000_000_000l, FluidIngredient.of(DSTags.fluidHelium, 1), FluidIngredient.of(DSTags.fluidAntiHelium, 1), "helium", pWriter);
    }

    public static void createRecipe(long energy, FluidIngredient fluidMatter, FluidIngredient fluidAnti, String path, Consumer<FinishedRecipe> pWriter){
        pWriter.accept(new Recipe(getLocation(path), energy, fluidMatter, fluidAnti));
    }

    public static ResourceLocation getLocation(String path){
        return new ResourceLocation(DysonSphere.MODID, basePath + "/" + path);
    }

    public static record Recipe(ResourceLocation id, long energy, FluidIngredient fluidMatter, FluidIngredient fluidAnti) implements FinishedRecipe {

        @Override
        public void serializeRecipeData(@Nonnull JsonObject pJson) {
            pJson.addProperty("producedEnergy", energy());
            pJson.add("fluidMatter", fluidMatter().toJson());
            pJson.add("fluidAnti", fluidAnti().toJson());
        }

        @Override
        public ResourceLocation getId() {
            return id();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.ANNIHILATION_SERIALIZER.get();
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
