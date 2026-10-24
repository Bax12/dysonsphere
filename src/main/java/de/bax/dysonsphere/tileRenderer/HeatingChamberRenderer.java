package de.bax.dysonsphere.tileRenderer;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import de.bax.dysonsphere.tileentities.HeatingChamberTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HeatingChamberRenderer implements BlockEntityRenderer<HeatingChamberTile>{

    public HeatingChamberRenderer(BlockEntityRendererProvider.Context context){
    }

    @Override
    public void render(@Nonnull HeatingChamberTile pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        
        pPoseStack.pushPose();

        pPoseStack.translate(0.3750f, 0.3250f, 0.3750f);
        pPoseStack.scale(0.3f, 0.3f, 0.3f);
        
        for(int i = 0; i < 8; ++i){
            ItemStack input = pBlockEntity.input.getStackInSlot(i);
            if(!input.isEmpty()){
                pPoseStack.pushPose();
                if(i % 2 == 1){
                    pPoseStack.translate(0.8, 0, 0);
                }
                if(i % 4 > 1){
                    pPoseStack.translate(0, 0, 0.8);
                }
                if(i > 3){
                    pPoseStack.translate(0, 1.05, 0);
                }
                if(input.getItem() instanceof BlockItem){
                    pPoseStack.scale(0.6f, 0.6f, 0.6f);
                    pPoseStack.translate(0F, 0.45f, 0f);
                } else {
                    pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
                }
                Minecraft.getInstance().getItemRenderer().renderStatic(input, ItemDisplayContext.NONE, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, null, 0);
                pPoseStack.popPose();
            }
        }

        pPoseStack.popPose();

        pPoseStack.translate(0.328125f, 0.1250f, 0.328125f);
        pPoseStack.scale(0.15f, 0.15f, 0.15f);

        pPoseStack.pushPose();
        for(int i = 0; i < 16; ++i){
            ItemStack output = pBlockEntity.output.getStackInSlot(i);
            if(!output.isEmpty()){
                pPoseStack.pushPose();
                float sway = (float) Math.sin((pBlockEntity.getLevel().getGameTime() / 25f) % (2 * Math.PI)) * 0.05f;
                pPoseStack.translate(0.8f * (i/4), sway + 0.1f, 0.8f * (i%4));
                pPoseStack.mulPose(Axis.YP.rotationDegrees(pBlockEntity.getLevel().getGameTime() * 2f % 360));
                

                if(output.getItem() instanceof BlockItem){
                    pPoseStack.scale(0.6f, 0.6f, 0.6f);
                    pPoseStack.translate(0F, 0.45f, 0f);
                } else {
                    pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
                }
                
                Minecraft.getInstance().getItemRenderer().renderStatic(output, ItemDisplayContext.NONE, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, null, 0);
                pPoseStack.popPose();
            }
        }

        pPoseStack.popPose();
    }
    
}
