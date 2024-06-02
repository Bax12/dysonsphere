package de.bax.dysonsphere.entityRenderer;

import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.entities.LaserStrikeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class LaserStrikeRenderer extends EntityRenderer<LaserStrikeEntity> {

    public static final float TEXTURE_WIDTH = 256;
    public static final float TEXTURE_HEIGHT = 32;
    public static final float BEAM_MIN_U = 224f / TEXTURE_WIDTH;
    public static final float BEAM_MAX_U = 1;
    public static final float BEAM_TARGETING_SIZE = 0.025f;
    public static final float BEAM_SMALL_SIZE = 0.25f;
    public static final float BEAM_MEDIUM_SIZE = 1.0f;
    public static final float BEAM_LARGE_SIZE = 2.0f;
    
    public static final ResourceLocation RES_LOC = new ResourceLocation(DysonSphere.MODID, "textures/effects/laserstrike.png");

    public LaserStrikeRenderer(Context ctx) {
        super(ctx);
    }

    @Override
    public ResourceLocation getTextureLocation(LaserStrikeEntity entity) {
        return RES_LOC;
    }
    
    @Override
    public void render(LaserStrikeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        
        VertexConsumer builder = bufferSource.getBuffer(RenderType.beaconBeam(RES_LOC, true));
        
        // poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(new Quaternionf().rotateAxis((float)Math.PI * (-Minecraft.getInstance().gameRenderer.getMainCamera().getYRot() / 180f), 0, 1, 0));
        
        
        if(entity.isAiming()){
            //aim
            drawBeam(BEAM_TARGETING_SIZE, 0.5f, 255, poseStack, builder, light);        
        } else if(entity.isStriking()){
            //strike
            drawBeam(BEAM_SMALL_SIZE, 1f, 255, poseStack, builder, light);        
        } else if (entity.isLingering()){

        }
        
        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(LaserStrikeEntity p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return true;
    }

    public static void drawBeam(float size, float opacity, float maxY, PoseStack matrixStack, VertexConsumer builder, int packedLightIn) {
        float minV = 0 / TEXTURE_HEIGHT;
        float maxV = 1 / TEXTURE_HEIGHT;
        PoseStack.Pose entry = matrixStack.last();
        Matrix4f matrix4f = entry.pose();
        Matrix3f matrix3f = entry.normal();
        drawVertex(matrix4f, matrix3f, builder, -size, 0, 0, BEAM_MIN_U, minV, opacity, packedLightIn);
        drawVertex(matrix4f, matrix3f, builder, -size, maxY, 0, BEAM_MIN_U, maxV, opacity, packedLightIn);
        drawVertex(matrix4f, matrix3f, builder, size, maxY, 0, BEAM_MAX_U, maxV, opacity, packedLightIn);
        drawVertex(matrix4f, matrix3f, builder, size, 0, 0, BEAM_MAX_U, minV, opacity, packedLightIn);
    }

    public static void drawVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer vertexBuilder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY, float alpha, int packedLightIn) {
        vertexBuilder.vertex(matrix, offsetX, offsetY, offsetZ).color(1, 1, 1, 1 * alpha).uv(textureX, textureY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normals, 0.0F, 1.0F, 0.0F).endVertex();
    }

}
