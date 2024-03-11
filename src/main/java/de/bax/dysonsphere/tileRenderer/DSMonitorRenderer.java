package de.bax.dysonsphere.tileRenderer;

import java.util.Locale;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.joml.Matrix4f;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import de.bax.dysonsphere.tileentities.DSMonitorTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

public class DSMonitorRenderer implements BlockEntityRenderer<DSMonitorTile> {

    public DSMonitorRenderer(BlockEntityRendererProvider.Context context){

    }


    // adapted from net.minecraft.client.renderer.entity.EntityRenderer:renderNamerTag()
    @Override
    public void render(@Nonnull DSMonitorTile tile, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();

        Direction facing = tile.getLevel().getBlockState(tile.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING);

        switch (facing) {
            case NORTH:
                poseStack.translate(0.8F, 0.8F, 0.124F);
                break;
            case EAST:
                poseStack.mulPose(Axis.YN.rotationDegrees(90f));
                poseStack.translate(0.8F, 0.8F, -0.876F);
                break;
            case SOUTH:
                poseStack.mulPose(Axis.YN.rotationDegrees(180f));
                poseStack.translate(-0.2F, 0.8F, -0.876F);
                break;
            case WEST:
                poseStack.mulPose(Axis.YN.rotationDegrees(270f));
                poseStack.translate(-0.2F, 0.8F, 0.124F);
                break;
        }

        // poseStack.pushPose();

        // poseStack.translate(-60F, 0F, 60F);
        // poseStack.mulPose(Axis.YP.rotationDegrees(90));
        
        float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int j = (int) (backgroundOpacity * 255.0F) << 24;
        poseStack.scale(-0.005F, -0.005F, 0.005F);
        Font font = Minecraft.getInstance().font;
        Matrix4f matrix = poseStack.last().pose();

        
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(5);
        // DysonSphere.LOGGER.info("DSMonitorRenderer render dscompletion: {}", tile.getDsCompletionPercentage()); //tile delivers 0.0 wrongfully.
        font.drawInBatch(Component.translatable("tooltip.dysonsphere.ds_monitor_status"), 0/*width adjust*/, 0/*line feed*/, -1, false, matrix, bufferSource, DisplayMode.NORMAL, j, combinedLight);
        font.drawInBatch(Component.translatable("tooltip.dysonsphere.ds_monitor_completion", df.format(tile.getDsCompletionPercentage())), 0, 10F, -1, false, matrix, bufferSource, DisplayMode.NORMAL, j, combinedLight);
        df.setMaximumFractionDigits(0);
        font.drawInBatch(Component.translatable("tooltip.dysonsphere.ds_monitor_capacity", df.format(tile.getDsEnergy())), 0, 20F, -1, false, matrix, bufferSource, DisplayMode.NORMAL, j, combinedLight);
        font.drawInBatch(Component.translatable("tooltip.dysonsphere.ds_monitor_parts"), 0, 30F, -1, false, matrix, bufferSource, DisplayMode.NORMAL, j, combinedLight);
        float drawOffset = 40f;
        for (Entry<Item, Integer> entry : tile.getDsParts().entrySet()){
            poseStack.pushPose();
            var comp = Component.translatable("tooltip.dysonsphere.ds_monitor_part", entry.getKey().getName(ItemStack.EMPTY), entry.getValue());
            // DysonSphere.LOGGER.info("DSMonitorRenderer render fontWidth: {}", font.width(comp));
            float scale = Math.min(125F / font.width(comp), 1f);
            // scale = 2f;
            poseStack.scale(scale, 1, 1);
            matrix = poseStack.last().pose();
            font.drawInBatch(comp, 0, drawOffset, -1, false, matrix, bufferSource, DisplayMode.NORMAL, j, combinedLight);
            drawOffset += 10f;
            poseStack.popPose();
        }
        // Entry<Item, Integer> part = ds.getDysonSphereParts().entrySet().iterator().next();
        // font.drawInBatch(Component.literal("  - " + part.getKey().getName(ItemStack.EMPTY) + ":" + part.getValue()), 0, 40F, -1, false, matrix, bufferSource, DisplayMode.NORMAL, j, combinedLight);
        

        
        // font.drawInBatch(Component.literal(">Dyson Sphere Status"), 0/*width adjust*/, 0/*line feed*/, -1, false, matrix, bufferSource, DisplayMode.NORMAL, j, combinedLight);
        // font.drawInBatch(Component.literal("Completion: 0.00008%"), 0, 10F, -1, false, matrix, bufferSource, DisplayMode.NORMAL, j, combinedLight);
        // font.drawInBatch(Component.literal("Capacity: 80RF/t"), 0, 20F, -1, false, matrix, bufferSource, DisplayMode.NORMAL, j, combinedLight);
        // font.drawInBatch(Component.literal("Parts:"), 0, 30F, -1, false, matrix, bufferSource, DisplayMode.NORMAL, j, combinedLight);
        // font.drawInBatch(Component.literal("  - Solar Capsule: 15"), 0, 40F, -1, false, matrix, bufferSource, DisplayMode.NORMAL, j, combinedLight);


        // poseStack.popPose();
        poseStack.popPose();
    }
    
}
