package de.bax.dysonsphere.mixin;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import de.bax.dysonsphere.util.SkyLightUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;

@Mixin(LightTexture.class)
public class MixinLightTexture {
    
    @Inject(method = "updateLightTexture", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LightTexture;blockLightRedFlicker:F"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void darkenBlockLight(float partialTicks, CallbackInfo ci, ClientLevel $$1, float $$2, float $$4, float $$5, float $$6, float $$7, float $$11, float $$8, Vector3f skyVector) {
        SkyLightUtil.changeSkyLight(skyVector, partialTicks);
    }
}
