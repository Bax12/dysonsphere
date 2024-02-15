package de.bax.dysonsphere.items;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.fluids.ModFluids;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
        
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DysonSphere.MODID);
    

    public static final RegistryObject<Item> COIL_COPPER = registerItem("coil_copper");
    public static final RegistryObject<Item> COIL_IRON = registerItem("coil_iron");
    public static final RegistryObject<Item> THERMOPILE = registerItem("thermopile");
    public static final RegistryObject<Item> SOLAR_FOIL = registerItem("solar_foil");
    public static final RegistryObject<Item> CAPSULE_EMPTY = registerItem("capsule_empty");
    public static final RegistryObject<Item> CAPSULE_SOLAR = registerItem("capsule_solar");
    public static final RegistryObject<Item> HEAT_SHIELDING = registerItem("heat_shielding");
    public static final RegistryObject<Item> RAILGUN = registerItem("railgun");
    public static final RegistryObject<Item> STEAM_BUCKET = ITEMS.register("bucket_steam", () -> new BucketItem(ModFluids.STEAM, new Item.Properties()));



    public static RegistryObject<Item> registerItem(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

}
