package de.bax.dysonsphere.constructs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.bax.dysonsphere.capabilities.dysonSphere.IDysonSphereContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class Construct {

    public record ComponentCount(int required, int foundation) {}
    
    public int tier; //demand requirements and foundation of equal or above tier.
    public float stability; //ds stability modifier once construct is built.
    public int energy; //energy draw (-) or supply (+) per tick
    public Map<Ingredient, ComponentCount> components = new HashMap<>(10); // item component -- int requirement, int foundation
    //requirement: cannot work / stop working without -- shared components between Constructs, not consumed
    //foundation: cannot exist / stop existing without -- shared components between Constructs, not consumed

    protected boolean canWork;
    protected boolean shouldBreak;

    public boolean canWork(){
        return canWork;
    }

    public boolean shouldBreak(){
        return shouldBreak;
    }

    public void onDSChange(IDysonSphereContainer ds){
        canWork = true;
        for (Entry<Ingredient, ComponentCount> component : components.entrySet()) {
            long count = ds.getDysonSpherePartCount(component.getKey());
            shouldBreak |= count < component.getValue().foundation();
            canWork &= !shouldBreak && count >= component.getValue().required();
            if(shouldBreak){ //not what the var name means, but quite funny.
                break;
            }
        }
    }

    public ResourceLocation getResourceLocation(){
        return ModConstructs.registry().getKey(this);
    }

    public @Nonnull Component getDisplayName(){
        ResourceLocation loc = getResourceLocation();
        return Component.translatable("construct." + loc.getNamespace() + "." + loc.getPath());
    }

    public @Nonnull Component getDescriptionName(){
        ResourceLocation loc = getResourceLocation();
        return Component.translatable("construct." + loc.getNamespace() + "." + loc.getPath() + "_desc");
    }


    public void writeToPacket(@Nonnull FriendlyByteBuf buffer){
        buffer.writeResourceLocation(getResourceLocation());
    }

    public static Construct readFromPacket(@Nonnull FriendlyByteBuf buffer){
        return ModConstructs.registry().getValue(buffer.readResourceLocation());
    }

    public JsonElement toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("id", getResourceLocation().toString());
        return json;
    }

    public static Construct fromJson(JsonObject json){
        ResourceLocation location = new ResourceLocation(json.get("id").getAsString());
        return ModConstructs.registry().getValue(location);
    }

    public CompoundTag save(){
        CompoundTag tag = new CompoundTag();
        tag.putString("id", getResourceLocation().toString());
        return tag;
    }

    public static Construct load(CompoundTag tag){
        ResourceLocation location = new ResourceLocation(tag.getString("id"));
        return ModConstructs.registry().getValue(location);
    }
    
}
