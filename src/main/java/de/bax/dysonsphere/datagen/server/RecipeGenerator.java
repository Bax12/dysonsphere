package de.bax.dysonsphere.datagen.server;

import java.util.Arrays;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.items.ModItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

public class RecipeGenerator extends RecipeProvider {

    public static final InventoryChangeTrigger.TriggerInstance UNLOCK = has(Items.COPPER_INGOT);

    public RecipeGenerator(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        LaserCraftingRecipeGenerator.buildRecipes(consumer);

        Recipe.shaped(ModItems.CAPSULE_EMPTY.get())
            .pattern("HHH")
            .pattern("I I")
            .pattern("IBI")
            .define('H', ModItems.HEAT_SHIELDING.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('B', Items.BUCKET)
            .save(consumer);
        
        Recipe.shaped(ModItems.CAPSULE_SOLAR.get())
            .pattern("SSS")
            .pattern("CEC")
            .pattern("GHG")
            .define('S', ModItems.SOLAR_FOIL.get())
            .define('C', ModItems.COIL_COPPER.get())
            .define('E', ModItems.CAPSULE_EMPTY.get())
            .define('G', Tags.Items.INGOTS_GOLD)
            .define('H', ModItems.HEAT_SHIELDING.get())
            .save(consumer);

        Recipe.shaped(ModItems.COIL_COPPER.get(), 4)
            .pattern("CSC")
            .pattern("CSC")
            .pattern("CSC")
            .define('C', Tags.Items.INGOTS_COPPER)
            .define('S', Items.STICK)
            .save(consumer);

        Recipe.shaped(ModItems.COIL_IRON.get(), 4)
            .pattern("ISI")
            .pattern("ISI")
            .pattern("ISI")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('S', Items.STICK)
            .save(consumer);

        Recipe.shaped(ModItems.HEAT_SHIELDING.get())
            .pattern("BBB")
            .pattern("BCB")
            .pattern("BBB")
            .define('B', Tags.Items.INGOTS_BRICK)
            .define('C', Tags.Items.STORAGE_BLOCKS_COAL)
            .save(consumer);

        Recipe.shaped(ModItems.RAILGUN.get())
            .pattern("CIC")
            .pattern("CIC")
            .pattern("HCH")
            .define('C', ModItems.COIL_COPPER.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('H', ModItems.HEAT_SHIELDING.get())
            .save(consumer);

        Recipe.shaped(ModItems.SOLAR_FOIL.get())
            .pattern("GGG")
            .pattern("ILI")
            .pattern("LIL")
            .define('G', Tags.Items.GLASS)
            .define('I', ModItems.COIL_IRON.get())
            .define('L', Tags.Items.GEMS_LAPIS)
            .save(consumer);

        Recipe.shaped(ModItems.THERMOPILE.get())
            .pattern("CCC")
            .pattern("III")
            .pattern("CCC")
            .define('C', ModItems.COIL_COPPER.get())
            .define('I', ModItems.COIL_IRON.get())
            .save(consumer);

        Recipe.shaped(ModBlocks.HEAT_PIPE_BLOCK.get())
            .pattern("HcH")
            .pattern("cCc")
            .pattern("HcH")
            .define('H', ModItems.HEAT_SHIELDING.get())
            .define('c', Tags.Items.INGOTS_COPPER)
            .define('C', Tags.Items.STORAGE_BLOCKS_COPPER)
            .save(consumer);

        Recipe.shaped(ModBlocks.DS_MONITOR_BLOCK.get())
            .pattern(" IG")
            .pattern("TIG")
            .pattern("ICI")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('G', Tags.Items.GLASS)
            .define('T', Items.REDSTONE_TORCH)
            .define('C', ModItems.COIL_IRON.get())
            .save(consumer);

        Recipe.shaped(ModBlocks.RAILGUN_BLOCK.get())
            .pattern(" R ")
            .pattern("iIi")
            .pattern("IAI")
            .define('R', ModItems.RAILGUN.get())
            .define('i', Tags.Items.INGOTS_IRON)
            .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
            .define('A', Items.ANVIL)
            .save(consumer);

        Recipe.shaped(ModBlocks.DS_ENERGY_RECEIVER_BLOCK.get())
            .pattern("CCC")
            .pattern("CIC")
            .pattern("HBH")
            .define('C', ModItems.COIL_COPPER.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('H', ModItems.HEAT_SHIELDING.get())
            .define('B', ModBlocks.HEAT_PIPE_BLOCK.get())
            .save(consumer);

        Recipe.shaped(ModBlocks.HEAT_EXCHANGER_BLOCK.get())
            .pattern("HPH")
            .pattern("cBc")
            .pattern("HPH")
            .define('H', ModItems.HEAT_SHIELDING.get())
            .define('P', ModBlocks.HEAT_PIPE_BLOCK.get())
            .define('c', ModItems.COIL_IRON.get())
            .define('B', Items.BUCKET)
            .save(consumer);

        Recipe.shaped(ModBlocks.HEAT_GENERATOR_BLOCK.get())
            .pattern("HTH")
            .pattern("TTT")
            .pattern("HTH")
            .define('H', ModItems.HEAT_SHIELDING.get())
            .define('T', ModItems.THERMOPILE.get())
            .save(consumer);

        
        Recipe.shapeless(Items.BAKED_POTATO, 8)
            .requires(Items.POTATO, 8)
            .requires(ModItems.STEAM_BUCKET.get())
            .save(consumer, new ResourceLocation(DysonSphere.MODID, "baked_potato"));

        
    }
    



    /*
     * Adapted from Actually Additions. All credit goes to the original author Ellpeck.
     * Original Source: https://github.com/Ellpeck/ActuallyAdditions/blob/1.20.1/src/main/java/de/ellpeck/actuallyadditions/data/ItemRecipeGenerator.java
     * License: https://github.com/Ellpeck/ActuallyAdditions/blob/1.20.1/LICENSE
     */
    public static class Recipe {
        public static RecipeGenerator.Recipe.Shapeless shapeless(ItemLike result) {
            return new RecipeGenerator.Recipe.Shapeless(result);
        }

        public static RecipeGenerator.Recipe.Shapeless shapeless(ItemLike result, int count) {
            return new RecipeGenerator.Recipe.Shapeless(result, count);
        }

        public static RecipeGenerator.Recipe.Shaped shaped(ItemLike result) {
            return new RecipeGenerator.Recipe.Shaped(result);
        }

        public static RecipeGenerator.Recipe.Shaped shaped(ItemLike result, int count) {
            return new RecipeGenerator.Recipe.Shaped(result, count);
        }

        private static class Shapeless extends ShapelessRecipeBuilder {
            private ResourceLocation name;
            public Shapeless(ItemLike result) {
                this(result, 1);
            }

            public Shapeless(ItemLike result, int countIn) {
                super(RecipeCategory.MISC, result, countIn);
            }

            public RecipeGenerator.Recipe.Shapeless ingredients(ItemLike... ingredients) {
                Arrays.asList(ingredients).forEach(this::requires);
                return this;
            }

            public RecipeGenerator.Recipe.Shapeless name(ResourceLocation name) {
                this.name = name;
                return this;
            }

            @Override
            public void save(@Nonnull Consumer<FinishedRecipe> consumer) {
                this.unlockedBy("has_book", UNLOCK);
                if (this.name != null) {
                    this.save(consumer, this.name);
                } else {
                    super.save(consumer);
                }
            }
            @Override
            public void save(@Nonnull Consumer<FinishedRecipe> consumer, @Nonnull ResourceLocation location) {
                this.unlockedBy("", has(Items.AIR));
                super.save(consumer, location);
            }
        }

        private static class Shaped extends ShapedRecipeBuilder {
            public Shaped(ItemLike resultIn) {
                this(resultIn, 1);
            }

            public Shaped(ItemLike resultIn, int countIn) {
                super(RecipeCategory.MISC, resultIn, countIn);
            }

            public RecipeGenerator.Recipe.Shaped pattern(String line1, String line2, String line3) {
                this.pattern(line1);
                this.pattern(line2);
                this.pattern(line3);
                return this;
            }

            public RecipeGenerator.Recipe.Shaped pattern(String line1, String line2) {
                this.pattern(line1);
                this.pattern(line2);
                return this;
            }

            public RecipeGenerator.Recipe.Shaped patternSingleKey(char key, ItemLike resource, String... lines) {
                this.define(key, resource);
                for (String line : lines) {
                    this.pattern(line);
                }

                return this;
            }

            // public RecipeGenerator.Recipe.Shaped requiresBook() {
            //     this.unlockedBy("has_book", has(ActuallyItems.ITEM_BOOKLET.get()));
            //     return this;
            // }

            @Override
            public void save(@Nonnull Consumer<FinishedRecipe> consumerIn) {
                this.unlockedBy("has_book", UNLOCK);
                super.save(consumerIn);
            }

            @Override
            public void save(@Nonnull Consumer<FinishedRecipe> consumer, @Nonnull ResourceLocation location) {
                this.unlockedBy("", has(Items.AIR));
                super.save(consumer, location);
            }
        }
    }
    //Actually Additions end
}
