package de.bax.dysonsphere.entityRenderer;

import javax.annotation.Nonnull;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.compat.ModCompat.MODID;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import de.bax.dysonsphere.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class GrapplingHookRenderer extends EntityRenderer<GrapplingHookEntity> {

    // public static final ResourceLocation RES_LOC = new ResourceLocation(DysonSphere.MODID, "grappling_hook");
    public static final ResourceLocation RES_LOC = new ResourceLocation(DysonSphere.MODID, "textures/effects/rope.png");
    // protected ItemStack displayStack = ItemStack.EMPTY;

    public GrapplingHookRenderer(Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(@Nonnull GrapplingHookEntity pEntity) {
        return RES_LOC;
    }
    
    @Override
    public void render(@Nonnull GrapplingHookEntity pEntity, float pEntityYaw, float pPartialTick, @Nonnull PoseStack pPoseStack, @Nonnull MultiBufferSource pBuffer, int pPackedLight) {
        ItemStack displayStack = ItemStack.EMPTY;
        if(displayStack.isEmpty() && pEntity.getOwner() instanceof Player player){
            displayStack = player.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).map((gContainer) -> {
                return gContainer.getGrapplingHookFrame().flatMap((gFrame) -> {
                    return gFrame.getHookIcon(player.level(), player).map((stack) -> {
                        return stack;
                    });
                }).orElse(ItemStack.EMPTY);
            }).orElse(displayStack);
        }
        pPoseStack.pushPose();
        pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        Minecraft.getInstance().getItemRenderer().renderStatic(displayStack.isEmpty() ? ModItems.GRAPPLING_HOOK_HARNESS.get().getDefaultInstance() : displayStack, ItemDisplayContext.NONE, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, null, 0);
        pPoseStack.popPose();

        // pPoseStack.pushPose();
        // // pPoseStack.mulPose(new Quaternionf().rotateAxis((float)Math.PI * (-Minecraft.getInstance().gameRenderer.getMainCamera().getYRot() / 180f), 0, 1, 0));

        // pPoseStack.mulPose(new Quaternionf().rotationTo(Vec3.ZERO.toVector3f(),  pEntity.position().toVector3f().sub(pEntity.getOwner().position().toVector3f())));
        // VertexConsumer builder = pBuffer.getBuffer(RenderType.entityTranslucent(RES_LOC, true));
        // drawBeam(0.1f, 1, 2, pPoseStack, builder, 255);

        

        // pPoseStack.popPose();


        // renderChain(pEntity.getOwner().position().x - pEntity.position().x, pEntity.getOwner().position().y - pEntity.position().y, pEntity.getOwner().position().z - pEntity.position().z, pPartialTick, 0, pPoseStack, pBuffer, pPackedLight);

        renderRope(pEntity, pPartialTick, pPoseStack, pBuffer, pEntity.getOwner());
    }

    // public static void drawBeam(float size, float opacity, float maxY, PoseStack matrixStack, VertexConsumer builder, int packedLightIn) {
    //     float minV = 0;
    //     float maxV = 1;
    //     PoseStack.Pose entry = matrixStack.last();
    //     Matrix4f matrix4f = entry.pose();
    //     Matrix3f matrix3f = entry.normal();
    //     drawVertex(matrix4f, matrix3f, builder, -size, 0, 0, 0, minV, opacity, packedLightIn);
    //     drawVertex(matrix4f, matrix3f, builder, -size, maxY, 0, 0, maxV, opacity, packedLightIn);
    //     drawVertex(matrix4f, matrix3f, builder, size, maxY, 0, 1, maxV, opacity, packedLightIn);
    //     drawVertex(matrix4f, matrix3f, builder, size, 0, 0, 1, minV, opacity, packedLightIn);
    // }

    // public static void drawVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer vertexBuilder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY, float alpha, int packedLightIn) {
    //     vertexBuilder.vertex(matrix, offsetX, offsetY, offsetZ).color(1, 1, 1, 1 * alpha).uv(textureX, textureY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normals, 0.0F, 1.0F, 0.0F).endVertex();
    // }


    // public void renderChain(double x, double y, double z, float tickDelta, int age, PoseStack stack, MultiBufferSource builder, int light) {
	// 	double lengthXY = Math.sqrt(x * x + z * z);
	// 	double squaredLength = x * x + y * y + z * z;
	// 	float length = (float) Math.sqrt(squaredLength);

	// 	stack.pushPose();
	// 	stack.mulPose(Axis.YP.rotation((float) (-Math.atan2(z, x)) - 1.5707964F));
	// 	stack.mulPose(Axis.XP.rotation((float) (-Math.atan2(lengthXY, y)) - 1.5707964F));
	// 	stack.mulPose(Axis.ZP.rotationDegrees(25));
	// 	stack.pushPose();
	// 	stack.translate(0.015, -0.2, 0);

	// 	VertexConsumer vertexConsumer = builder.getBuffer(RenderType.entitySmoothCutout(RES_LOC));
	// 	float vertX1 = 0F;
	// 	float vertY1 = 0.25F;
	// 	float vertX2 = (float) Math.sin(6.2831855F) * 0.125F;
	// 	float vertY2 = (float) Math.cos(6.2831855F) * 0.125F;
	// 	float minU = 0F;
	// 	float maxU = 0.1875F;
	// 	float minV = 0.0F - ((float) age + tickDelta) * 0.01F;
	// 	float maxV = (float) Math.sqrt(squaredLength) / 8F - ((float) age + tickDelta) * 0.01F;
	// 	PoseStack.Pose entry = stack.last();
	// 	Matrix4f matrix4f = entry.pose();
	// 	Matrix3f matrix3f = entry.normal();

	// 	vertexConsumer.vertex(matrix4f, vertX1, vertY1, 0F).color(0, 0, 0, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
	// 	vertexConsumer.vertex(matrix4f, vertX1, vertY1, length).color(255, 255, 255, 255).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
	// 	vertexConsumer.vertex(matrix4f, vertX2, vertY2, length).color(255, 255, 255, 255).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
	// 	vertexConsumer.vertex(matrix4f, vertX2, vertY2, 0F).color(0, 0, 0, 255).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

	// 	stack.popPose();
	// 	stack.mulPose(Axis.ZP.rotationDegrees(90));
	// 	stack.translate(-0.015, -0.2, 0);

	// 	entry = stack.last();
	// 	matrix4f = entry.pose();
	// 	matrix3f = entry.normal();

	// 	vertexConsumer.vertex(matrix4f, vertX1, vertY1, 0F).color(0, 0, 0, 255).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
	// 	vertexConsumer.vertex(matrix4f, vertX1, vertY1, length).color(255, 255, 255, 255).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
	// 	vertexConsumer.vertex(matrix4f, vertX2, vertY2, length).color(255, 255, 255, 255).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
	// 	vertexConsumer.vertex(matrix4f, vertX2, vertY2, 0F).color(0, 0, 0, 255).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();

	// 	stack.popPose();
	// }


    private void renderRope(GrapplingHookEntity pEntity, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, Entity pHookUser) {
        pPoseStack.pushPose();
        Vec3 holderPos = pHookUser.getPosition(pPartialTicks).add(0, pHookUser.getBbHeight() / 2, 0);
        // double yRotDegrees = (double)(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
        // Vec3 entityOffset = Vec3.ZERO;
        // double xOffset = Math.cos(yRotDegrees) * entityOffset.z + Math.sin(yRotDegrees) * entityOffset.x;
        // double yOffset = Math.sin(yRotDegrees) * entityOffset.z - Math.cos(yRotDegrees) * entityOffset.x;
        // double d3 = Mth.lerp((double)pPartialTicks, pEntity.xo, pEntity.getX()) + xOffset;
        // double d4 = Mth.lerp((double)pPartialTicks, pEntity.yo, pEntity.getY()) + entityOffset.y;
        // double d5 = Mth.lerp((double)pPartialTicks, pEntity.zo, pEntity.getZ()) + yOffset;
        // pPoseStack.translate(xOffset, entityOffset.y, yOffset);
        float xDistance = (float)(holderPos.x - pEntity.getX());
        float yDistance = (float)(holderPos.y - pEntity.getY());
        float zDistance = (float)(holderPos.z - pEntity.getZ());
        // float f3 = 0.025F;
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.leash());
        Matrix4f matrix4f = pPoseStack.last().pose();
        float f4 = Mth.invSqrt(xDistance * xDistance + zDistance * zDistance) * 0.025F / 2.0F;
        float f5 = zDistance * f4;
        float f6 = xDistance * f4;
        BlockPos blockpos = BlockPos.containing(pEntity.getEyePosition(pPartialTicks));
        BlockPos blockpos1 = BlockPos.containing(pHookUser.getEyePosition(pPartialTicks));
        int i = this.getBlockLightLevel(pEntity, blockpos);
        int j = 15;
        int k = pEntity.level().getBrightness(LightLayer.SKY, blockpos);
        int l = pEntity.level().getBrightness(LightLayer.SKY, blockpos1);

        int color = pEntity.getRopeColor();
        int invColor = color ^ 0x00FFFFFF;

        double distRatio = pEntity.distanceToSqr(pHookUser) / (pEntity.getMaxDistance() * pEntity.getMaxDistance());
        if(distRatio > 0.6 && distRatio < 1){
            int timer = (int) Math.ceil(50 * (1 - distRatio));
            if(pEntity.tickCount % timer >= timer/2){
                color = invColor;
            }
        }

        

        for(int i1 = 0; i1 <= 48; ++i1) {
            addVertexPair(vertexconsumer, matrix4f, color, xDistance, yDistance, zDistance, i, j, k, l, 0.025F, 0.025F, f5, f6, i1);
        }

        for(int j1 = 48; j1 >= 0; --j1) {
            addVertexPair(vertexconsumer, matrix4f, color, xDistance, yDistance, zDistance, i, j, k, l, 0.025F, 0F, f5, f6, j1);
        }

        pPoseStack.popPose();
    }

    private static void addVertexPair(VertexConsumer pConsumer, Matrix4f pMatrix, int color, float xDistance, float yDistance, float zDistance, int pEntityBlockLightLevel, int pLeashHolderBlockLightLevel, int pEntitySkyLightLevel, int pLeashHolderSkyLightLevel, float slantOffset, float ySize, float xSize, float zSize, int pIndex) {
        float f = (float)pIndex / 48.0F;
        int i = (int)Mth.lerp(f, (float)pEntityBlockLightLevel, (float)pLeashHolderBlockLightLevel);
        int j = (int)Mth.lerp(f, (float)pEntitySkyLightLevel, (float)pLeashHolderSkyLightLevel);
        int k = LightTexture.pack(i, j);
        float f5 = xDistance * f;
        float f6 = yDistance * f;
        float f7 = zDistance * f;
        pConsumer.vertex(pMatrix, f5 - xSize, f6 + ySize, f7 + zSize).color(color).uv2(k).endVertex();
        pConsumer.vertex(pMatrix, f5 + xSize, f6 + slantOffset - ySize, f7 - zSize).color(color).uv2(k).endVertex();
    }


    @Override
    public boolean shouldRender(GrapplingHookEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ) {
        // if(Minecraft.getInstance().player.equals(pLivingEntity.getOwner())){
            return true;
        // }
        // return super.shouldRender(pLivingEntity, pCamera, pCamX, pCamY, pCamZ);
    }

}
