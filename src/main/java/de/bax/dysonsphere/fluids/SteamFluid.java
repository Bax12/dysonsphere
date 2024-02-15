package de.bax.dysonsphere.fluids;

import de.bax.dysonsphere.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SteamFluid extends Fluid {

    @Override
    public Item getBucket() {
        return ModItems.STEAM_BUCKET.get();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState p_76127_, BlockGetter p_76128_, BlockPos p_76129_, Fluid p_76130_, Direction p_76131_) {
        return true;
    }

    @Override
    protected Vec3 getFlow(BlockGetter p_76110_, BlockPos p_76111_, FluidState p_76112_) {
        return Vec3.ZERO;
    }

    @Override
    public int getTickDelay(LevelReader p_76120_) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    public float getHeight(FluidState p_76124_, BlockGetter p_76125_, BlockPos p_76126_) {
        return 0;
    }

    @Override
    public float getOwnHeight(FluidState p_76123_) {
        return 0;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState p_76136_) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState p_76140_) {
        return true;
    }

    @Override
    public int getAmount(FluidState p_76141_) {
        return 0;
    }

    @Override
    public VoxelShape getShape(FluidState p_76137_, BlockGetter p_76138_, BlockPos p_76139_) {
        return Shapes.empty();
    }
    
}
