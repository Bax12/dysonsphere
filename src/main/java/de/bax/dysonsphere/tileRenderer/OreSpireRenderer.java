package de.bax.dysonsphere.tileRenderer;

import javax.annotation.Nonnull;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.OreSpireBlock;
import de.bax.dysonsphere.tileentities.OreSpireTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

public class OreSpireRenderer implements BlockEntityRenderer<OreSpireTile> {

    protected static final ResourceLocation RES_LOC = new ResourceLocation("block/stone");

    public OreSpireRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@Nonnull OreSpireTile tile, float pPartialTick, @Nonnull PoseStack pPoseStack, @Nonnull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5f, 0.1875f, 0.5f);
        if (tile.getBlockState().getValue(OreSpireBlock.HAS_BASE)) {
            pPoseStack.translate(0f, 0.1875f, 0f);
        }

        ItemStack ore = tile.inventory.getStackInSlot(0);

        //render ore L0
        for(int i = 0; i*10 < ore.getCount() && i < 25; i++){
            pPoseStack.pushPose();
            pPoseStack.mulPose(Axis.YP.rotationDegrees((101 * i) % 360));
            
            pPoseStack.translate(0.3f, 0.05f, 0f);

            pPoseStack.scale(0.2f, 0.2f, 0.2f);

            pPoseStack.mulPose(Axis.YP.rotationDegrees(17 * (i+1)));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(23 * (i+1)));
            pPoseStack.mulPose(Axis.XP.rotationDegrees(31 * (i+1)));

            AssetUtil.renderItemInWorld(ore, pPackedLight, pPackedOverlay, pPoseStack, pBuffer);

            pPoseStack.popPose();
        }
        

        if (tile.getMaxInventory() > 256) {
            pPoseStack.pushPose();
            pPoseStack.mulPose(Axis.YP.rotationDegrees(23));

            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(RES_LOC);

            renderCuboid(pPoseStack, pBuffer, sprite, 0.4375f, 0.1875f, 0.4375f, pPackedLight);

            //render ore L1
            for(int i = 0; i*10 < (ore.getCount()-256) && i < 25; i++){
                pPoseStack.pushPose();
                pPoseStack.mulPose(Axis.YP.rotationDegrees((101 * (i+2)) % 360));
                
                pPoseStack.translate(0.24f, 0.165f, 0f);

                pPoseStack.scale(0.2f, 0.2f, 0.2f);

                pPoseStack.mulPose(Axis.YP.rotationDegrees(17 * (i+1)));
                pPoseStack.mulPose(Axis.ZP.rotationDegrees(23 * (i+1)));
                pPoseStack.mulPose(Axis.XP.rotationDegrees(31 * (i+1)));

                AssetUtil.renderItemInWorld(ore, pPackedLight, pPackedOverlay, pPoseStack, pBuffer);

                pPoseStack.popPose();
            }

            pPoseStack.popPose();
            if (tile.getMaxInventory() > 512) { //todo make greater
                pPoseStack.translate(0f, 0.17f, 0f);
                pPoseStack.pushPose();
                pPoseStack.mulPose(Axis.YN.rotationDegrees(110));
                pPoseStack.mulPose(Axis.XP.rotationDegrees(7));

                renderCuboid(pPoseStack, pBuffer, sprite, 0.25f, 0.3125f, 0.25f, pPackedLight);

                
                //render ore L2
                for(int i = 0; i*10 < (ore.getCount()-512) && i < 25; i++){
                pPoseStack.pushPose();
                pPoseStack.mulPose(Axis.YP.rotationDegrees((101 * (i+5)) % 360));
                
                pPoseStack.translate(0.17f, 0.23f - 0.01 * (i % 13), 0f);

                pPoseStack.scale(0.2f, 0.2f, 0.2f);

                pPoseStack.mulPose(Axis.YP.rotationDegrees(17 * (i+1)));
                pPoseStack.mulPose(Axis.ZP.rotationDegrees(23 * (i+1)));
                pPoseStack.mulPose(Axis.XP.rotationDegrees(31 * (i+1)));

                AssetUtil.renderItemInWorld(ore, pPackedLight, pPackedOverlay, pPoseStack, pBuffer);

                pPoseStack.popPose();
                }

                pPoseStack.popPose();
                if (tile.getMaxInventory() > 768) {
                    pPoseStack.translate(0, 0.25f, 0f);
                    pPoseStack.pushPose();
                    pPoseStack.mulPose(Axis.YN.rotationDegrees(25));
                    pPoseStack.mulPose(Axis.ZN.rotationDegrees(13));

                    renderCuboid(pPoseStack, pBuffer, sprite, 0.1875f, 0.1875f, 0.1875f, pPackedLight);

                    //render ore L3
                    for(int i = 0; i*10 < (ore.getCount()-768) && i < 25; i++){
                        pPoseStack.pushPose();
                        pPoseStack.mulPose(Axis.YP.rotationDegrees((101 * (i+7)) % 360));
                        
                        pPoseStack.translate(0.17f, 0.1f - 0.01 * (i % 3), 0f);

                        pPoseStack.scale(0.2f, 0.2f, 0.2f);

                        pPoseStack.mulPose(Axis.YP.rotationDegrees(17 * (i+1)));
                        pPoseStack.mulPose(Axis.ZP.rotationDegrees(23 * (i+1)));
                        pPoseStack.mulPose(Axis.XP.rotationDegrees(31 * (i+1)));

                        AssetUtil.renderItemInWorld(ore, pPackedLight, pPackedOverlay, pPoseStack, pBuffer);

                        pPoseStack.popPose();
                    }

                    if(ore.getCount() == 1024){
                        pPoseStack.translate(0, 0.1875f, 0f);
                        pPoseStack.mulPose(Axis.YP.rotationDegrees(33));
                        pPoseStack.scale(0.2f, 0.2f, 0.2f);
                        AssetUtil.renderItemInWorld(ore, pPackedLight, pPackedOverlay, pPoseStack, pBuffer);
                    }

                    pPoseStack.popPose();
                }
            }
        }
        pPoseStack.popPose();
    }

    public static void renderCuboid(PoseStack poseStack, MultiBufferSource buffer, TextureAtlasSprite sprite, float width, float height, float depth, int light) {

        poseStack.pushPose();
        poseStack.translate(width / -2f, 0, depth / -2f);
        
        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS));

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();

        float uW = minU + (maxU - minU) * width;
        float vH = minV + (maxV - minV) * height;
        float uD = minU + (maxU - minU) * depth;
        float vD = minV + (maxV - minV) * depth;

        // FRONT
        quad(vc, pose, normal,
                0, 0, depth,
                width, 0, depth,
                width, height, depth,
                0, height, depth,
                minU, minV, uW, vH,
                0, 0, 1,
                light);

        // BACK
        quad(vc, pose, normal,
                width, 0, 0,
                0, 0, 0,
                0, height, 0,
                width, height, 0,
                minU, minV, uW, vH,
                0, 0, -1,
                light);

        // LEFT
        quad(vc, pose, normal,
                0, 0, 0,
                0, 0, depth,
                0, height, depth,
                0, height, 0,
                minU, minV, uD, vH,
                -1, 0, 0,
                light);

        // RIGHT
        quad(vc, pose, normal,
                width, 0, depth,
                width, 0, 0,
                width, height, 0,
                width, height, depth,
                minU, minV, uD, vH,
                1, 0, 0,
                light);

        // TOP
        quad(vc, pose, normal,
                0, height, depth,
                width, height, depth,
                width, height, 0,
                0, height, 0,
                minU, minV, uW, vD,
                0, 1, 0,
                light);

        // BOTTOM
        quad(vc, pose, normal,
                0, 0, 0,
                width, 0, 0,
                width, 0, depth,
                0, 0, depth,
                minU, minV, uW, vD,
                0, -1, 0,
                light);

        poseStack.popPose();
    }

    private static void quad(VertexConsumer vc, Matrix4f pose, Matrix3f normalMatrix, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float u1, float v1, float u2, float v2, float nx, float ny, float nz, int light) {
        drawVertex(pose, normalMatrix, vc, x1, y1, z1, u1, v1, nx, ny, nz, light);
        drawVertex(pose, normalMatrix, vc, x2, y2, z2, u2, v1, nx, ny, nz, light);
        drawVertex(pose, normalMatrix, vc, x3, y3, z3, u2, v2, nx, ny, nz, light);
        drawVertex(pose, normalMatrix, vc, x4, y4, z4, u1, v2, nx, ny, nz, light);
    }

    public static void drawVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer vertexBuilder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY, float nx, float ny, float nz, int packedLightIn) {
        vertexBuilder.vertex(matrix, offsetX, offsetY, offsetZ).color(1f, 1f, 1f, 1f).uv(textureX, textureY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(normals, nx, ny, nz).endVertex();
    }

}
