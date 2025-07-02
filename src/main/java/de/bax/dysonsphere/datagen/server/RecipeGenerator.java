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
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.AndCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;

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
            .pattern("S S")
            .pattern("ISI")
            .define('H', ModItems.HEAT_SHIELDING.get())
            .define('I', Tags.Items.INGOTS_IRON)
            .define('S', DSTags.itemIngotSmartAlloy)
            .save(consumer);
        
        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.LUMIUM_TAG_EMPTY)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.CAPSULE_SOLAR.get())
            .pattern("SSS")
            .pattern("CEC")
            .pattern("GHG")
            .define('S', ModItems.SOLAR_FOIL.get())
            .define('C', DSTags.itemCoilCopper)
            .define('E', DSTags.itemCapsuleEmpty)
            .define('G', Tags.Items.INGOTS_GOLD)
            .define('H', ModItems.HEAT_SHIELDING.get())
            .save(con);
        })
        .addCondition(RecipeConditions.LUMIUM_EXISTS)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.CAPSULE_SOLAR.get())
            .pattern("SSS")
            .pattern("CEC")
            .pattern("LHL")
            .define('S', ModItems.SOLAR_FOIL.get())
            .define('C', DSTags.itemCoilCopper)
            .define('E', DSTags.itemCapsuleEmpty)
            .define('L', DSTags.itemIngotLumium)
            .define('H', ModItems.HEAT_SHIELDING.get())
            .save(con);
        })
        .build(consumer, ModItems.CAPSULE_SOLAR.getId());

        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.WIRE_COPPER_TAG_EMPTY)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.COIL_COPPER.get(), 4)
            .pattern("CSC")
            .pattern("CSC")
            .pattern("CSC")
            .define('C', Tags.Items.INGOTS_COPPER)
            .define('S', Items.STICK)
            .save(con);
        })
        .addCondition(RecipeConditions.WIRE_COPPER_EXISTS)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.COIL_COPPER.get())
            .pattern(" C ")
            .pattern("CSC")
            .pattern(" C ")
            .define('C', DSTags.itemWireCopper)
            .define('S', Items.STICK)
            .save(con);
        })
        .build(consumer, ModItems.COIL_COPPER.getId());

        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.WIRE_IRON_TAG_EMPTY)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.COIL_IRON.get(), 4)
            .pattern("ISI")
            .pattern("ISI")
            .pattern("ISI")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('S', Items.STICK)
            .save(con);
        })
        .addCondition(RecipeConditions.WIRE_IRON_EXISTS)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.COIL_IRON.get())
            .pattern(" I ")
            .pattern("ISI")
            .pattern(" I ")
            .define('I', DSTags.itemWireIron)
            .define('S', Items.STICK)
            .save(con);
        })
        .build(consumer, ModItems.COIL_IRON.getId());



        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.TUNGSTEN_TAG_EMPTY)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.HEAT_SHIELDING.get())
            .pattern("BBB")
            .pattern("BCB")
            .pattern("BBB")
            .define('B', Tags.Items.INGOTS_BRICK)
            .define('C', Tags.Items.STORAGE_BLOCKS_COAL)
            .save(con);
        })
        .addCondition(RecipeConditions.TUNGSTEN_EXISTS)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.HEAT_SHIELDING.get())
            .pattern("BTB")
            .pattern("TCT")
            .pattern("BTB")
            .define('B', Tags.Items.INGOTS_BRICK)
            .define('C', Tags.Items.STORAGE_BLOCKS_COAL)
            .define('T', DSTags.itemIngotTungsten)
            .save(con);
        })
        .build(consumer, ModItems.HEAT_SHIELDING.getId());

        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.CIRCUIT_TAG_EMPTY)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.RAILGUN.get())
                .pattern("CIC")
                .pattern("CIC")
                .pattern("HCH")
                .define('C', DSTags.itemCoilCopper)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('H', ModItems.HEAT_SHIELDING.get())
                .save(con);
        })
        .addCondition(RecipeConditions.CIRCUIT_EXISTS)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.RAILGUN.get())
                .pattern("CIC")
                .pattern("CIC")
                .pattern("HPH")
                .define('C', DSTags.itemCoilCopper)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('H', ModItems.HEAT_SHIELDING.get())
                .define('P', DSTags.itemCircuit)
                .save(con);
        })
        .build(consumer, ModItems.RAILGUN.getId());

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

        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.CIRCUIT_TAG_EMPTY)
        .addRecipe((con) -> {
            Recipe.shaped(ModBlocks.DS_MONITOR_BLOCK.get())
            .pattern(" IG")
            .pattern("TIG")
            .pattern("ICI")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('G', Tags.Items.GLASS)
            .define('T', Items.REDSTONE_TORCH)
            .define('C', DSTags.itemCoilIron)
            .save(con);
        })
        .addCondition(RecipeConditions.CIRCUIT_EXISTS)
        .addRecipe((con) -> {
            Recipe.shaped(ModBlocks.DS_MONITOR_BLOCK.get())
            .pattern(" IG")
            .pattern("TIG")
            .pattern("ICI")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('G', Tags.Items.GLASS)
            .define('T', Items.REDSTONE_TORCH)
            .define('C', DSTags.itemCircuit)
            .save(con);
        })
        .build(consumer, ModBlocks.DS_MONITOR_BLOCK.getId());

        

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
            
        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.CIRCUIT_TAG_EMPTY)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.LASER_CONTROLLER.get())
            .pattern("SSC")
            .pattern("SCI")
            .define('S', DSTags.itemIngotSmartAlloy)
            .define('I', Tags.Items.INGOTS_IRON)
            .define('C', DSTags.itemCoilCopper)
            .save(con);
        })
        .addCondition(RecipeConditions.CIRCUIT_EXISTS)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.LASER_CONTROLLER.get())
            .pattern("SSC")
            .pattern("SCP")
            .define('S', DSTags.itemIngotSmartAlloy)
            .define('P', DSTags.itemCircuit)
            .define('C', DSTags.itemCoilCopper)
            .save(con);
        })
        .build(consumer, ModItems.LASER_CONTROLLER.getId());
        

        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.CIRCUIT_TAG_EMPTY)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.LASER_PATTERN.get())
            .pattern("ICI")
            .pattern("CSC")
            .pattern("ICI")
            .define('S', DSTags.itemIngotSmartAlloy)
            .define('I', DSTags.itemCoilIron)
            .define('C', DSTags.itemCoilCopper)
            .save(con);
        })
        .addCondition(RecipeConditions.CIRCUIT_EXISTS)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.LASER_PATTERN.get())
            .pattern("ICI")
            .pattern("CPC")
            .pattern("ICI")
            .define('P', DSTags.itemCircuit)
            .define('I', DSTags.itemCoilIron)
            .define('C', DSTags.itemCoilCopper)
            .save(con);
        })
        .build(consumer, ModItems.LASER_PATTERN.getId());
        
        ConditionalRecipe.builder()
        .addCondition(new OrCondition(RecipeConditions.NICKEL_TAG_EMPTY, RecipeConditions.TITANIUM_TAG_EMPTY))
        .addRecipe((con) -> {
            Recipe.shapeless(ModItems.INGOT_SMART_ALLOY.get())
            .requires(Items.POLISHED_DIORITE, 2)
            .requires(Ingredient.of(Tags.Items.INGOTS_GOLD), 2)
            .save(con);
        })
        .addCondition(new AndCondition(RecipeConditions.NICKEL_EXISTS, RecipeConditions.TITANIUM_EXISTS))
        .addRecipe((con) -> {
            Recipe.shapeless(ModItems.INGOT_SMART_ALLOY.get())
            .requires(DSTags.itemIngotNickel)
            .requires(DSTags.itemIngotTitanium)
            .save(con);
        })
        .build(consumer, ModItems.INGOT_SMART_ALLOY.getId());

        

        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.CIRCUIT_TAG_EMPTY)
        .addRecipe((con) -> {
            Recipe.shaped(ModBlocks.LASER_PATTERN_CONTROLLER_BLOCK.get())
            .pattern("GGG")
            .pattern("ILI")
            .pattern("IRI")
            .define('G', Tags.Items.GLASS)
            .define('I', DSTags.itemCoilIron)
            .define('L', Items.REDSTONE_LAMP)
            .define('R', Items.REDSTONE)
            .save(con);
        })
        .addCondition(RecipeConditions.CIRCUIT_EXISTS)
        .addRecipe((con) -> {
            Recipe.shaped(ModBlocks.LASER_PATTERN_CONTROLLER_BLOCK.get())
            .pattern("GGG")
            .pattern("ILI")
            .pattern("IPI")
            .define('G', Tags.Items.GLASS)
            .define('I', DSTags.itemCoilIron)
            .define('L', Items.REDSTONE_LAMP)
            .define('P', DSTags.itemCircuit)
            .save(con);
        })
        .build(consumer, ModBlocks.LASER_PATTERN_CONTROLLER_BLOCK.getId());

        Recipe.shaped(ModBlocks.LASER_CONTROLLER_BLOCK.get())
            .pattern("IEI")
            .pattern("SCS")
            .pattern("ScS")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('E', ModItems.CONSTRUCT_ENDER.get())
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

        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.SIGNALUM_TAG_EMPTY)
        .addRecipe((con) -> {
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
                .save(con);
        })
        .addCondition(RecipeConditions.SIGNALUM_EXISTS)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.CAPSULE_LASER.get())
                .pattern("SCS")
                .pattern("sEs")
                .pattern("TsT")
                .define('S', DSTags.itemIngotSmartAlloy)
                .define('C', DSTags.itemCoilCopper)
                .define('T', ModBlocks.HEAT_PIPE_BLOCK.get())
                .define('E', DSTags.itemCapsuleEmpty)
                .define('s', DSTags.itemIngotSignalum)
                .save(con);
        })
        .build(consumer, ModItems.CAPSULE_LASER.getId());

        Recipe.shaped(ModItems.CONSTRUCT_ENDER.get())
            .pattern("ECE")
            .pattern("CEC")
            .pattern("ECE")
            .define('E', Tags.Items.ENDER_PEARLS)
            .define('C', ModItems.COMPONENT_SMART_ALLOY.get())
            .save(consumer);

        Recipe.shaped(ModItems.GRAPPLING_HOOK_HARNESS.get())
            .pattern("B B")
            .pattern("SLS")
            .pattern("III")
            .define('B', ItemTags.BUTTONS)
            .define('S', Tags.Items.RODS)
            .define('I', Tags.Items.INGOTS)
            .define('L', Tags.Items.LEATHER)
            .save(consumer);

        Recipe.shaped(ModItems.GRAPPLING_HOOK_CONTROLLER.get())
            .pattern("  B")
            .pattern(" S ")
            .pattern("S  ")
            .define('B', ItemTags.BUTTONS)
            .define('S', Tags.Items.RODS_WOODEN)
            .save(consumer);

        Recipe.shaped(ModItems.GRAPPLING_HOOK_HOOK_SMART_ALLOY.get())
            .pattern("III")
            .pattern("I I")
            .pattern("  I")
            .define('I', DSTags.itemIngotSmartAlloy)
            .save(consumer);
            
        Recipe.shaped(ModItems.GRAPPLING_HOOK_HOOK_BLAZE.get())
            .pattern("III")
            .pattern("I I")
            .pattern("  I")
            .define('I', Tags.Items.RODS_BLAZE)
            .save(consumer);

        Recipe.shaped(ModItems.GRAPPLING_HOOK_HOOK_WOOD.get())
            .pattern("III")
            .pattern("I I")
            .pattern("  I")
            .define('I', ItemTags.PLANKS)
            .save(consumer);

        Recipe.shapeless(ModItems.GRAPPLING_HOOK_HOOK_SLIME.get())
            .requires(ModItems.GRAPPLING_HOOK_HOOK_WOOD.get())
            .requires(Ingredient.of(Tags.Items.SLIMEBALLS), 3)
            .save(consumer);

        Recipe.shaped(ModItems.GRAPPLING_HOOK_ENGINE_STEAM.get())
            .pattern("PCC")
            .pattern("CPC")
            .pattern("SSS")
            .define('P', Items.PISTON)
            .define('C', Tags.Items.INGOTS_COPPER)
            .define('S', Tags.Items.STONE)
            .save(consumer);

        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.CIRCUIT_TAG_EMPTY)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC.get())
            .pattern("III")
            .pattern("CCC")
            .pattern("IRI")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('C', DSTags.itemCoilCopper)
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .save(con);
            })
        .addCondition(RecipeConditions.CIRCUIT_EXISTS)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC.get())
            .pattern("III")
            .pattern("CCC")
            .pattern("IPI")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('C', DSTags.itemCoilCopper)
            .define('P', DSTags.itemCircuit)
            .save(con);
            })
        .build(consumer, ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC.getId());

        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.CIRCUIT_TAG_EMPTY)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC_2.get())
            .pattern("AAS")
            .pattern("ASA")
            .pattern("CCC")
            .define('A', DSTags.itemIngotSmartAlloy)
            .define('S', ModItems.COMPONENT_SMART_ALLOY.get())
            .define('C', DSTags.itemCoilCopper)
            .save(con);
        })
        .addCondition(RecipeConditions.CIRCUIT_EXISTS)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC_2.get())
            .pattern("AAS")
            .pattern("ASA")
            .pattern("PCP")
            .define('A', DSTags.itemIngotSmartAlloy)
            .define('S', ModItems.COMPONENT_SMART_ALLOY.get())
            .define('C', DSTags.itemCoilCopper)
            .define('P', DSTags.itemCircuit)
            .save(con);
        })
        .build(consumer, ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC_2.getId());

        Recipe.shaped(ModItems.GRAPPLING_HOOK_ENGINE_MANUAL.get())
            .pattern("WWS")
            .pattern("SSW")
            .pattern(" W ")
            .define('W', ItemTags.PLANKS)
            .define('S', Tags.Items.RODS_WOODEN)
            .save(consumer);

        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.GEAR_TAG_EMPTY)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.GRAPPLING_HOOK_ENGINE_MECHANICAL.get())
            .pattern(" RI")
            .pattern("CER")
            .pattern("CC ")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('E', ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC.get())
            .define('C', DSTags.itemCoilCopper)
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .save(con);
        })
        .addCondition(RecipeConditions.GEAR_EXISTS)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.GRAPPLING_HOOK_ENGINE_MECHANICAL.get())
            .pattern("CRI")
            .pattern("GER")
            .pattern("CGC")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('E', ModItems.GRAPPLING_HOOK_ENGINE_ELECTRIC.get())
            .define('C', DSTags.itemCoilCopper)
            .define('R', Tags.Items.DUSTS_REDSTONE)
            .define('G', DSTags.itemGear)
            .save(con);
        })
        .build(consumer, ModItems.GRAPPLING_HOOK_ENGINE_MECHANICAL.getId());

        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.PNEUMATICCRAFT_LOADED)
        .addRecipe((con) -> {
            Recipe.shaped(ModItems.GRAPPLING_HOOK_ENGINE_PRESSURE.get())
            .pattern("TCC")
            .pattern("CRC")
            .pattern(" C ")
            .define('T', me.desht.pneumaticcraft.common.core.ModItems.AIR_CANISTER.get())
            .define('C', DSTags.itemIngotCompressedIron)
            .define('R', me.desht.pneumaticcraft.common.core.ModItems.TURBINE_ROTOR.get())
            .save(con);
        })
        .build(consumer, ModItems.GRAPPLING_HOOK_ENGINE_PRESSURE.getId());

        Recipe.shaped(ModBlocks.INPUT_HATCH_PROXY.get())
            .pattern("ICI")
            .pattern("CHC")
            .pattern("ICI")
            .define('I', DSTags.itemCoilIron)
            .define('C', ModItems.COMPONENT_SMART_ALLOY.get())
            .define('H', Blocks.HOPPER)
            .save(consumer);

        ConditionalRecipe.builder()
        .addCondition(RecipeConditions.GEAR_TAG_EMPTY)
        .addRecipe((con) -> {
            Recipe.shaped(ModBlocks.INPUT_HATCH_PARALLEL.get())
            .pattern("SPS")
            .pattern("SHS")
            .pattern("SPS")
            .define('S', DSTags.itemIngotSmartAlloy)
            .define('P', Items.PISTON)
            .define('H', ModBlocks.INPUT_HATCH_PROXY.get())
            .save(con);
        })
        .addCondition(RecipeConditions.GEAR_EXISTS)
        .addRecipe((con) -> {
            Recipe.shaped(ModBlocks.INPUT_HATCH_PARALLEL.get())
            .pattern("GGG")
            .pattern("SHS")
            .pattern("GGG")
            .define('S', DSTags.itemIngotSmartAlloy)
            .define('G', DSTags.itemGear)
            .define('H', ModBlocks.INPUT_HATCH_PROXY.get())
            .save(con);
        })
        .build(consumer, ModBlocks.INPUT_HATCH_PARALLEL.getId());


        Recipe.shapeless(ModBlocks.INPUT_HATCH_PARALLEL_HEAT.get())
            .requires(ModBlocks.INPUT_HATCH_PARALLEL.get())
            .requires(ModBlocks.HEAT_PIPE_BLOCK.get())
            .save(consumer);

        Recipe.shapeless(ModBlocks.INPUT_HATCH_SERIAL_HEAT.get())
            .requires(ModBlocks.INPUT_HATCH_SERIAL.get())
            .requires(ModBlocks.HEAT_PIPE_BLOCK.get())
            .save(consumer);

        Recipe.shapeless(ModBlocks.INPUT_HATCH_PROXY_HEAT.get())
            .requires(ModBlocks.INPUT_HATCH_PROXY.get())
            .requires(ModBlocks.HEAT_PIPE_BLOCK.get())
            .save(consumer);

        Recipe.shapeless(ModBlocks.INPUT_HATCH_ENERGY_HEAT.get())
            .requires(ModBlocks.INPUT_HATCH_ENERGY.get())
            .requires(ModBlocks.HEAT_PIPE_BLOCK.get())
            .save(consumer);

        Recipe.shapeless(ModBlocks.INPUT_HATCH_FLUID_HEAT.get())
            .requires(ModBlocks.INPUT_HATCH_FLUID.get())
            .requires(ModBlocks.HEAT_PIPE_BLOCK.get())
            .save(consumer);

        Recipe.shaped(ModItems.WRENCH.get())
            .pattern("I I")
            .pattern(" C ")
            .pattern(" I ")
            .define('I', DSTags.itemIngotSmartAlloy)
            .define('C', DSTags.itemCoilCopper)
            .save(consumer);

        Recipe.shaped(ModBlocks.SMART_ALLOY_BLOCK.get())
            .pattern("III")
            .pattern("III")
            .pattern("III")
            .define('I', ModItems.INGOT_SMART_ALLOY.get())
            .save(consumer);
        
        Recipe.shapeless(ModItems.INGOT_SMART_ALLOY.get(), 9)
            .requires(ModBlocks.SMART_ALLOY_BLOCK.get())
            .save(consumer, ModItems.INGOT_SMART_ALLOY.getId().withSuffix("_uncompress"));
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
