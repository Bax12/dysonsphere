package de.bax.dysonsphere.containers;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainers {
    
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, DysonSphere.MODID);


    public static final RegistryObject<MenuType<RailgunContainer>> RAILGUN_CONTAINER = CONTAINERS.register("railgun_container", () -> IForgeMenuType.create(RailgunContainer::fromNetwork));
    public static final RegistryObject<MenuType<DSEnergyReceiverContainer>> DS_ENERGY_RECEIVER_CONTAINER = CONTAINERS.register("ds_energy_receiver_container", () -> IForgeMenuType.create(DSEnergyReceiverContainer::fromNetwork));
    public static final RegistryObject<MenuType<HeatGeneratorContainer>> HEAT_GENERATOR = CONTAINERS.register("heat_generator_container", () -> IForgeMenuType.create(HeatGeneratorContainer::fromNetwork));
    public static final RegistryObject<MenuType<HeatExchangerContainer>> HEAT_EXCHANGER = CONTAINERS.register("heat_exchanger_container", () -> IForgeMenuType.create(HeatExchangerContainer::fromNetwork));
}
