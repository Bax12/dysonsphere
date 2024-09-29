package de.bax.dysonsphere.compat.waila;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.tileentities.DSEnergyReceiverTile;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class DSEnergyReceiverDataProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final ResourceLocation ID = new ResourceLocation(DysonSphere.MODID, "ds_energy_receiver");
    public static DSEnergyReceiverDataProvider INSTANCE = new DSEnergyReceiverDataProvider();

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if(accessor.getBlockEntity() instanceof DSEnergyReceiverTile tile){
            tag.putInt("target", tile.getDsPowerDraw());
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if(accessor.getServerData().contains("target")){
            tooltip.add(Component.translatable("tooltip.dysonsphere.ds_energy_receiver_energy").append(" "));
            tooltip.append(Component.translatable("tooltip.dysonsphere.heat_generator_production", AssetUtil.FLOAT_FORMAT.format(accessor.getServerData().getInt("target"))).withStyle(ChatFormatting.WHITE));
        }
    }
    
}
