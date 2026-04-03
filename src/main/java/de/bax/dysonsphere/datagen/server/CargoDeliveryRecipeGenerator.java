package de.bax.dysonsphere.datagen.server;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.constructs.Construct;
import de.bax.dysonsphere.constructs.ModConstructs;
import de.bax.dysonsphere.fluids.ModFluids;
import de.bax.dysonsphere.recipes.ModRecipes;
import de.bax.dysonsphere.util.SerializationUtil;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CargoDeliveryRecipeGenerator {
    
    public static final String basePath = "cargo_delivery";

    public static void buildRecipes(Consumer<FinishedRecipe> pWriter){
        RecipeBuilder.of(ModFluids.HELIUM, 250).energy(100_000).addRequiredConstruct(ModConstructs.CARGO_LAUNCHER.get()).addRequiredConstruct(ModConstructs.HELIUM_MINE).save(pWriter);
    }

    public static class RecipeBuilder {
        private int energy;
        private FluidStack fluidOutput;
        private ItemStack itemOutput;
        private List<Construct> requiredConstructs = new ArrayList<>();

        private RecipeBuilder(ItemStack itemOutput){
            this.itemOutput = itemOutput;
        }

        private RecipeBuilder(FluidStack fluidOutput){
            this.fluidOutput = fluidOutput;
        }

        public static RecipeBuilder of(ItemStack itemOutput){
            return new RecipeBuilder(itemOutput);
        }

        public static RecipeBuilder of(ItemLike itemOutput){
            return new RecipeBuilder(new ItemStack(itemOutput.asItem()));
        }

        public static RecipeBuilder of(ItemLike itemOutput, int amount){
            return new RecipeBuilder(new ItemStack(itemOutput.asItem(), amount));
        }

        public static RecipeBuilder of(RegistryObject<Item> itemOutput){
            return new RecipeBuilder(new ItemStack(itemOutput.get()));
        }

        public  static RecipeBuilder of(RegistryObject<?> output, int amount){
            if(output.get() instanceof Item item){
                return new RecipeBuilder(new ItemStack(item, amount));
            } else if (output.get() instanceof Fluid fluid){
                return new RecipeBuilder(new FluidStack(fluid, amount));
            }
            return null;
        }

        public static RecipeBuilder of(FluidStack fluidOutput){
            return new RecipeBuilder(fluidOutput);
        }

        public static RecipeBuilder of(Fluid fluidOutput, int amount){
            return new RecipeBuilder(new FluidStack(fluidOutput, amount));
        }

        public RecipeBuilder energy(int energy){
            this.energy = energy;
            return this;
        }

        public RecipeBuilder addRequiredConstruct(Construct construct){
            requiredConstructs.add(construct);
            return this;
        }

        public RecipeBuilder addRequiredConstruct(RegistryObject<Construct> construct){
            requiredConstructs.add(construct.get());
            return this;
        }

        public void save(Consumer<FinishedRecipe> consumer){
            if(itemOutput != null){
                this.save(consumer, ForgeRegistries.ITEMS.getKey(itemOutput.getItem()));
            } else if(fluidOutput != null){
                this.save(consumer, ForgeRegistries.FLUIDS.getKey(fluidOutput.getFluid()));
            }
        }

        public void save(Consumer<FinishedRecipe> consumer, ResourceLocation location){
            this.save(consumer, location.getPath());
        }

        public void save(Consumer<FinishedRecipe> consumer, String location){
            consumer.accept(new Recipe(getLocation(location), energy, fluidOutput, itemOutput, requiredConstructs));
        }


        public static ResourceLocation getLocation(ResourceLocation location){
            return getLocation(location.getPath());
        }

        public static ResourceLocation getLocation(String path){
            return new ResourceLocation(DysonSphere.MODID, basePath + "/" + path);
        }

    }

    public static record Recipe(ResourceLocation id, int energy, FluidStack fluidOutput, ItemStack itemOutput, List<Construct> requiredConstructs) implements FinishedRecipe {

        @Override
        public void serializeRecipeData(@Nonnull JsonObject pJson) {
            pJson.addProperty("energy", energy());
            if(fluidOutput() != null){
                pJson.add("fluidOutput", SerializationUtil.serializeFluidStack(fluidOutput()));
            }
            if(itemOutput() != null){
                pJson.add("itemOutput", SerializationUtil.serializeItemStack(itemOutput()));
            }
            JsonArray conJson = new JsonArray();
            for (Construct con : requiredConstructs()) {
                conJson.add(con.toJson());
            }
            if(!conJson.isEmpty()){
                pJson.add("requiredConstructs", conJson);
            }
        }

        @Override
        public ResourceLocation getId() {
            return id();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipes.CARGO_DELIVERY_SERIALIZER.get();
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
