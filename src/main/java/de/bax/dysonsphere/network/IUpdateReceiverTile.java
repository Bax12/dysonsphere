package de.bax.dysonsphere.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface IUpdateReceiverTile {
    
    public void handleUpdate(CompoundTag updateTag, Player player);

    public void sendGuiUpdate();

}
