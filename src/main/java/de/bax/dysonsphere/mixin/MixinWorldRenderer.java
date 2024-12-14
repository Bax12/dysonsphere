package de.bax.dysonsphere.mixin;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;

import de.bax.dysonsphere.util.SkyLightUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;

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
    @Shadow
    private VertexBuffer starBuffer;

    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0))
    private void bindCustomSunTexture(int shaderTexture, ResourceLocation resourceLocation) {
        RenderSystem.setShaderTexture(shaderTexture, SkyLightUtil.getSunTexture(resourceLocation));
    }

    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"))
    private void darkenSky(float r, float g, float b, float a){
        float f = Math.max(1f - SkyLightUtil.darkenBy, 0.1f);
        RenderSystem.setShaderColor(r * f, g * f, b * f, a);//we do not change alpha, as it tends to make a lot of fog and cloud stuff invisible
    }

    @Inject(method = "renderSky", at = @At("RETURN"))
    private void resetSkyColor(CallbackInfo ci){
        //fixing the mess we made in darkenSky()
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    @SuppressWarnings("null")
    @Inject(method = "renderSky", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void brightenStars(PoseStack pPoseStack, Matrix4f pProjectionMatrix, float pPartialTick, Camera pCamera, boolean pIsFoggy, Runnable pSkyFogSetup, CallbackInfo ci, FogType fogtype,  Vec3 vec3, float f, float f1, float f2, BufferBuilder bufferbuilder, ShaderInstance shaderinstance,
            float[] afloat, float f11, Matrix4f matrix4f1, float f12,  int k, int l, int i1, float f13, float f14,float f15,float f16){
        if(SkyLightUtil.darkenBy > 0.1f){
            float brightness = (SkyLightUtil.darkenBy * 2f) * f11;
            RenderSystem.setShaderColor(brightness,brightness,brightness,brightness);   
            FogRenderer.setupNoFog();
            this.starBuffer.bind();
            this.starBuffer.drawWithShader(pPoseStack.last().pose(), pProjectionMatrix, GameRenderer.getPositionShader());
            VertexBuffer.unbind();
            pSkyFogSetup.run();
        }
    }

}
