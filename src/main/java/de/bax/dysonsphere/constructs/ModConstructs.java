package de.bax.dysonsphere.constructs;

import java.util.function.Supplier;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class ModConstructs {
    
    public static final DeferredRegister<Construct> CONSTRUCTS = DeferredRegister.createOptional(new ResourceLocation(DysonSphere.MODID, "construct"), DysonSphere.MODID);
    protected static final Supplier<IForgeRegistry<Construct>> REGISTRY = CONSTRUCTS.makeRegistry(RegistryBuilder::new);

    public static final RegistryObject<Construct> HELIUM_MINE = CONSTRUCTS.register("helium_mine", () -> new Construct()); //harvest liquid helium
    public static final RegistryObject<Construct> STABILIZER = CONSTRUCTS.register("stabilizer", () -> new Construct()); //reduce ds damage chance
    public static final RegistryObject<Construct> HEAT_SINK = CONSTRUCTS.register("heat_sink", () -> new Construct());  //reduce laser cooldown period
    public static final RegistryObject<Construct> CARGO_LAUNCHER = CONSTRUCTS.register("cargo_launcher", () -> new Construct());  //allows sending material back to world (helium, etc...)
    public static final RegistryObject<Construct> PLASMA_LAUNCHER = CONSTRUCTS.register("plasma_launcher", () -> new Construct()); //allows using plasma projectiles instead of lasers for orbital strikes. Needs helium?
    public static final RegistryObject<Construct> SOLAR_LAMP = CONSTRUCTS.register("solar_lamp", () -> new Construct()); //prevent the sky from darkening with ds progress

    public static IForgeRegistry<Construct> registry(){
        return REGISTRY.get();
    }

    /*todo: 
        - stats (from json?)
        - recipes (consumed components / catalysts?(catalyst launch recipe?))
        - commands (add/remove & enable/disable components)
        - controller & ui (add/remove & enable/disable components)
        - cargo delivery / receiver
        - ds stability & part damage chance
        - plasma projectiles
    All constructs should be unique, some require others to work (both launchers need the mine)
    when working harder, more energy should be used.
    */
}
