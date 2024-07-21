package de.bax.dysonsphere.tileRenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import de.bax.dysonsphere.tileentities.LaserCrafterTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
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
    }
    
}
