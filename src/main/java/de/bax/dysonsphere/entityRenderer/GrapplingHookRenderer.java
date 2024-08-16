package de.bax.dysonsphere.entityRenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import de.bax.dysonsphere.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GrapplingHookRenderer extends EntityRenderer<GrapplingHookEntity> {

    public static final ResourceLocation RES_LOC = new ResourceLocation(DysonSphere.MODID, "grappling_hook");
    // protected ItemStack displayStack = ItemStack.EMPTY;

    public GrapplingHookRenderer(Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(GrapplingHookEntity pEntity) {
        return RES_LOC;
    }
    
    @Override
    public void render(GrapplingHookEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        ItemStack displayStack = ItemStack.EMPTY;
        if(displayStack.isEmpty() && pEntity.getOwner() instanceof Player player){
            displayStack = player.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).map((gContainer) -> {
                return gContainer.getGrapplingHookFrame().flatMap((gFrame) -> {
                    return gFrame.getHookIcon(player.level(), player).map((stack) -> {
                        return stack;
                    });
                }).orElse(ItemStack.EMPTY);
            }).orElse(displayStack);
        }
        pPoseStack.pushPose();
        pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        Minecraft.getInstance().getItemRenderer().renderStatic(displayStack.isEmpty() ? ModItems.GRAPPLING_HOOK_HARNESS.get().getDefaultInstance() : displayStack, ItemDisplayContext.NONE, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, null, 0);
        pPoseStack.popPose();
    }
}
