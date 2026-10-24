package de.bax.dysonsphere.datagen.server;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.recipes.ModRecipes;
import de.bax.dysonsphere.schematic.ISchematicElement;
import de.bax.dysonsphere.schematic.ModSchematics;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;

public class SchematicBaseInputGenerator {
    
    public static final String basePath = "schematic_input";

    public static void buildRecipes(Consumer<FinishedRecipe> pWriter){
        RecipeBuilder.of(ModSchematics.AND).input(Tags.Items.GEMS_EMERALD).save(pWriter);
        RecipeBuilder.of(ModSchematics.OR).input(Tags.Items.GEMS_PRISMARINE).save(pWriter);
        RecipeBuilder.of(ModSchematics.NOT).input(Tags.Items.GEMS_QUARTZ).save(pWriter);
        RecipeBuilder.of(ModSchematics.NOR).input(Tags.Items.GEMS_LAPIS).save(pWriter);
        RecipeBuilder.of(ModSchematics.NAND).input(Tags.Items.GEMS_AMETHYST).save(pWriter);
    }

    public static class RecipeBuilder {
        private Ingredient input;
        private ISchematicElement element;

        private RecipeBuilder(ISchematicElement element){
            this.element = element;
        }

        public static RecipeBuilder of(ISchematicElement element){
            return new RecipeBuilder(element);
        }

        public static RecipeBuilder of(RegistryObject<ISchematicElement> element){
            return new RecipeBuilder(element.get());
        }

        public RecipeBuilder input(Ingredient input){
            this.input = input;
            return this;
        }

        public RecipeBuilder input(TagKey<Item> tag){
            this.input = Ingredient.of(tag);
            return this;
        }

        public void save(Consumer<FinishedRecipe> consumer){
            save(consumer, element.getResourceLocation());
        }

        public void save(Consumer<FinishedRecipe> consumer, ResourceLocation location){
            consumer.accept(new Recipe(getLocation(location), input, element));
        }

        public static ResourceLocation getLocation(ResourceLocation location){
            return getLocation(location.getPath());
        }

        public static ResourceLocation getLocation(String path){
            return new ResourceLocation(DysonSphere.MODID, basePath + "/" + path);
        }

    }

    public static record Recipe(ResourceLocation id, Ingredient input, ISchematicElement element) implements FinishedRecipe {

        @Override
        public void serializeRecipeData(@Nonnull JsonObject pJson) {
            pJson.add("input", input().toJson());
            pJson.add("element", element().toJson());
        }

        @Override
        public ResourceLocation getId() {
            return id();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.SCHEMATIC_INPUT_SERIALIZER.get();
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
