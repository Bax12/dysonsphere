package de.bax.dysonsphere.fluids;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, DysonSphere.MODID);

    public static final RegistryObject<Fluid> STEAM = FLUIDS.register("steam", () -> new SteamFluid());

}
