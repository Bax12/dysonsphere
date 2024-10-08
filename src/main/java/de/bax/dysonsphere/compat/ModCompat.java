package de.bax.dysonsphere.compat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.bax.dysonsphere.compat.ad_astra.AdAstra;
import de.bax.dysonsphere.compat.curio.Curios;
import de.bax.dysonsphere.compat.mekanism.Mekanism;
import de.bax.dysonsphere.compat.pneumaticcraft.Pneumaticcraft;
import net.minecraftforge.fml.ModList;

public class ModCompat {
    

    public static enum MODID {
        MEKANISM("mekanism", () -> new Mekanism()),
        PNEUMATICCRAFT("pneumaticcraft", () -> new Pneumaticcraft()),
        JADE("jade", () -> new IModCompat(){}),
        CURIOS("curios", () -> new Curios()),
        AD_ASTRA("ad_astra", () -> new AdAstra());

        public String id;
        public Supplier<IModCompat> compat;
        MODID(String modid, Supplier<IModCompat> compat){
            this.id = modid;
            this.compat = compat;
        }
    }

    protected static List<IModCompat> loadedCompats = new ArrayList<>();

    public static void init(){
        for(MODID mod : MODID.values()){
            if(ModList.get().isLoaded(mod.id)){
                loadedCompats.add(mod.compat.get());
            }
        }


        for(IModCompat compat : loadedCompats){
            compat.init();
        }
    }


    public static boolean isLoaded(MODID mod){
        return ModList.get().isLoaded(mod.id);
    }

}
