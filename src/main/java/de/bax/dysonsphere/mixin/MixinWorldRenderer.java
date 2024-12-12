package de.bax.dysonsphere.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import de.bax.dysonsphere.util.SkyLightUtil;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;

@Mixin(LevelRenderer.class)
public abstract class MixinWorldRenderer {
    
    // @Inject(method = "renderSky", at = @At("HEAD"))
    // private void changeSkyBrightness(CallbackInfo ci){
        
    // }

    //tiny sun mode lol
    // @ModifyConstant(method = "renderSky", constant = @Constant(floatValue = 30.0F))
    // private float getSuperMoonSize(float size) {
    //     return 5f;
    // }

    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0))
    private void bindCustomSunTexture(int shaderTexture, ResourceLocation resourceLocation) {
        RenderSystem.setShaderTexture(shaderTexture, SkyLightUtil.getSunTexture(resourceLocation));
    }

    protected boolean isStarCall = false; //weird hack solution, couldn't find a better way to have darkenSky change behavior at the one call for the stars.

    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"))
    private void darkenSky(float r, float g, float b, float a){
        if(isStarCall){
            float f = 1f + SkyLightUtil.darkenBy;
            RenderSystem.setShaderColor(r * f, g * f, b * f, a);    
            isStarCall = false;
        } else {
            float f = Math.max(1f - SkyLightUtil.darkenBy, 0.1f);
            RenderSystem.setShaderColor(r * f, g * f, b * f, a);//we do not change alpha, as it tends to make a lot of fog and cloud stuff invisible
        }
    }

    @Inject(method = "renderSky", at = @At("RETURN"))
    private void resetSkyColor(CallbackInfo ci){
        //fixing the mess we made in darkenSky()
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F"))
    private void brightenStars(CallbackInfo ci){
        isStarCall = true;
    }

}
