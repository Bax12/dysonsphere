package de.bax.dysonsphere.tileRenderer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.entityRenderer.LaserStrikeRenderer;
import de.bax.dysonsphere.tileentities.LaserCrafterTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ContainerScreenEvent.Render;
import net.minecraftforge.client.model.data.ModelData;

public class LaserCrafterRenderer implements BlockEntityRenderer<LaserCrafterTile> {

    public LaserCrafterRenderer(BlockEntityRendererProvider.Context context){
    }

    @Override
    public void render(LaserCrafterTile pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if(pBlockEntity == null) return;
        pPoseStack.pushPose();
        pPoseStack.translate(0.5F, 0.4380f, 0.5f);
        pPoseStack.scale(0.4f, 0.4f, 0.4f);
        

        ItemStack input = pBlockEntity.input.getStackInSlot(0);

        if(!input.isEmpty()){
            if(input.getItem() instanceof BlockItem){
                pPoseStack.scale(0.8f, 0.8f, 0.8f);
                pPoseStack.translate(0F, 0.5f, 0f);
            } else {
                pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(input, ItemDisplayContext.NONE, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, null, 0);
        }

        


        pPoseStack.popPose();

        // pPoseStack.pushPose();
        // VertexConsumer builder = pBuffer.getBuffer(RenderType.lightning());
        // pPoseStack.translate(0, 0.5f, 0);

        // PoseStack.Pose entry = pPoseStack.last();
        // Matrix4f matrix4f = entry.pose();
        // Matrix3f matrix3f = entry.normal();
        // RenderSystem.setShaderColor(1, 0, 0, 1);
        // drawVertex(matrix4f, matrix3f, builder, 0, 0, 0, 0.5f, pPackedLight);
        // drawVertex(matrix4f, matrix3f, builder, 0, 0, 1, 0.5f, pPackedLight);
        // drawVertex(matrix4f, matrix3f, builder, 1, 0, 1,  0.5f, pPackedLight);
        // drawVertex(matrix4f, matrix3f, builder, 1, 0, 0, 0.5f, pPackedLight);

        // RenderSystem.setShaderColor(1, 1, 1, 1);
        // pPoseStack.popPose();

        // pPoseStack.pushPose();
        // pPoseStack.translate(0.5f, 0.5f, 0.5f);
        // pPoseStack.scale(0.8f, 0.8f, 0.8f);
        // RenderSystem.setShaderColor(1f, 0f, 0f, 1f);
        // Minecraft.getInstance().getBlockRenderer().renderSingleBlock(ModBlocks.LASER_CRAFTER_BLOCK.get().defaultBlockState(), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, ModelData.EMPTY, RenderType.translucent());

        // Minecraft.getInstance().getItemRenderer().renderStatic(ModBlocks.LASER_CRAFTER_BLOCK.get().asItem().getDefaultInstance(), ItemDisplayContext.NONE, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, null, 0);

        // RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        // pPoseStack.popPose();

    }

    public static void drawVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer vertexBuilder, float offsetX, float offsetY, float offsetZ, float alpha, int packedLightIn) {
        vertexBuilder.vertex(matrix, offsetX, offsetY, offsetZ).color(1, 0, 0, 1 * alpha).overlayCoords(OverlayTexture.NO_OVERLAY).uv(0, 0).uv2(packedLightIn).normal(normals, 0.0F, 1.0F, 0.0F).endVertex();
    }
    
}
