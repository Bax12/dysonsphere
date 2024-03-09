package de.bax.dysonsphere.network;

import java.util.function.Supplier;

import de.bax.dysonsphere.tileentities.DSEnergyReceiverTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class DSEnergyReceiverGuiUpdatePacket {
    
    public final int x,y,z,powerDraw;


    public DSEnergyReceiverGuiUpdatePacket(int targetHeat, int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.powerDraw = targetHeat;

    }


    public void encode(FriendlyByteBuf buf){
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(powerDraw);
    }

    public static DSEnergyReceiverGuiUpdatePacket decode(FriendlyByteBuf buf){
        int x,y,z,targetHeat;
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        targetHeat = buf.readInt();
        return new DSEnergyReceiverGuiUpdatePacket(targetHeat,x,y,z);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            BlockEntity tile = ctx.get().getSender().level().getBlockEntity(new BlockPos(x, y, z));
            if(tile != null && tile.getType().equals(ModTiles.DS_ENERGY_RECEIVER.get())){
                ((DSEnergyReceiverTile) tile).setDsPowerDraw(powerDraw);
            }

        });
        ctx.get().setPacketHandled(true);
    }



}
