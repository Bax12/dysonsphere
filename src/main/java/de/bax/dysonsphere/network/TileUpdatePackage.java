package de.bax.dysonsphere.network;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class TileUpdatePackage {
    
    public final CompoundTag updateTag;
    public final BlockPos pos;

    public TileUpdatePackage(CompoundTag updateTag, BlockPos pos){
        this.updateTag = updateTag;
        this.pos = pos;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeNbt(updateTag);
        buf.writeBlockPos(pos);
    }

    public static TileUpdatePackage decode(FriendlyByteBuf buf){
        CompoundTag updateTag = buf.readNbt();
        BlockPos pos = buf.readBlockPos();
        return new TileUpdatePackage(updateTag, pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)){
            ctx.get().enqueueWork(() -> {
                Level level = ctx.get().getSender().level();
                if(level.isLoaded(pos)){
                    BlockEntity tile = level.getBlockEntity(pos);
                    if(tile != null && tile instanceof IUpdateReceiverTile rec){
                        rec.handleUpdate(updateTag);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

}
