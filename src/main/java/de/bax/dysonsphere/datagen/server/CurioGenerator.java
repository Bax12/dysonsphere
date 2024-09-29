package de.bax.dysonsphere.datagen.server;

import java.util.concurrent.CompletableFuture;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

public class CurioGenerator extends CuriosDataProvider {

    public CurioGenerator(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<Provider> registries) {
        super(DysonSphere.MODID, output, fileHelper, registries);
    }

    @Override
    public void generate(Provider registries, ExistingFileHelper fileHelper) {
        this.createEntities("player").addPlayer().addSlots("belt", "back", "hands", "bracelet");
    }
    
}
