package de.bax.dysonsphere.util;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class SerializationUtil {

    public static FluidStack deserializeFluidStack(JsonObject json) {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(json.get("fluid").getAsString()));
        int amount = json.get("amount").getAsInt();
        return new FluidStack(fluid, amount);
    }

    public static JsonObject serializeItemStack(ItemStack stack) {
        JsonObject json = new JsonObject();

        json.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
        if (stack.hasTag()) {
            json.addProperty("tag", stack.getTag().getAsString());
        }
        if (stack.getCount() > 1) {
            json.addProperty("count", stack.getCount());
        }

        return json;
    }

    public static JsonObject serializeFluidStack(FluidStack stack) {
        JsonObject json = new JsonObject();

        json.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
        json.addProperty("amount", stack.getAmount());
        return json;
    }
}
