package de.bax.dysonsphere.datagen.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.bax.dysonsphere.constructs.Construct;
import de.bax.dysonsphere.constructs.Construct.ComponentCount;
import de.bax.dysonsphere.constructs.ModConstructs;
import de.bax.dysonsphere.tags.DSTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ConstructGenerator implements DataProvider{
    
    public static final String basePath = "constructs";

    private final PackOutput.PathProvider pathProvider;
    private final ExistingFileHelper fileHelper;
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final List<Construct> constructs; 

    public ConstructGenerator(PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> registries){
        this.pathProvider = output.createPathProvider(Target.DATA_PACK, basePath);
        this.fileHelper = fileHelper;
        this.registries = registries;
        this.constructs = new ArrayList<>();
    } 

    public void buildConstructs(){
        // output.createPathProvider(PackOutput.Target.DATA_PACK, DysonSphere.MODID + "/" + basePath).file(null, ".json").toFile()
        // output.getOutputFolder();

        Construct con = ModConstructs.HELIUM_MINE.get();
        con.energy = -5000;
        con.stability = 0.85f;
        con.tier = 0;
        con.components.put(Ingredient.of(DSTags.itemCapsuleStructure), new ComponentCount(30, 10));
        con.components.put(Ingredient.of(DSTags.itemCapsuleLaser), new ComponentCount(20, 10));
        con.components.put(Ingredient.of(DSTags.itemCapsuleSolar), new ComponentCount(50, 10));
        constructs.add(con);

        con = ModConstructs.STABILIZER.get();
        con.energy = -250;
        con.stability = 2.5f;
        con.tier = 1;
        con.components.put(Ingredient.of(DSTags.itemCapsuleStructure), new ComponentCount(50, 50));
        con.components.put(Ingredient.of(DSTags.itemCapsuleLaser), new ComponentCount(50, 10));
        constructs.add(con);

        con = ModConstructs.HEAT_SINK.get();
        con.energy = 0;
        con.stability = 0.95f;
        con.tier = 0;
        con.components.put(Ingredient.of(DSTags.itemCapsuleStructure), new ComponentCount(200, 50));
        constructs.add(con);

        con = ModConstructs.CARGO_LAUNCHER.get();
        con.energy = -50;
        con.stability = 0.99f;
        con.tier = 0;
        con.components.put(Ingredient.of(DSTags.itemCapsuleStructure), new ComponentCount(20, 0));
        con.components.put(Ingredient.of(DSTags.itemCapsuleSolar), new ComponentCount(500, 100));
        constructs.add(con);

        con = ModConstructs.PLASMA_LAUNCHER.get();
        con.energy = -1000;
        con.stability = 0.85f;
        con.tier = 1;
        con.components.put(Ingredient.of(DSTags.itemCapsuleStructure), new ComponentCount(200, 100));
        con.components.put(Ingredient.of(DSTags.itemCapsuleLaser), new ComponentCount(500, 100));
        con.components.put(Ingredient.of(DSTags.itemCapsuleSolar), new ComponentCount(500, 200));
        constructs.add(con);

        con = ModConstructs.SOLAR_LAMP.get();
        con.energy = -750;
        con.stability = 1f;
        con.tier = 1;
        con.components.put(Ingredient.of(DSTags.itemCapsuleStructure), new ComponentCount(200, 200));
        con.components.put(Ingredient.of(DSTags.itemCapsuleLaser), new ComponentCount(20, 0));
        constructs.add(con);
    }

    @Override
    public CompletableFuture<?> run(@Nonnull CachedOutput pOutput) {
        buildConstructs();
        List<CompletableFuture<?>> list = new ArrayList<>();

        for (Construct con : constructs) {
            list.add(DataProvider.saveStable(pOutput, serializeConstruct(con), pathProvider.json(con.getResourceLocation())));    
        }
        

        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "DysonSphere Constructs";
    }

    protected JsonElement serializeConstruct(Construct construct){
        JsonObject json = new JsonObject();
        json.addProperty("Tier", construct.tier);
        json.addProperty("Energy", construct.energy);
        json.addProperty("Stability", construct.stability);
        JsonArray componentsJson = new JsonArray();
        construct.components.forEach((component, count) -> {
            JsonObject compJson = new JsonObject();
            compJson.add("Component", component.toJson());
            compJson.addProperty("Required", count.required());
            compJson.addProperty("Foundation", count.foundation());
            componentsJson.add(compJson);
        });
        json.add("Components", componentsJson);
        return json;
    }

}
