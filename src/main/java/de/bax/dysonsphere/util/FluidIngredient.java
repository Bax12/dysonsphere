package de.bax.dysonsphere.util;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;

public class FluidIngredient implements Predicate<FluidStack> {

    protected int amount;
    protected ResourceLocation fluidLocation;
    protected List<Fluid> fluids;

    protected FluidIngredient(ResourceLocation fluidLocation, int amount){
        this.amount = amount;
        this.fluidLocation = fluidLocation;
    }

    protected void initFluids(){
        TagKey<Fluid> fluidKey = TagKey.create(Registries.FLUID, fluidLocation); 
        if(ForgeRegistries.FLUIDS.tags().isKnownTagName(fluidKey)){//check fluidTags first in case a name overlaps (e.g. minecraft:water)
            ITag<Fluid> fluidTag = ForgeRegistries.FLUIDS.tags().getTag(fluidKey); //but what if I need a specific fluid that happens to have a tag of the same name? - You don't.
            fluids = ImmutableList.copyOf(fluidTag);
        } else {
            fluids = ImmutableList.of(ForgeRegistries.FLUIDS.getValue(fluidLocation));
        }
    }

    @Override
    public boolean test(FluidStack fluidStack) {
        if(amount > fluidStack.getAmount()) { //a to small fluid input never matches.
            return false;
        }
        if(fluids == null){
            initFluids();
        }
        return fluids.contains(fluidStack.getFluid());
    }

    public int getAmount(){
        return amount;
    }

    public List<Fluid> getFluids(){
        if(fluids == null){
            initFluids();
        }
        return fluids;
    }
    
    public static FluidIngredient of(FluidStack fluidStack){
        return new FluidIngredient(ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()), fluidStack.getAmount());
    }

    public static FluidIngredient of(Fluid fluid, int amount){
        return new FluidIngredient(ForgeRegistries.FLUIDS.getKey(fluid), amount);
    }

    public static FluidIngredient of(TagKey<Fluid> tag, int amount){
        return new FluidIngredient(tag.location(), amount);
    }

    public void writeToPacket(@Nonnull FriendlyByteBuf buffer){
        buffer.writeResourceLocation(fluidLocation);
        buffer.writeInt(amount);
    }

    public static FluidIngredient readFromPacket(@Nonnull FriendlyByteBuf buffer){
        ResourceLocation location = buffer.readResourceLocation();
        int amount = buffer.readInt();
        return new FluidIngredient(location, amount);
    }

    public JsonElement toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("amount", amount);
        json.addProperty("fluid", fluidLocation.toString());
        return json;
    }

    public static FluidIngredient fromJson(JsonObject json){
        ResourceLocation location = new ResourceLocation(json.get("fluid").getAsString());
        int amount = json.get("amount").getAsInt();
        return new FluidIngredient(location, amount);
    }

}
