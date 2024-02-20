package de.bax.dysonsphere.datagen.server;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.tags.DSTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTagGenerator extends ItemTagsProvider {

    public ItemTagGenerator(PackOutput output, CompletableFuture<Provider> provider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, blockTags, DysonSphere.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider provider) {
        tag(DSTags.itemOrbitCapsule).add(ModItems.CAPSULE_SOLAR.get(), ModItems.CAPSULE_EMPTY.get());
    }
    
    

}
