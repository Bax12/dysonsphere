package de.bax.dysonsphere.constructs;

import java.util.Map;
import java.util.Map.Entry;

import de.bax.dysonsphere.capabilities.dysonSphere.IDysonSphereContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class Construct {

    public record ComponentCount(int required, int foundation) {}
    
    public int tier; //demand requirements and foundation of equal or above tier.
    public float stability; //ds stability modifier once construct is built.
    public int energy; //energy draw (-) or supply (+) per tick
    public Map<Item, ComponentCount> components; // item component -- int requirement, int foundation
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
        for (Entry<Item, ComponentCount> component : components.entrySet()) {
            long count = ds.getDysonSpherePartCount(component.getKey());
            shouldBreak = shouldBreak || count < component.getValue().required();
            canWork = !shouldBreak && canWork && count > component.getValue().foundation();
            if(shouldBreak){ //not what the var name means, but quite funny.
                break;
            }
        }
    }

    public Component getDisplayName(){
        ResourceLocation loc = ModConstructs.registry().getKey(this);
        return Component.translatable("construct." + loc.getNamespace() + "." + loc.getPath());
    }

    
}
