package de.bax.dysonsphere.tileRenderer;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import de.bax.dysonsphere.compat.ModCompat;
import de.bax.dysonsphere.compat.aaaparticle.AAAParticle;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.tileentities.RailgunTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RailgunRenderer implements BlockEntityRenderer<RailgunTile> {

    public RailgunRenderer(BlockEntityRendererProvider.Context context){
    }

    @Override
    public void render(@Nonnull RailgunTile tile, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 2.3F, 0.5F);
        float rotation = -(360f * tile.getLevel().getDayTime() / 24000) -90f % 360f;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        // poseStack.translate(0.0F, 0.0F, -0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(65f));
        poseStack.scale(1.9f, 1.9f, 1.9f);

        poseStack.translate(0, 0, 0.1f * Math.pow((90f - tile.getTicksSinceLastLaunch())/100f, 4));

        if(ModCompat.isLoaded(ModCompat.MODID.AAA_PARTICLE) && tile.getTicksSinceLastLaunch() <= 5){
            AAAParticle.spawnOrbitalLaunchEmitter(tile.getLevel(), tile.getBlockPos().getX() + 0.5f, tile.getBlockPos().getY() + 2.3f, tile.getBlockPos().getZ() + 0.5f, 245f * ((float)Math.PI / 180F), rotation * ((float)Math.PI / 180F), 0);
        }

        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(ModItems.RAILGUN.get()), ItemDisplayContext.NONE, combinedLight, combinedOverlay, poseStack, bufferSource, null, 0);


        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(RailgunTile tile) {
        return true;
    }

    
}
