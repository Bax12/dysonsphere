package de.bax.dysonsphere.fluids;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

public class BasicGaseousFluid extends Fluid {

    protected final Supplier<Item> item;
    protected final String texturePath;
    protected final String description;
    protected final int temperature;

    public BasicGaseousFluid(Supplier<Item> steamBucket, String texturePath, String description, int temperature){
        this.item = steamBucket;
        this.texturePath = texturePath;
        this.description = description;
        this.temperature = temperature;
    }

    @Override
    public Item getBucket() {
        return item.get();
    }

    @Override
    protected boolean canBeReplacedWith(@Nonnull FluidState p_76127_, @Nonnull BlockGetter p_76128_, @Nonnull BlockPos p_76129_, @Nonnull Fluid p_76130_, @Nonnull Direction p_76131_) {
        return true;
    }

    @Override
    protected Vec3 getFlow(@Nonnull BlockGetter p_76110_, @Nonnull BlockPos p_76111_, @Nonnull FluidState p_76112_) {
        return Vec3.ZERO;
    }

    @Override
    public int getTickDelay(@Nonnull LevelReader p_76120_) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    public float getHeight(@Nonnull FluidState p_76124_, @Nonnull BlockGetter p_76125_, @Nonnull BlockPos p_76126_) {
        return 0;
    }

    @Override
    public float getOwnHeight(@Nonnull FluidState p_76123_) {
        return 0;
    }

    @Override
    protected BlockState createLegacyBlock(@Nonnull FluidState p_76136_) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSource(@Nonnull FluidState p_76140_) {
        return true;
    }

    @Override
    public int getAmount(@Nonnull FluidState p_76141_) {
        return 0;
    }

    @Override
    public VoxelShape getShape(@Nonnull FluidState p_76137_, @Nonnull BlockGetter p_76138_, @Nonnull BlockPos p_76139_) {
        return Shapes.empty();
    }

    @Override
    public FluidType getFluidType() {
        return new FluidType(FluidType.Properties.create().density(-200).temperature(temperature).viscosity(1).canSwim(false).canConvertToSource(false).descriptionId(description)){
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    @Override
                    public ResourceLocation getStillTexture() {
                        return new ResourceLocation(DysonSphere.MODID, texturePath);
                    }
                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return new ResourceLocation(DysonSphere.MODID, texturePath);
                    }
                });
            }
        };
    }
    
}
