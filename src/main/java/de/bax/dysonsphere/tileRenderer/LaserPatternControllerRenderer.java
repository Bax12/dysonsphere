package de.bax.dysonsphere.tileRenderer;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import de.bax.dysonsphere.tileentities.LaserPatternControllerTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class LaserPatternControllerRenderer implements BlockEntityRenderer<LaserPatternControllerTile> {

    public LaserPatternControllerRenderer(BlockEntityRendererProvider.Context context){

    }

    @Override
    public void render(@Nonnull LaserPatternControllerTile pBlockEntity, float pPartialTick, @Nonnull PoseStack pPoseStack, @Nonnull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();

        ItemStack containedStack = pBlockEntity.inventory.getStackInSlot(0);
        if(!containedStack.isEmpty()) {
            pPoseStack.translate(0.5F, 0.2f, 0.5f);
            pPoseStack.scale(0.4f, 0.4f, 0.4f);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90));

            BlockState state = pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos());
            Optional<Direction> facing = state.getOptionalValue(HorizontalDirectionalBlock.FACING);
            if(facing.isPresent()){
                switch(facing.get()){
                    case EAST:
                        pPoseStack.mulPose(Axis.ZP.rotationDegrees(90));
                        break;
                    case SOUTH:
                        pPoseStack.mulPose(Axis.ZP.rotationDegrees(180));
                        break;
                    case WEST:
                        pPoseStack.mulPose(Axis.ZN.rotationDegrees(90));
                        break;
                }
            }
            
            
            pPoseStack.translate(0f, -0.5f, 0f);

            Minecraft.getInstance().getItemRenderer().renderStatic(containedStack, ItemDisplayContext.NONE, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, null, 0);
        }
        


        pPoseStack.popPose();
    }
    
}
