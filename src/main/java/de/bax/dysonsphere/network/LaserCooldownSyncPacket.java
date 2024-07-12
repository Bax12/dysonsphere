package de.bax.dysonsphere.network;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class LaserCooldownSyncPacket {
    
    public final Map<Integer, Integer> laserCooldowns;
    public final int serverGameTick;
    public final int dsLaserCount;

    public LaserCooldownSyncPacket(Map<Integer, Integer> laserCooldowns, int serverGameTick, int dsLaserCount){
        this.laserCooldowns = laserCooldowns;
        this.serverGameTick = serverGameTick;
        this.dsLaserCount = dsLaserCount;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeInt(laserCooldowns.size());
        laserCooldowns.forEach((cooldown, laserCount) -> {
            buf.writeInt(cooldown - serverGameTick);
            buf.writeInt(laserCount);
        });
        buf.writeInt(dsLaserCount);
    }

    public static LaserCooldownSyncPacket decode(FriendlyByteBuf buf){
        int count = buf.readInt();
        Map<Integer, Integer> map = new TreeMap<>();
        for(int i = 0; i < count; i++){
            map.put(buf.readInt(), buf.readInt());
        }
        int dsLaserCount = buf.readInt();
        return new LaserCooldownSyncPacket(map, 0, dsLaserCount);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)){
            ctx.get().enqueueWork(() -> {
                Minecraft.getInstance().player.getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((laser) -> {
                    laser.load(new CompoundTag(), 0); //clearing the existing list
                    // DysonSphere.LOGGER.info("LaserCooldownSyncPacket handle laserCooldown Size: {}", laserCooldowns.size());
                    // DysonSphere.LOGGER.info("LaserCooldownSyncPacket handle dsLaserCount: {}", dsLaserCount);
                    laserCooldowns.forEach((cooldown, laserCount) -> {
                        // DysonSphere.LOGGER.info("LaserCooldownSyncPacket handle single laserCooldown: {}", cooldown);
                        // DysonSphere.LOGGER.info("LaserCooldownSyncPacket handle single laserCount: {}", laserCount);
                        laser.putLasersOnCooldown(Minecraft.getInstance().player.tickCount, laserCount, cooldown);
                    });
                    laser.setDysonSphereLaserCount(dsLaserCount);
                });
            });
            ctx.get().setPacketHandled(true);
        }
    }

}
