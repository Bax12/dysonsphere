package de.bax.dysonsphere.util;

import java.text.NumberFormat;
import java.util.Locale;

import com.mojang.blaze3d.vertex.PoseStack;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AssetUtil {

    public static final NumberFormat FLOAT_FORMAT = NumberFormat.getNumberInstance(Locale.ENGLISH);
    
    public static ResourceLocation getGuiLocation(String file){
        return new ResourceLocation(DysonSphere.MODID, "textures/gui/" + file + ".png");
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderItemInWorld(ItemStack stack, int combinedLight, int combinedOverlay, PoseStack matrices, MultiBufferSource buffer) {
        if (!stack.isEmpty()) {
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, matrices, buffer, null, 0
            );
        }
    }

}
