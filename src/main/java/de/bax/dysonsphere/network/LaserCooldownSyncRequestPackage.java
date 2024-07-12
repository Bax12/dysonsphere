package de.bax.dysonsphere.network;

import java.util.function.Supplier;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class LaserCooldownSyncRequestPackage {
    
    public LaserCooldownSyncRequestPackage(){

    }

    public void encode(FriendlyByteBuf buf){

    }

    public static LaserCooldownSyncRequestPackage decode(FriendlyByteBuf buf){
        return new LaserCooldownSyncRequestPackage();
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)){
            ctx.get().enqueueWork(() -> {
                var player = ctx.get().getSender();
                player.getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((laser) -> {
                    laser.getLasersAvailable(player.tickCount);
                });
            });
        }
    }

}
