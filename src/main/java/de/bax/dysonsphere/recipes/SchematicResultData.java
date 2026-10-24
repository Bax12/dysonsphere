package de.bax.dysonsphere.recipes;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class SchematicResultData extends SavedData {

    public static final String DATA_NAME = "dysonsphere_schematic_values";
    public static final String TAG_NAME = "recipes";

    protected Map<ResourceLocation, int[]> values = new HashMap<>();

    @Override
    public CompoundTag save(@Nonnull CompoundTag tag) {

        values.forEach((id, outputs) -> {
            tag.putIntArray(id.toString(), outputs);//compoundTag cannot save short[], so we save two short[] as int[].
            //the high bit short[] is the dont-care mask, the low bit short[] the output values
        });
        
        return tag;
    }

    public static SchematicResultData load(CompoundTag tag){
        SchematicResultData data = new SchematicResultData();

        CompoundTag recipes = tag.getCompound(TAG_NAME);

        for (String key : recipes.getAllKeys()){
            ResourceLocation id = new ResourceLocation(key);
            data.values.put(id, tag.getIntArray(key));
        }

        return data;

    }

    public int[] getOrCreate(SchematicResultRecipe recipe, ServerLevel level){
        int[] outputs = values.get(recipe.id());

        if(outputs != null) return outputs.clone();
        
        int inputCount = 1 << recipe.inputCount();
        outputs = new int[inputCount];

        //should be deterministic enough
        Random rand = new Random(level.getSeed() + recipe.hashCode());

        for(int i = 0; i < inputCount; i++){
            outputs[i] = 0;
            int dcCount = rand.nextInt(recipe.minDontCare(), recipe.maxDontCare()+1);
            if(dcCount >= recipe.outputCount()){ //more dont-Cares then output bits, so we do not care at all.
                outputs[i] = 0xFFFFFFFF;
                continue;
            }
            for(int j = 0; j < recipe.outputCount(); j++){ //iterate over every output bit
                int output = rand.nextInt(0, dcCount > 0 ? 4 : 2); //generate 2 random bits, high is dont-care, low is expected output
                if(output > 2) dcCount--;//if dont-care is true we need 1 less to fulfill the demand.
                outputs[i] |= (((output & 0b10) << 15) | (output & 0b01)) << j;//dont-care mask starts at bit 16, values at bit 0. as dont care is already at bit 1 we shift the last 15
            }

            for(int j = 0; dcCount > 0; dcCount--){//set the last missing dont-care, if any
                while((outputs[i] & (1 << j+16)) != 0) j++; //At this point I do not care about even distribution
                outputs[i] |= 1 << j+16;
            }
            
        }
        

        values.put(recipe.id(), outputs);
        setDirty();
        return outputs;
    }

    public static SchematicResultData get(ServerLevel level){
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(SchematicResultData::load, SchematicResultData::new, DATA_NAME);
    }
    
}
