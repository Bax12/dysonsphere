package de.bax.dysonsphere.entityRenderer;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.tileentities.ListenerTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ListenerRenderer implements BlockEntityRenderer<ListenerTile> {

    protected float frame = 0;

    public ListenerRenderer(BlockEntityRendererProvider.Context context){
    }

    @Override
    public void render(@Nonnull ListenerTile tile, float pPartialTick, @Nonnull PoseStack pPoseStack, @Nonnull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5F, 0.85F, 0.5F);

        frame += Minecraft.getInstance().getDeltaFrameTime();

        float rotation = (frame / 5) % 360f;
        
        pPoseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        pPoseStack.mulPose(Axis.XP.rotationDegrees(90f));


        
        AssetUtil.renderItemInWorld(ModItems.SENSOR_UNIT.get().getDefaultInstance(), pPackedLight, pPackedOverlay, pPoseStack, pBuffer);

        pPoseStack.popPose();
    }
    
}
