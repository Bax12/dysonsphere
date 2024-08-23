package de.bax.dysonsphere.datagen.server;

import de.bax.dysonsphere.compat.ModCompat;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;

public class RecipeConditions {
    
    public static final ICondition PNEUMATICCRAFT_LOADED = new ModLoadedCondition(ModCompat.MODID.PNEUMATICCRAFT.id);

    public static final ICondition NICKEL_TAG_EMPTY = new TagEmptyCondition("forge", "ingots/nickel");
    public static final ICondition TITANIUM_TAG_EMPTY = new TagEmptyCondition("forge", "ingots/titanium");
    public static final ICondition TUNGSTEN_TAG_EMPTY = new TagEmptyCondition("forge", "ingots/tungsten");
    public static final ICondition SIGNALUM_TAG_EMPTY = new TagEmptyCondition("forge", "ingots/signalum");
    public static final ICondition LUMIUM_TAG_EMPTY = new TagEmptyCondition("forge", "ingots/lumium");

    public static final ICondition CIRCUIT_TAG_EMPTY = new TagEmptyCondition("forge", "circuits");
    public static final ICondition GEAR_TAG_EMPTY = new TagEmptyCondition("forge", "gears");

    public static final ICondition WIRE_COPPER_TAG_EMPTY = new TagEmptyCondition("forge", "wires/copper");
    public static final ICondition WIRE_IRON_TAG_EMPTY = new TagEmptyCondition("forge", "wires/iron");

    public static final ICondition NICKEL_EXISTS = new NotCondition(NICKEL_TAG_EMPTY);
    public static final ICondition TITANIUM_EXISTS = new NotCondition(TITANIUM_TAG_EMPTY);
    public static final ICondition TUNGSTEN_EXISTS = new NotCondition(TUNGSTEN_TAG_EMPTY);
    public static final ICondition SIGNALUM_EXISTS = new NotCondition(SIGNALUM_TAG_EMPTY);
    public static final ICondition LUMIUM_EXISTS = new NotCondition(LUMIUM_TAG_EMPTY);

    public static final ICondition CIRCUIT_EXISTS = new NotCondition(CIRCUIT_TAG_EMPTY);
    public static final ICondition GEAR_EXISTS = new NotCondition(GEAR_TAG_EMPTY);

    public static final ICondition WIRE_COPPER_EXISTS = new NotCondition(WIRE_COPPER_TAG_EMPTY);
    public static final ICondition WIRE_IRON_EXISTS = new NotCondition(WIRE_IRON_TAG_EMPTY);

}
