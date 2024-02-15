package de.bax.dysonsphere.datagen.server;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.fluids.ModFluids;
import de.bax.dysonsphere.tags.DSTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class FluidTagGenerator extends FluidTagsProvider{

    public FluidTagGenerator(PackOutput output, CompletableFuture<Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, DysonSphere.MODID, existingFileHelper);
    }
    

    @Override
    protected void addTags(Provider provider) {
        tag(DSTags.fluidSteam).add(ModFluids.STEAM.get());
    }


}
