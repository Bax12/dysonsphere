package de.bax.dysonsphere.schematic;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * SchematicRecipe - not a real recipe in the sense of minecraft, so no id and not registered
 * Just a container to map inputs to outputs for a schematic
 */
public record SchematicRecipe(ItemStack output, List<Ingredient> inputs) {}
