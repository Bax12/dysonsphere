package de.bax.dysonsphere.fluids;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.items.ModItems;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, DysonSphere.MODID);

    public static final RegistryObject<Fluid> STEAM = FLUIDS.register("steam", () -> new BasicGaseousFluid(ModItems.STEAM_BUCKET, "block/steam_still", "fluid.dysonsphere.steam", 450));
    public static final RegistryObject<Fluid> HELIUM = FLUIDS.register("helium", () -> new BasicGaseousFluid(ModItems.HELIUM_BUCKET, "block/helium_still", "fluid.dysonsphere.helium", 290));

}
