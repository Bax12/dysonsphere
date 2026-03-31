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

    public static final RegistryObject<RecipeType<OrbitalLaunchRecipe>> ORBITAL_LAUNCH_TYPE = TYPES.register("orbital_launch", () -> RecipeType.simple(new ResourceLocation(DysonSphere.MODID, "orbital_launch")));
    public static final RegistryObject<RecipeSerializer<OrbitalLaunchRecipe>> ORBITAL_LAUNCH_SERIALIZER = SERIALIZERS.register("orbital_launch", OrbitalLaunchRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<ConstructRecipe>> CONSTRUCT_TYPE = TYPES.register("construct", () -> RecipeType.simple(new ResourceLocation(DysonSphere.MODID, "construct")));
    public static final RegistryObject<RecipeSerializer<ConstructRecipe>> CONSTRUCT_SERIALIZER = SERIALIZERS.register("construct", ConstructRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<CargoDeliveryRecipe>> CARGO_DELIVERY_TYPE  = TYPES.register("cargo_delivery", () -> RecipeType.simple(new ResourceLocation(DysonSphere.MODID, "cargo_delivery")));
    public static final RegistryObject<RecipeSerializer<CargoDeliveryRecipe>> CARGO_DELIVERY_SERIALIZER = SERIALIZERS.register("cargo_delivery", CargoDeliveryRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<ListenerRecipe>> LISTENER_TYPE  = TYPES.register("listener", () -> RecipeType.simple(new ResourceLocation(DysonSphere.MODID, "listener")));
    public static final RegistryObject<RecipeSerializer<ListenerRecipe>> LISTENER_SERIALIZER = SERIALIZERS.register("listener", ListenerRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<AnnihilationRecipe>> ANNIHILATION_TYPE = TYPES.register("matter_annihilation", () -> RecipeType.simple(new ResourceLocation(DysonSphere.MODID, "matter_annihilation")));
    public static final RegistryObject<RecipeSerializer<AnnihilationRecipe>> ANNIHILATION_SERIALIZER = SERIALIZERS.register("matter_annihilation", AnnihilationRecipe.Serializer::new);
}
