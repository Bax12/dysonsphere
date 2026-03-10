package de.bax.dysonsphere.util;

import java.text.NumberFormat;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AssetUtil {

    public static final NumberFormat FLOAT_FORMAT = NumberFormat.getNumberInstance(Locale.ENGLISH);
    
    @Nonnull
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

    @OnlyIn(Dist.CLIENT)
    public static void renderMaxWidthString(@Nonnull GuiGraphics guiGraphics,@Nonnull Component comp, int x, int y, float width, int color, int backgroundColor, int lightIn, boolean shadow){
        guiGraphics.pose().pushPose();
        Font font = Minecraft.getInstance().font;

        float scale = Math.min(width / font.width(comp), 1f);
        guiGraphics.pose().translate(x, y, 0);
        guiGraphics.pose().scale(scale, 1, 1);

        font.drawInBatch(comp, 0, 0, color, shadow, guiGraphics.pose().last().pose(), guiGraphics.bufferSource(), DisplayMode.NORMAL, backgroundColor, lightIn);

        guiGraphics.pose().popPose();
    }

}
