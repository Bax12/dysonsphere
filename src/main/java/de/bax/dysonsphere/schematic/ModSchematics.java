package de.bax.dysonsphere.schematic;

import java.util.function.Supplier;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.schematic.baseElements.BasicElement;
import de.bax.dysonsphere.schematic.baseElements.Input;
import de.bax.dysonsphere.schematic.baseElements.Output;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class ModSchematics {
    
    public static final DeferredRegister<ISchematicElement> SCHEMATIC_ELEMENTS = DeferredRegister.create(new ResourceLocation(DysonSphere.MODID, "schematic_element"), DysonSphere.MODID);
    protected static final Supplier<IForgeRegistry<ISchematicElement>> REGISTRY = SCHEMATIC_ELEMENTS.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<ISchematicElement> INPUT = SCHEMATIC_ELEMENTS.register("input", () -> new Input());
    public static final RegistryObject<ISchematicElement> OUTPUT = SCHEMATIC_ELEMENTS.register("output", () -> new Output());
    public static final RegistryObject<ISchematicElement> SCHEMATIC = SCHEMATIC_ELEMENTS.register("schematic", () -> new Schematic());
    public static final RegistryObject<ISchematicElement> AND = SCHEMATIC_ELEMENTS.register("and", () -> new BasicElement.And());
    public static final RegistryObject<ISchematicElement> OR = SCHEMATIC_ELEMENTS.register("or", () -> new BasicElement.Or());
    public static final RegistryObject<ISchematicElement> NAND = SCHEMATIC_ELEMENTS.register("nand", () -> new BasicElement.Nand());
    public static final RegistryObject<ISchematicElement> NOR = SCHEMATIC_ELEMENTS.register("nor", () -> new BasicElement.Nor());
    public static final RegistryObject<ISchematicElement> NOT = SCHEMATIC_ELEMENTS.register("not", () -> new BasicElement.Not());

    public static IForgeRegistry<ISchematicElement> registry(){
        return REGISTRY.get();
    }

}
