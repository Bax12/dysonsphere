package de.bax.dysonsphere.util;

import org.joml.Vector3f;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.resources.ResourceLocation;

public class SkyLightUtil {
    
    public static float darkenBy = 0.9f; //0 = normal sky; 1 = total eclipse

    public static final ResourceLocation sun_eclipse = new ResourceLocation(DysonSphere.MODID, "textures/environment/sun_eclipse.png");
    public static final ResourceLocation sun_90 = new ResourceLocation(DysonSphere.MODID, "textures/environment/sun_90.png");
    public static final ResourceLocation sun_80 = new ResourceLocation(DysonSphere.MODID, "textures/environment/sun_80.png");
    public static final ResourceLocation sun_70 = new ResourceLocation(DysonSphere.MODID, "textures/environment/sun_70.png");
    public static final ResourceLocation sun_60 = new ResourceLocation(DysonSphere.MODID, "textures/environment/sun_60.png");

    public static ResourceLocation getSunTexture(ResourceLocation defaultLocation){
        if(darkenBy >= 1f){
            return sun_eclipse;
        }
        if(darkenBy >= 0.9f){
            return sun_90;
        }
        if(darkenBy >= 0.8f){
            return sun_80;
        }
        if(darkenBy >= 0.7f){
            return sun_70;
        }
        if(darkenBy >= 0.6f){
            return sun_60;
        }      
        return defaultLocation;
    }

    public static void changeSkyLight(Vector3f skyLight, float partialTicks){
        skyLight.lerp(new Vector3f(0, 0, 0), darkenBy * 1.1f);
    }



}
