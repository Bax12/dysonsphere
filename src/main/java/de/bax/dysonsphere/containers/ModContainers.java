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
    public static final RegistryObject<MenuType<HeatGeneratorContainer>> HEAT_GENERATOR_CONTAINER = CONTAINERS.register("heat_generator_container", () -> IForgeMenuType.create(HeatGeneratorContainer::fromNetwork));
    public static final RegistryObject<MenuType<HeatExchangerContainer>> HEAT_EXCHANGER_CONTAINER = CONTAINERS.register("heat_exchanger_container", () -> IForgeMenuType.create(HeatExchangerContainer::fromNetwork));
    public static final RegistryObject<MenuType<LaserPatternControllerContainer>> LASER_PATTERN_CONTROLLER_CONTAINER = CONTAINERS.register("laser_pattern_controller_container", () -> IForgeMenuType.create(LaserPatternControllerContainer::fromNetwork));
    public static final RegistryObject<MenuType<LaserControllerInventoryContainer>> LASER_CONTROLLER_INVENTORY_CONTAINER = CONTAINERS.register("laser_controller_inventory_container", () -> IForgeMenuType.create(LaserControllerInventoryContainer::fromNetwork));
    public static final RegistryObject<MenuType<LaserPatternControllerInventoryContainer>> LASER_PATTERN_CONTROLLER_INVENTORY_CONTAINER = CONTAINERS.register("laser_pattern_controller_inventory_container", () -> IForgeMenuType.create(LaserPatternControllerInventoryContainer::fromNetwork));
    public static final RegistryObject<MenuType<LaserControllerContainer>> LASER_CONTROLLER_CONTAINER = CONTAINERS.register("laser_controller_container", () -> IForgeMenuType.create(LaserControllerContainer::fromNetwork));
    public static final RegistryObject<MenuType<GrapplingHookHarnessInventoryContainer>> GRAPPLING_HOOK_HARNESS_INVENTORY_CONTAINER = CONTAINERS.register("grappling_hook_harness_inventory_container", () -> IForgeMenuType.create(GrapplingHookHarnessInventoryContainer::fromNetwork));
    public static final RegistryObject<MenuType<InputHatchSerialContainer>> INPUT_HATCH_SERIAL_CONTAINER = CONTAINERS.register("input_hatch_serial_container", () -> IForgeMenuType.create(InputHatchSerialContainer::fromNetwork));
    public static final RegistryObject<MenuType<InputHatchParallelContainer>> INPUT_HATCH_PARALLEL_CONTAINER = CONTAINERS.register("input_hatch_parallel_container", () -> IForgeMenuType.create(InputHatchParallelContainer::fromNetwork));
    public static final RegistryObject<MenuType<InputHatchEnergyContainer>> INPUT_HATCH_ENERGY_CONTAINER = CONTAINERS.register("input_hatch_energy_container", () -> IForgeMenuType.create(InputHatchEnergyContainer::fromNetwork));
    public static final RegistryObject<MenuType<InputHatchFluidContainer>> INPUT_HATCH_FLUID_CONTAINER = CONTAINERS.register("input_hatch_fluid_container", () -> IForgeMenuType.create(InputHatchFluidContainer::fromNetwork));
}
