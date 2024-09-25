package de.bax.dysonsphere.compat.waila;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.tileentities.HeatGeneratorTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class HeatGeneratorDataProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final ResourceLocation ID = new ResourceLocation(DysonSphere.MODID, "heat_generator");
    public static HeatGeneratorDataProvider INSTANCE = new HeatGeneratorDataProvider();

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if(accessor.getBlockEntity() instanceof HeatGeneratorTile tile){
            tag.putInt("diff", tile.getHeatDifference());
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if(accessor.getServerData().contains("diff")){
            tooltip.add(Component.translatable("tooltip.dysonsphere.heat_generator_diff", AssetUtil.FLOAT_FORMAT.format(accessor.getServerData().getInt("diff"))));
        }
    }
    
}
