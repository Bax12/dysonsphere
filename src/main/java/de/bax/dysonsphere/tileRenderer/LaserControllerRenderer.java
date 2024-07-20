package de.bax.dysonsphere.tileRenderer;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.tileentities.LaserControllerTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LaserControllerRenderer implements BlockEntityRenderer<LaserControllerTile> {

    public LaserControllerRenderer(BlockEntityRendererProvider.Context context){
    }

    @Override
    public void render(@Nonnull LaserControllerTile tile, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if(tile.hasPattern()){
            poseStack.pushPose();

            poseStack.translate(0.5f, 1.4f, 0.5f);
            float sway = (float) Math.sin((tile.getLevel().getGameTime() / 25f) % (2 * Math.PI)) * 0.065f;
            poseStack.translate(0, sway, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(tile.getLevel().getGameTime() * 4f % 360));

            poseStack.scale(0.4f, 0.4f, 0.4f);

            Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(ModItems.TARGET_DESIGNATOR.get()), ItemDisplayContext.NONE, combinedLight, combinedOverlay, poseStack, bufferSource, null, 0);

            poseStack.popPose();
        }
        
        
    }
    
    
}
