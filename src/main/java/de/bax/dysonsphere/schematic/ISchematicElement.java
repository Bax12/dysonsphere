package de.bax.dysonsphere.schematic;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface ISchematicElement {
    
    public int getWidth();

    public int getHeight();

    public String getName();

    public List<String> getInputs();

    public List<String> getOutputs();

    default public void evaluate(int packedInputs){};

    public boolean getState(String output);

    default public CompoundTag serialize(){
        CompoundTag tag = new CompoundTag();
        tag.putString("id", getResourceLocation().toString());
        return tag;
    }

    default public void deserialize(CompoundTag nbt){};

    public static ISchematicElement load(CompoundTag tag){
        ISchematicElement element = ModSchematics.registry().getValue(new ResourceLocation(tag.getString("id")));
        element.deserialize(tag);
        return element;
    }

    default public ResourceLocation getResourceLocation(){
        return ModSchematics.registry().getKey(this);
    }

    public static ISchematicElement readFromPacket(@Nonnull FriendlyByteBuf buffer){
        return ModSchematics.registry().getValue(buffer.readResourceLocation());
    }

    default public void writeToPacket(@Nonnull FriendlyByteBuf buffer){
        buffer.writeResourceLocation(getResourceLocation());
    }

    public static ISchematicElement fromJson(JsonObject json){
        ResourceLocation location = new ResourceLocation(json.get("id").getAsString());
        return ModSchematics.registry().getValue(location);
    }

    default public JsonElement toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("id", getResourceLocation().toString());
        return json;
    }

    public static final ISchematicElement EMPTY = new ISchematicElement() {
        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public List<String> getInputs() {
            return List.of();
        }

        @Override
        public List<String> getOutputs() {
            return List.of();
        }

        @Override
        public boolean getState(String output) {
            return false;
        }
    };

}
