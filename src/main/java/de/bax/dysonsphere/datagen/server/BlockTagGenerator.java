package de.bax.dysonsphere.datagen.server;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockTagGenerator extends BlockTagsProvider {

    public BlockTagGenerator(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, DysonSphere.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider provider) {
        
    }
    
}
