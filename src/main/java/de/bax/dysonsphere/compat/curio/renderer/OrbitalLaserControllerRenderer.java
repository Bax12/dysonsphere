package de.bax.dysonsphere.compat.curio.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class OrbitalLaserControllerRenderer implements ICurioRenderer {

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer,
            int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        
        matrixStack.pushPose();
        LivingEntity player = slotContext.entity();
        
        
        // ICurioRenderer.translateIfSneaking(matrixStack, player);
        if(renderLayerParent.getModel() instanceof HumanoidModel hModel){
            hModel.translateToHand(HumanoidArm.LEFT, matrixStack);
        }

        matrixStack.translate(0.065f, 0.64f, -0.125f);
        matrixStack.mulPose(Axis.ZN.rotationDegrees(180f));
        matrixStack.mulPose(Axis.XN.rotationDegrees(90f));
        


        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, light, OverlayTexture.NO_OVERLAY, matrixStack, renderTypeBuffer, player.level(), 0);
        matrixStack.popPose();
    }
    
}
