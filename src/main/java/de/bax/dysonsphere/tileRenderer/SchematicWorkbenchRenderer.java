package de.bax.dysonsphere.tileRenderer;

import java.util.Optional;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.tileentities.SchematicWorkbenchTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SchematicWorkbenchRenderer implements BlockEntityRenderer<SchematicWorkbenchTile> {

    protected static final ResourceLocation RES_LOC = new ResourceLocation(DysonSphere.MODID, "block/schematic_workbench_renderer");
    protected static TextureAtlasSprite sprite;


    public SchematicWorkbenchRenderer(BlockEntityRendererProvider.Context context) {
        sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(RES_LOC);
    }

    @Override
    public void render(SchematicWorkbenchTile pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();

        BlockState state = pBlockEntity.getLevel().getBlockState(pBlockEntity.getBlockPos());
        Optional<Direction> facing = state.getOptionalValue(HorizontalDirectionalBlock.FACING);
        if(facing.isPresent()){
            switch(facing.get()){
                case EAST:
                    pPoseStack.translate(1, 0, 0);
                    pPoseStack.mulPose(Axis.YN.rotationDegrees(90));
                    break;
                case SOUTH:
                    pPoseStack.translate(1, 0, 1);
                    pPoseStack.mulPose(Axis.YP.rotationDegrees(180));
                    break;
                case WEST:
                    pPoseStack.translate(0, 0, 1);
                    pPoseStack.mulPose(Axis.YP.rotationDegrees(90));
                    break;
            }
        }
        

        //0-8 upper row, 9-17 lower row
        //0 leftmost, 8 rightmost
        for(int i = 0; i < pBlockEntity.inventory.getSlots(); i++){
            ItemStack stack = pBlockEntity.inventory.getStackInSlot(i);
            if(stack.isEmpty()) continue;
            pPoseStack.pushPose();
            pPoseStack.translate(1 - (0.125 + ((i % 9) * 0.09375f)), i < 9 ? 0.5f : 0.0625f, 0.28125);
            renderCuboid(pPoseStack, pBuffer, sprite, 0.09375f, 0.375f, 0.375f, pPackedLight);


            pPoseStack.popPose();
        }

        pPoseStack.popPose();
    }

        public static void renderCuboid(PoseStack poseStack, MultiBufferSource buffer, TextureAtlasSprite sprite, float width, float height, float depth, int light) {

        poseStack.pushPose();
        poseStack.translate(width / -2f, 0, depth / -2f);
        
        VertexConsumer vc = buffer.getBuffer(RenderType.solid());

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();

        float uW = minU + (maxU - minU) * width;
        float vH = minV + (maxV - minV) * height;
        float uD = minU + (maxU - minU) * depth;
        float vD = minV + (maxV - minV) * depth;

        // FRONT
        OreSpireRenderer.quad(vc, pose, normal,
                0, 0, depth,
                width, 0, depth,
                width, height, depth,
                0, height, depth,
                minU, minV, uW, vH,
                0, 0, 1,
                light);

        // BACK
        OreSpireRenderer.quad(vc, pose, normal,
                width, 0, 0,
                0, 0, 0,
                0, height, 0,
                width, height, 0,
                minU, minV, uW, vH,
                0, 0, -1,
                light);

        // LEFT
        OreSpireRenderer.quad(vc, pose, normal,
                0, 0, 0,
                0, 0, depth,
                0, height, depth,
                0, height, 0,
                minU, minV, uD, vH,
                -1, 0, 0,
                light);

        // RIGHT
        OreSpireRenderer.quad(vc, pose, normal,
                width, 0, depth,
                width, 0, 0,
                width, height, 0,
                width, height, depth,
                minU, minV, uD, vH,
                1, 0, 0,
                light);

        // TOP
        OreSpireRenderer.quad(vc, pose, normal,
                0, height, depth,
                width, height, depth,
                width, height, 0,
                0, height, 0,
                minU, minV, uW, vD,
                0, 1, 0,
                light);

        // BOTTOM
        OreSpireRenderer.quad(vc, pose, normal,
                0, 0, 0,
                width, 0, 0,
                width, 0, depth,
                0, 0, depth,
                minU, minV, uW, vD,
                0, -1, 0,
                light);

        poseStack.popPose();
    }
    
}
