package de.bax.dysonsphere.datagen.client.model;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.items.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

public class ItemModelGenerator extends ItemModelProvider{

    public ItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DysonSphere.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        Set<RegistryObject<Item>> complexItems = ImmutableSet.of(
            ModItems.CAPSULE_SOLAR,
            ModItems.CAPSULE_EMPTY,
            ModItems.CAPSULE_LASER,
            ModItems.RAILGUN,
            ModItems.TARGET_DESIGNATOR,
            ModItems.LASER_CONTROLLER
        );

        Set<Item> complexBlocks = ImmutableSet.of(
            ModBlocks.RAILGUN_BLOCK.get().asItem()
        );

        
        ModItems.ITEMS.getEntries().stream().filter(i -> !complexItems.contains(i)).forEach(this::simpleItem);
        ModBlocks.ITEM_BLOCKS.getEntries().stream().filter(i -> !complexBlocks.contains(i.get())).forEach(this::simpleBlock);
    }
    

    protected void simpleItem(RegistryObject<Item> item){
        basicItem(item.get());
    }

    protected void simpleBlock(RegistryObject<Item> block){
        String path = block.getId().getPath();
        getBuilder(path).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
    }
}
