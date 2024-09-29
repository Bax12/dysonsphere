package de.bax.dysonsphere.compat.waila;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class HeatDataProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {


    public static final ResourceLocation ID = new ResourceLocation(DysonSphere.MODID, "heat");
    public static HeatDataProvider INSTANCE = new HeatDataProvider();

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if(accessor.getBlockEntity() instanceof IHeatTile tile){
            tag.putInt("heat", (int) Math.round(tile.getHeat()));
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if(accessor.getServerData().contains("heat")){
            tooltip.add(Component.translatable("tooltip.dysonsphere.heat_current", AssetUtil.FLOAT_FORMAT.format(accessor.getServerData().getInt("heat"))));
        }
    }
    
}
