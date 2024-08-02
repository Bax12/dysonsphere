package de.bax.dysonsphere.tileRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.tileentities.HeatExchangerTile;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;



public class HeatExchangerRenderer implements BlockEntityRenderer<HeatExchangerTile>{

    public static final float TEXTURE_WIDTH = 16f;
    public static final float TEXTURE_HEIGHT = 16f;
    public static final float RECT_MIN_U = 6f / TEXTURE_WIDTH;
    public static final float RECT_MAX_U = 10f / TEXTURE_WIDTH;
    public static float RECT_MIN_V = 1f / TEXTURE_HEIGHT;
    public static float RECT_MAX_V = 15f / TEXTURE_HEIGHT;

    public static final ResourceLocation RES_LOC_SIDE = new ResourceLocation(DysonSphere.MODID, "textures/block/heat_exchanger_block_side.png");

    public HeatExchangerRenderer(BlockEntityRendererProvider.Context context){
    }

    @SuppressWarnings("null")
    @Override
    public void render(@Nonnull HeatExchangerTile pBlockEntity, float pPartialTick, @Nonnull PoseStack pPoseStack, @Nonnull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        Optional<HeatExchangerTile>[] neighbors = pBlockEntity.getExchangerNeighbors();
        if(Arrays.stream(neighbors).anyMatch((neigh) -> {
            return neigh != null && neigh.isPresent();
        })){
            List<Direction> toRender = new ArrayList<>();
            for(int i = 2; i < 6; i++){ //Skip UP (1) and down (0) to only get horizontals
                if(neighbors[i] != null && neighbors[i].isPresent()){
                    toRender.add(Direction.values()[i]);
                }
            }
            for(Direction dir : toRender){
                pPoseStack.pushPose();
                alignToDirection(pPoseStack, dir);
                pPoseStack.translate(0.875f, 0.0625f, -0.0001f);

                // renderIcon(pPoseStack, pBuffer, icon, 0.5f, 0, 1, 1, 255, pPackedOverlay);
                VertexConsumer builder = pBuffer.getBuffer(RenderType.entitySolid(RES_LOC_SIDE));
                
                int light = LevelRenderer.getLightColor(pBlockEntity.getLevel(), pBlockEntity.getBlockPos().relative(dir.getCounterClockWise()));
                int light2 = LevelRenderer.getLightColor(pBlockEntity.getLevel(), pBlockEntity.getBlockPos().relative(dir.getCounterClockWise()).relative(dir));

                light = Math.max(light, light2); //this is rendered in between the two blocks, we want the brighter in case on of the positions contains a full block and is thus 0
                
                drawTexturedRect(0.25f,  1f, 0.875f, pPoseStack, builder, pPackedOverlay, light);
                // pPoseStack.mulPose(Axis.XP.rotationDegrees(180f));
                // pPoseStack.translate(0f, -1f, -1.0002f);
                // drawBeam(0.25f,  1f, 1, pPoseStack, builder, pPackedOverlay, 255);

                pPoseStack.popPose();
            }
        }
    }

    public static void alignToDirection(PoseStack pose, Direction facing){
        switch (facing) {
            case NORTH:
                pose.translate(0f, 0f, 1f);
                pose.mulPose(Axis.YP.rotationDegrees(90f));
                break;
            case SOUTH:
                pose.translate(1f, 0f, 0f);
                pose.mulPose(Axis.YN.rotationDegrees(90f));
                break;
            case EAST:
                // pose.translate(0f, 0f, -0.0001f);
                break;
            case WEST:
                pose.translate(1f, 0f, 1f);
                pose.mulPose(Axis.YP.rotationDegrees(180f));
                break;
            default:
                break;
        }
    }


    public static void drawTexturedRect(float size, float opacity, float maxY, PoseStack matrixStack, VertexConsumer builder, int overlay, int light) {
        PoseStack.Pose entry = matrixStack.last();
        Matrix4f matrix4f = entry.pose();
        Matrix3f matrix3f = entry.normal();
        drawVertex(matrix4f, matrix3f, builder, 0, 0, 0, RECT_MIN_U, RECT_MIN_V, opacity, overlay, light);
        drawVertex(matrix4f, matrix3f, builder, 0, maxY, 0, RECT_MIN_U, RECT_MAX_V, opacity, overlay, light);
        drawVertex(matrix4f, matrix3f, builder, size, maxY, 0, RECT_MAX_U, RECT_MAX_V, opacity, overlay, light);
        drawVertex(matrix4f, matrix3f, builder, size, 0, 0, RECT_MAX_U, RECT_MIN_V, opacity, overlay, light);
    }

    public static void drawVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer vertexBuilder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY, float alpha, int overlay, int light) {
        vertexBuilder.vertex(matrix, offsetX, offsetY, offsetZ).color(1, 1, 1, 1 * alpha).uv(textureX, textureY).overlayCoords(overlay).uv2(light).normal(normals, 0.0F, 1.0F, 0.0F).endVertex();
    }
    
}
