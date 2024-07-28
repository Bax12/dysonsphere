package de.bax.dysonsphere.tileRenderer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import de.bax.dysonsphere.entityRenderer.LaserStrikeRenderer;
import de.bax.dysonsphere.tileentities.LaserCrafterTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

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
        
        pPoseStack.pushPose();

        ItemStack output = pBlockEntity.output.getStackInSlot(0);
        if(!output.isEmpty()){
            pPoseStack.translate(0.5f, 0.75f, 0.5f);
            float sway = (float) Math.sin((pBlockEntity.getLevel().getGameTime() / 25f) % (2 * Math.PI)) * 0.05f;
            pPoseStack.translate(0, sway, 0);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(pBlockEntity.getLevel().getGameTime() * 4f % 360));

            pPoseStack.scale(0.4f, 0.4f, 0.4f);

            Minecraft.getInstance().getItemRenderer().renderStatic(output, ItemDisplayContext.NONE, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, null, 0);
        }

        pPoseStack.popPose();

        pPoseStack.pushPose();
        if(!input.isEmpty() && pBlockEntity.getCharge() > 0 && pBlockEntity.getRecipe().isPresent()){
            pPoseStack.translate(0.5F, 0.4380f, 0.5f);
            pPoseStack.mulPose(new Quaternionf().rotateAxis((float)Math.PI * (-Minecraft.getInstance().gameRenderer.getMainCamera().getYRot() / 180f), 0, 1, 0));
            // pPoseStack.mulPose(new Quaternionf().rotateAxis(0, 0, 1, 0));
            float swayX = (float) Math.sin((pBlockEntity.getLevel().getGameTime() / 25f) % (2 * Math.PI)) * 0.1f;
            float swayZ = (float) Math.cos((pBlockEntity.getLevel().getGameTime() / 5f) % (2 * Math.PI)) * 0.1f;
            pPoseStack.translate(swayX, 0f, swayZ);
            VertexConsumer builder = pBuffer.getBuffer(RenderType.entityTranslucent(LaserStrikeRenderer.RES_LOC, false));
            LaserStrikeRenderer.drawBeam(0.015f + (0.001f * pPartialTick), 0.85f, 0.55f, pPoseStack, builder, pPackedLight);
            // pPoseStack.mulPose(new Quaternionf().rotateAxis(120, 0, 1, 0));
            // LaserStrikeRenderer.drawBeam(0.015f + (0.002f * pPartialTick), 0.85f, 0.2f, pPoseStack, builder, pPackedLight);
        }
        pPoseStack.popPose();
    }
    
}
