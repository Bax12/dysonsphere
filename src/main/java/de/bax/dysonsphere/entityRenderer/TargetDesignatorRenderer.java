package de.bax.dysonsphere.entityRenderer;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.entities.TargetDesignatorEntity;
import de.bax.dysonsphere.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TargetDesignatorRenderer extends EntityRenderer<TargetDesignatorEntity> {

    public static final float DEPLOY_BEAM_SIZE = 0.03f;
    public static final int DEPLOY_BEAM_HEIGHT = 5;

    public static final ResourceLocation RES_LOC = new ResourceLocation(DysonSphere.MODID, "target_designator");

    public TargetDesignatorRenderer(Context ctx) {
        super(ctx);
    }

    @Override
    public void render(TargetDesignatorEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        if(entity.tickCount > 1){
            poseStack.pushPose();
            poseStack.translate(0, 0.15F, 0);
            poseStack.scale(0.4f, 0.4f, 0.4f);
            Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(ModItems.TARGET_DESIGNATOR.get()), ItemDisplayContext.NONE, light, 1, poseStack, bufferSource, null, 0);

            poseStack.popPose();
        }
        if(entity.deployedAt > 0){
            poseStack.pushPose();
            poseStack.mulPose(new Quaternionf().rotateAxis((float)Math.PI * (-Minecraft.getInstance().gameRenderer.getMainCamera().getYRot() / 180f), 0, 1, 0));
            VertexConsumer builder = bufferSource.getBuffer(RenderType.beaconBeam(LaserStrikeRenderer.RES_LOC, true));
            LaserStrikeRenderer.drawBeam(DEPLOY_BEAM_SIZE, 1f, DEPLOY_BEAM_HEIGHT, poseStack, builder, light);
            poseStack.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(TargetDesignatorEntity entity) {
        
        return RES_LOC;
    }
    
}
