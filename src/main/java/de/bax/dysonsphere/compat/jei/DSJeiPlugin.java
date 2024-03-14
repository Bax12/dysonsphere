package de.bax.dysonsphere.compat.jei;

import java.util.List;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.fluids.ModFluids;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.tileentities.HeatExchangerTile;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

@JeiPlugin
public class DSJeiPlugin implements IModPlugin {

    public static IJeiHelpers helpers;
    public static IGuiHelper guiHelper;

    public DSJeiPlugin(){
        DysonSphere.LOGGER.info("DSJeiPlugin Launched");
    }

    public static final RailgunRecipeCategory RAILGUN = new RailgunRecipeCategory();
    public static final HeatExchangerCategory HEAT_EXCHANGER = new HeatExchangerCategory();

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(DysonSphere.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        helpers = registration.getJeiHelpers();
        guiHelper = helpers.getGuiHelper();
        registration.addRecipeCategories(RAILGUN);
        registration.addRecipeCategories(HEAT_EXCHANGER);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.RAILGUN_BLOCK.get().asItem().getDefaultInstance(), RAILGUN.getRecipeType());
        registration.addRecipeCatalyst(ModBlocks.HEAT_EXCHANGER_BLOCK.get().asItem().getDefaultInstance(), HEAT_EXCHANGER.getRecipeType());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(RAILGUN.getRecipeType(), List.of(new RailgunRecipeCategory.RailgunRecipe(ModItems.CAPSULE_SOLAR.get().getDefaultInstance())));
        registration.addRecipes(HEAT_EXCHANGER.getRecipeType(), List.of(new HeatExchangerCategory.HeatExchangerRecipe(new FluidStack(Fluids.WATER, 5), new FluidStack(ModFluids.STEAM.get(), 50), HeatExchangerTile.minHeat)));
    }
    
}
