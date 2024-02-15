package de.bax.dysonsphere.tabs;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DysonSphere.MODID);

    public static final RegistryObject<CreativeModeTab> DYSONSPHERE_TAB = CREATIVE_MODE_TABS.register("dysonsphere_tab", () -> CreativeModeTab.builder()
            .withLabelColor(14)
            .title(Component.translatable("itemGroup.dysonsphere_tab"))
            .icon(() -> ModItems.COIL_COPPER.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                for (RegistryObject<Item> item : ModItems.ITEMS.getEntries()){
                    output.accept(item.get());
                }
                for (RegistryObject<Item> item : ModBlocks.ITEM_BLOCKS.getEntries()){
                    output.accept(item.get());
                }
                
            })
            .build());

}
