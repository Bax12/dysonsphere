package de.bax.dysonsphere.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class LaserPatternSyncPacket {
    
    public final List<OrbitalLaserAttackPattern> patterns;

    public LaserPatternSyncPacket(List<OrbitalLaserAttackPattern> patterns){
        this.patterns = patterns;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeInt(patterns.size());
        patterns.forEach((pattern) -> {
            buf.writeNbt(pattern.serializeNBT());
        });
    }

    public static LaserPatternSyncPacket decode(FriendlyByteBuf buf){
        List<OrbitalLaserAttackPattern> patterns = new ArrayList<>();
        int count = buf.readInt();
        for(int i = 0; i < count; i++){
            OrbitalLaserAttackPattern pattern = new OrbitalLaserAttackPattern();
            pattern.deserializeNBT(buf.readNbt());
            patterns.add(pattern);
        }
        return new LaserPatternSyncPacket(patterns);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)){
            ctx.get().enqueueWork(() -> {
                Minecraft.getInstance().player.getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((laser) -> {
                    laser.setActivePatterns(patterns);
                });
            });
            ctx.get().setPacketHandled(true);
        }
        if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)){
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                player.getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((orbitalLaser) -> {
                    orbitalLaser.setActivePatterns(patterns);
                });
            });
            ctx.get().setPacketHandled(true);
        }
    }

}
