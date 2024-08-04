package de.bax.dysonsphere.datagen.server;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.tags.DSTags;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

public class RecipeGenerator extends RecipeProvider {

    public static final InventoryChangeTrigger.TriggerInstance UNLOCK = has(Items.COPPER_INGOT);

    public RecipeGenerator(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@Nonnull Consumer<FinishedRecipe> consumer) {
        HeatExchangerRecipeGenerator.buildRecipes(consumer);
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
            .define('C', DSTags.itemCoilCopper)
            .define('E', DSTags.itemCapsuleEmpty)
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
            .define('C', DSTags.itemCoilCopper)
            .define('I', Tags.Items.INGOTS_IRON)
            .define('H', ModItems.HEAT_SHIELDING.get())
            .save(consumer);

        Recipe.shaped(ModItems.SOLAR_FOIL.get())
            .pattern("GGG")
            .pattern("ILI")
            .pattern("LIL")
            .define('G', Tags.Items.GLASS)
            .define('I', DSTags.itemCoilIron)
            .define('L', Tags.Items.GEMS_LAPIS)
            .save(consumer);

        Recipe.shaped(ModItems.THERMOPILE.get())
            .pattern("CCC")
            .pattern("III")
            .pattern("CCC")
            .define('C', DSTags.itemCoilCopper)
            .define('I', DSTags.itemCoilIron)
            .save(consumer);

        Recipe.shaped(ModBlocks.HEAT_PIPE_BLOCK.get(), 3)
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
            .define('C', DSTags.itemCoilIron)
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
            .define('C', DSTags.itemCoilCopper)
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
            .define('c', DSTags.itemCoilIron)
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

        Recipe.shaped(ModItems.LASER_CONTROLLER.get())
            .pattern("SSC")
            .pattern("SCI")
            .define('S', DSTags.itemIngotSmartAlloy)
            .define('I', Tags.Items.INGOTS_IRON)
            .define('C', DSTags.itemCoilCopper)
            .save(consumer);

        Recipe.shaped(ModItems.LASER_PATTERN.get())
            .pattern("ICI")
            .pattern("CSC")
            .pattern("ICI")
            .define('S', DSTags.itemIngotSmartAlloy)
            .define('I', DSTags.itemCoilIron)
            .define('C', DSTags.itemCoilCopper)
            .save(consumer);

        Recipe.shapeless(ModItems.INGOT_SMART_ALLOY.get())
            .requires(Items.POLISHED_DIORITE, 2)
            .requires(Ingredient.of(Tags.Items.INGOTS_GOLD), 2)
            .save(consumer);

        Recipe.shaped(ModBlocks.LASER_PATTERN_CONTROLLER_BLOCK.get())
            .pattern("GGG")
            .pattern("ILI")
            .pattern("IRI")
            .define('G', Tags.Items.GLASS)
            .define('I', DSTags.itemCoilIron)
            .define('L', Items.REDSTONE_LAMP)
            .define('R', Items.REDSTONE)
            .save(consumer);

        Recipe.shaped(ModBlocks.LASER_CONTROLLER_BLOCK.get())
            .pattern("IEI")
            .pattern("SCS")
            .pattern("ScS")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('E', Tags.Items.ENDER_PEARLS)
            .define('S', DSTags.itemIngotSmartAlloy)
            .define('C', ModItems.COMPONENT_SMART_ALLOY.get())
            .define('c', DSTags.itemCoilCopper)
            .save(consumer);

        Recipe.shaped(ModBlocks.LASER_CRAFTER_BLOCK.get())
            .pattern("IGI")
            .pattern("GBG")
            .pattern("SCS")
            .define('I', DSTags.itemCoilIron)
            .define('G', Tags.Items.GLASS)
            .define('B', Tags.Items.STORAGE_BLOCKS_IRON)
            .define('S', DSTags.itemIngotSmartAlloy)
            .define('C', ModBlocks.HEAT_PIPE_BLOCK.get())
            .save(consumer);

        Recipe.shaped(ModItems.CAPSULE_LASER.get())
            .pattern("SCS")
            .pattern("TET")
            .pattern("HRH")
            .define('S', DSTags.itemIngotSmartAlloy)
            .define('C', DSTags.itemCoilCopper)
            .define('T', ModBlocks.HEAT_PIPE_BLOCK.get())
            .define('E', DSTags.itemCapsuleEmpty)
            .define('H', ModItems.HEAT_SHIELDING.get())
            .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .save(consumer);
    }
    

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
            public Shapeless(ItemLike result) {
                this(result, 1);
            }

            public Shapeless(ItemLike result, int countIn) {
                super(RecipeCategory.MISC, result, countIn);
            }

            @Override
            public void save(@Nonnull Consumer<FinishedRecipe> consumer) {
                // this.unlockedBy("has_book", UNLOCK);
                super.save(consumer);
            }
            @Override
            public void save(@Nonnull Consumer<FinishedRecipe> consumer, @Nonnull ResourceLocation location) {
                this.unlockedBy("has_book", UNLOCK);
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

            @Override
            public void save(@Nonnull Consumer<FinishedRecipe> consumerIn) {
                // this.unlockedBy("has_book", UNLOCK);
                super.save(consumerIn);
            }

            @Override
            public void save(@Nonnull Consumer<FinishedRecipe> consumer, @Nonnull ResourceLocation location) {
                this.unlockedBy("has_book", UNLOCK);
                super.save(consumer, location);
            }
        }
    }
}
