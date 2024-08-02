package de.bax.dysonsphere.recipes;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, DysonSphere.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, DysonSphere.MODID);

    public static final RegistryObject<RecipeType<LaserCraftingRecipe>> LASER_CRAFTING_TYPE = TYPES.register("laser_crafting", () -> RecipeType.simple(new ResourceLocation(DysonSphere.MODID, "laser_crafting")));
    public static final RegistryObject<RecipeSerializer<LaserCraftingRecipe>> LASER_CRAFTING_SERIALIZER = SERIALIZERS.register("laser_crafting", LaserCraftingRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<HeatExchangerRecipe>> HEAT_EXCHANGER_TYPE = TYPES.register("heat_exchanger", () -> RecipeType.simple(new ResourceLocation(DysonSphere.MODID, "heat_exchanger")));
    public static final RegistryObject<RecipeSerializer<HeatExchangerRecipe>> HEAT_EXCHANGER_SERIALIZER = SERIALIZERS.register("heat_exchanger", HeatExchangerRecipe.Serializer::new);
}
