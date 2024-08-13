package de.bax.dysonsphere.network;

import java.util.function.Supplier;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class GrapplingHookSyncPackage {
    
    CompoundTag nbt;

    public GrapplingHookSyncPackage(CompoundTag nbt){
        this.nbt = nbt;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeNbt(nbt);
    }

    public static GrapplingHookSyncPackage decode(FriendlyByteBuf buf){
        return new GrapplingHookSyncPackage(buf.readNbt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)){
            ctx.get().enqueueWork(() -> {
                Minecraft.getInstance().player.getCapability(DSCapabilities.GRAPPLING_HOOK).ifPresent((hookContainer) -> {
                    hookContainer.load(nbt);
                });
            });
            ctx.get().setPacketHandled(true);
        }

    }

    public static void sendSyncPackage(ServerPlayer player, IGrapplingHookContainer hookContainer){
        ModPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(()->player), new GrapplingHookSyncPackage(hookContainer.save()));
    }

}
