package de.bax.dysonsphere.tileRenderer;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.entityRenderer.LaserStrikeRenderer;
import de.bax.dysonsphere.tileentities.LaserRedirectorTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class LaserRedirectorRenderer implements BlockEntityRenderer<LaserRedirectorTile> {

    public static final ResourceLocation RES_LOC = new ResourceLocation(DysonSphere.MODID, "textures/effects/laserstrike.png");

    public LaserRedirectorRenderer(BlockEntityRendererProvider.Context context){
    }

    @Override
    public void render(LaserRedirectorTile tile, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if(tile.emittedLength <= 0) return;
        pPoseStack.pushPose();
        VertexConsumer builder = pBuffer.getBuffer(RenderType.entityTranslucent(RES_LOC, false));

        Direction facing = tile.getBlockState().getValue(BlockStateProperties.FACING);

        pPoseStack.translate(0.5f, 0.5f, 0.5f);
        
        //todo rotate to player view while maintaining the axis of facing
        // pPoseStack.mulPose(new Quaternionf().rotateAxis((float)Math.PI * (-Minecraft.getInstance().gameRenderer.getMainCamera().getYRot() / 180f), 0, 1, 0));
        
        pPoseStack.mulPose(facing.getRotation());

        

        

        Vec3 diff = tile.getBlockPos().getCenter().subtract(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());

        if(facing.getAxis().isVertical()){
            diff = diff.multiply(1, 0, 1);
            float angle = diff.toVector3f().angleSigned(0, 0, 1, 0, -1, 0);
            pPoseStack.mulPose(new Quaternionf().rotateAxis(angle, 0, facing.getStepY(), 0));
        } else if (facing.getAxis().equals(Direction.Axis.Z)){
            diff = diff.multiply(1, 1, 0);
            float angle = diff.toVector3f().angleSigned(0, 1, 0, 0, 0, -1);
            pPoseStack.mulPose(new Quaternionf().rotateAxis(angle, 0, facing.getStepZ(), 0));
        } else {
            diff = diff.multiply(0, 1, 1);
            float angle = diff.toVector3f().angleSigned(0, 1, 0, -1, 0, 0);
            pPoseStack.mulPose(new Quaternionf().rotateAxis(angle, 0, facing.getStepX(), 0));
        }
        


        pPoseStack.translate(0f, 0.45f, 0f);

        // pPoseStack.mulPose(Axis.YP.rotationDegrees(tile.getLevel().getGameTime() * 4f % 360));
        

        LaserStrikeRenderer.drawBeam(0.1f, 1f, tile.emittedLength-0.9f, pPoseStack, builder, 255);//length -1 to compensate the start/end block.

        pPoseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(LaserRedirectorTile pBlockEntity) {
        return true;
    }
    
}
