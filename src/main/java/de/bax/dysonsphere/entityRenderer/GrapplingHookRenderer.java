package de.bax.dysonsphere.entityRenderer;

import com.mojang.blaze3d.vertex.PoseStack;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import de.bax.dysonsphere.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GrapplingHookRenderer extends EntityRenderer<GrapplingHookEntity> {

    public static final ResourceLocation RES_LOC = new ResourceLocation(DysonSphere.MODID, "grappling_hook");

    public GrapplingHookRenderer(Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(GrapplingHookEntity pEntity) {
        return RES_LOC;
    }
    
    @Override
    public void render(GrapplingHookEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(ModItems.GRAPPLING_HOOK_HARNESS.get()), ItemDisplayContext.NONE, pPackedLight, 1, pPoseStack, pBuffer, null, 0);
    }
}
