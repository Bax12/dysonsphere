package de.bax.dysonsphere.network;

import net.minecraft.nbt.CompoundTag;

public interface IUpdateReceiverTile {
    
    public void handleUpdate(CompoundTag updateTag);

    public void sendGuiUpdate();

}
