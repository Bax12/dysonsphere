package de.bax.dysonsphere.compat.waila;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.tileentities.LaserControllerTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class LaserControllerDataProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final ResourceLocation ID = new ResourceLocation(DysonSphere.MODID, "laser_controller");
    public static LaserControllerDataProvider INSTANCE = new LaserControllerDataProvider();

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        if(accessor.getBlockEntity() instanceof LaserControllerTile tile){
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("x", tile.getTargetX());
            nbt.putInt("y", tile.getTargetY());
            nbt.putInt("z", tile.getTargetZ());
            nbt.putString("name", tile.getPattern().name);
            nbt.putString("sequence", tile.getPattern().getCallInSequence());
            if(tile.getOwner() != null){
                nbt.putUUID("owner", tile.getOwner().getUUID());
            }
            nbt.putInt("state", tile.isWorking() ? 1 : tile.isOnCooldown() ? 2 : 0);
            tag.put("data", nbt);
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if(accessor.getServerData().contains("data")){
            CompoundTag tag = accessor.getServerData().getCompound("data");
            tooltip.add(Component.translatable("tooltip.dysonsphere.laser_controller_target", tag.getInt("x"), tag.getInt("y"), tag.getInt("z")));
            tooltip.add(Component.translatable("tooltip.dysonsphere.orbital_laser_hud_pattern_name", tag.getString("name"), tag.getString("sequence")));
            if(tag.contains("owner")){
                Player player = accessor.getLevel().getPlayerByUUID(tag.getUUID("owner"));
                if(player != null){
                    tooltip.add(Component.translatable("tooltip.dysonsphere.laser_controller_owner", player.getDisplayName()));
                }
            } else {
                tooltip.add(Component.translatable("tooltip.dysonsphere.laser_controller_no_owner"));
            }
            switch (tag.getInt("state")) {
                case 1: {
                    tooltip.add(Component.translatable("tooltip.dysonsphere.laser_controller_launching"));
                    break;
                }
                case 2: {
                    tooltip.add(Component.translatable("tooltip.dysonsphere.laser_controller_cooldown"));
                    break;
                }
            }
            
            
        }
    }
    
}
