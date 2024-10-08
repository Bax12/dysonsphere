package de.bax.dysonsphere.compat.curio.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class GrapplingHookHarnessRenderer implements ICurioRenderer{

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer,
            int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        
        matrixStack.pushPose();
        LivingEntity player = slotContext.entity();
        ICurioRenderer.translateIfSneaking(matrixStack, player);
        ICurioRenderer.rotateIfSneaking(matrixStack, player);
        matrixStack.translate(0f, 0.4f, 0.08f);
        matrixStack.scale(-0.6f, -0.6f, 0.6f);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY, matrixStack, renderTypeBuffer, player.level(), 0);
        matrixStack.popPose();
    }
    
}
