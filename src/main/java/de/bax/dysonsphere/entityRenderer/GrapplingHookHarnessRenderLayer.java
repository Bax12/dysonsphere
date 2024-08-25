package de.bax.dysonsphere.entityRenderer;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import de.bax.dysonsphere.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;

public class GrapplingHookHarnessRenderLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public GrapplingHookHarnessRenderLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(@Nonnull PoseStack pPoseStack, @Nonnull MultiBufferSource pBuffer, int pPackedLight, @Nonnull T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if(getParentModel() instanceof HumanoidModel hModel){
            hModel.body.translateAndRotate(pPoseStack);
            pPoseStack.translate(0f, 0.4f, 0.08f);
            pPoseStack.scale(-0.6f, -0.6f, 0.6f);
            pLivingEntity.getArmorSlots().forEach((armor) -> {
                if(armor.is(ModItems.GRAPPLING_HOOK_HARNESS.get())){
                    Minecraft.getInstance().getItemRenderer().renderStatic(armor, ItemDisplayContext.NONE, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pLivingEntity.level(), 0);
                }
            });
            
        }
    }
    
}
