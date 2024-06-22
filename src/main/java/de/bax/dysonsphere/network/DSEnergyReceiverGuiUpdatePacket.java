package de.bax.dysonsphere.network;

import java.util.function.Supplier;

import de.bax.dysonsphere.tileentities.DSEnergyReceiverTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class DSEnergyReceiverGuiUpdatePacket {
    
    public final int powerDraw;
    public final BlockPos pos;


    public DSEnergyReceiverGuiUpdatePacket(int powerDraw, BlockPos pos){
        this.pos = pos;
        this.powerDraw = powerDraw;

    }


    public void encode(FriendlyByteBuf buf){
        buf.writeBlockPos(pos);
        buf.writeInt(powerDraw);
    }

    public static DSEnergyReceiverGuiUpdatePacket decode(FriendlyByteBuf buf){
        BlockPos pos = buf.readBlockPos();
        int targetHeat = buf.readInt();
        return new DSEnergyReceiverGuiUpdatePacket(targetHeat, pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            Level level = ctx.get().getSender().level();
            if(level.isLoaded(pos)){
                BlockEntity tile = level.getBlockEntity(pos);
                if(tile != null && tile.getType().equals(ModTiles.DS_ENERGY_RECEIVER.get())){
                    ((DSEnergyReceiverTile) tile).setDsPowerDraw(powerDraw);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }



}
