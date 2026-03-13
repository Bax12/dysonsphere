package de.bax.dysonsphere.entityRenderer;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.compat.ModCompat;
import de.bax.dysonsphere.entities.LaserStrikeEntity;
import de.bax.dysonsphere.entities.DeliveryDropEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class DeliveryDropRenderer extends EntityRenderer<DeliveryDropEntity> {

    public static final ResourceLocation RES_LOC = new ResourceLocation(DysonSphere.MODID, "textures/effects/plasmastrike.png");

    public DeliveryDropRenderer(Context pContext) {
        super(pContext);
    }

    @Override
    public void render(@Nonnull DeliveryDropEntity pEntity, float pEntityYaw, float pPartialTick, @Nonnull PoseStack pPoseStack, @Nonnull MultiBufferSource pBuffer, int pPackedLight) {
        //todo
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public boolean shouldRender(@Nonnull DeliveryDropEntity entity, @Nonnull Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return !ModCompat.isLoaded(ModCompat.MODID.AAA_PARTICLE);
    }

    @Override
    public ResourceLocation getTextureLocation(@Nonnull DeliveryDropEntity pEntity) {
        return RES_LOC;        
    }
    
}
