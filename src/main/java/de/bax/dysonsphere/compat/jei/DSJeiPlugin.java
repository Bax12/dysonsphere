package de.bax.dysonsphere.compat.jei;

import java.util.Objects;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.gui.HeatExchangerGui;
import de.bax.dysonsphere.gui.ListenerGui;
import de.bax.dysonsphere.gui.RailgunGui;
import de.bax.dysonsphere.recipes.ListenerRecipe;
import de.bax.dysonsphere.recipes.ModRecipes;
import de.bax.dysonsphere.tileentities.ListenerTile;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

@JeiPlugin
public class DSJeiPlugin implements IModPlugin {

    public static IJeiHelpers helpers;
    public static IGuiHelper guiHelper;

    public DSJeiPlugin(){
        DysonSphere.LOGGER.info("DSJeiPlugin Launched");
    }

    public static final RailgunRecipeCategory RAILGUN = new RailgunRecipeCategory();
    public static final HeatExchangerCategory HEAT_EXCHANGER = new HeatExchangerCategory();
    public static final LaserCrafterCategory LASER_CRAFTER = new LaserCrafterCategory();
    public static final ListenerCategory LISTENER = new ListenerCategory();
    public static final CargoDeliveryCategory CARGO_DELIVERY = new CargoDeliveryCategory();
    public static final EnergyConverterCategory ENERGY_CONVERTER = new EnergyConverterCategory();

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(DysonSphere.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registration) {
        helpers = registration.getJeiHelpers();
        guiHelper = helpers.getGuiHelper();
        registration.addRecipeCategories(RAILGUN);
        registration.addRecipeCategories(HEAT_EXCHANGER);
        registration.addRecipeCategories(LASER_CRAFTER);
        registration.addRecipeCategories(LISTENER);
        registration.addRecipeCategories(CARGO_DELIVERY);
        registration.addRecipeCategories(ENERGY_CONVERTER);
    }

    @Override
    public void registerRecipeCatalysts(@Nonnull IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.RAILGUN_BLOCK.get().asItem().getDefaultInstance(), RAILGUN.getRecipeType());
        registration.addRecipeCatalyst(ModBlocks.HEAT_EXCHANGER_BLOCK.get().asItem().getDefaultInstance(), HEAT_EXCHANGER.getRecipeType());
        registration.addRecipeCatalyst(ModBlocks.LASER_CRAFTER_BLOCK.get().asItem().getDefaultInstance(), LASER_CRAFTER.getRecipeType());
        registration.addRecipeCatalyst(ModBlocks.LISTENER_BLOCK.get().asItem().getDefaultInstance(), LISTENER.getRecipeType());
        registration.addRecipeCatalyst(ModBlocks.CARGO_RECEIVER_BLOCK.get().asItem().getDefaultInstance(), CARGO_DELIVERY.getRecipeType());
        registration.addRecipeCatalyst(ModBlocks.ENERGY_CONVERTER_BLOCK.get().asItem().getDefaultInstance(), ENERGY_CONVERTER.getRecipeType());
        registration.addRecipeCatalyst(ModBlocks.INPUT_HATCH_PARALLEL.get().asItem().getDefaultInstance(), LASER_CRAFTER.getRecipeType(), RAILGUN.getRecipeType());
        registration.addRecipeCatalyst(ModBlocks.INPUT_HATCH_SERIAL.get().asItem().getDefaultInstance(), LASER_CRAFTER.getRecipeType(), RAILGUN.getRecipeType());
        registration.addRecipeCatalyst(ModBlocks.INPUT_HATCH_FLUID.get().asItem().getDefaultInstance(), RAILGUN.getRecipeType());
        registration.addRecipeCatalyst(ModBlocks.INPUT_HATCH_ENERGY.get().asItem().getDefaultInstance(), RAILGUN.getRecipeType(), ENERGY_CONVERTER.getRecipeType());
    }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        // registration.addRecipes(RAILGUN.getRecipeType(), List.of(new RailgunRecipeCategory.RailgunRecipe(ModItems.CAPSULE_SOLAR_0.get().getDefaultInstance()), new RailgunRecipeCategory.RailgunRecipe(ModItems.CAPSULE_LASER_0.get().getDefaultInstance())));
        // registration.addRecipes(HEAT_EXCHANGER.getRecipeType(), List.of(new HeatExchangerCategory.HeatExchangerRecipe(new FluidStack(Fluids.WATER, 5), new FluidStack(ModFluids.STEAM.get(), 50), HeatExchangerTile.minHeat)));
        registration.addRecipes(RAILGUN.getRecipeType(), recipeManager.getAllRecipesFor(ModRecipes.ORBITAL_LAUNCH_TYPE.get()));
        registration.addRecipes(HEAT_EXCHANGER.getRecipeType(), recipeManager.getAllRecipesFor(ModRecipes.HEAT_EXCHANGER_TYPE.get()));
        registration.addRecipes(LASER_CRAFTER.getRecipeType(), recipeManager.getAllRecipesFor(ModRecipes.LASER_CRAFTING_TYPE.get()));
        registration.addRecipes(LISTENER.getRecipeType(), ListenerRecipe.getRecipes(Minecraft.getInstance().level));
        registration.addRecipes(CARGO_DELIVERY.getRecipeType(), recipeManager.getAllRecipesFor(ModRecipes.CARGO_DELIVERY_TYPE.get()));
        registration.addRecipes(ENERGY_CONVERTER.getRecipeType(), EnergyConverterCategory.getAllRecipes());
    }

    @Override
    public void registerGuiHandlers(@Nonnull IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(RailgunGui.class, 78, 21, 22, 39, RAILGUN.getRecipeType());
        registration.addRecipeClickArea(HeatExchangerGui.class, 36, 39, 41, 15, HEAT_EXCHANGER.getRecipeType());
        registration.addRecipeClickArea(HeatExchangerGui.class, 98, 39, 42, 15, HEAT_EXCHANGER.getRecipeType());
        registration.addRecipeClickArea(ListenerGui.class, 83, 44, 57, 13, LISTENER.getRecipeType());
    }
    
}
