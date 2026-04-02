package de.bax.dysonsphere.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserPlayerContainer.OrbitalLaserContainer.LaserCooldown;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class LaserCooldownSyncPackage {
    
    public final Collection<LaserCooldown> laserCooldowns; //Serverside gametick is relative to servertick, clientside gametick is absolute 
    public final int serverGameTick;
    public final int dsLaserCount;

    public LaserCooldownSyncPackage(Collection<LaserCooldown> laserCooldowns, int serverGameTick, int dsLaserCount){
        this.laserCooldowns = laserCooldowns;
        this.serverGameTick = serverGameTick;
        this.dsLaserCount = dsLaserCount;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeInt(laserCooldowns.size());
        laserCooldowns.forEach((cooldown) -> {
            buf.writeInt(cooldown.gameTick() - serverGameTick);
            buf.writeInt(cooldown.count());
        });
        buf.writeInt(dsLaserCount);
    }

    public static LaserCooldownSyncPackage decode(FriendlyByteBuf buf){
        int count = buf.readInt();
        Collection<LaserCooldown> cooldowns = new ArrayList<>(count);
        for(int i = 0; i < count; i++){
            cooldowns.add(new LaserCooldown(buf.readInt(), buf.readInt()));
        }
        int dsLaserCount = buf.readInt();
        return new LaserCooldownSyncPackage(cooldowns, 0, dsLaserCount);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)){
            ctx.get().enqueueWork(() -> {
                Minecraft.getInstance().player.getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((laser) -> {
                    laser.load(new CompoundTag(), 0); //clearing the existing list
                    // DysonSphere.LOGGER.info("LaserCooldownSyncPacket handle laserCooldown Size: {}", laserCooldowns.size());
                    // DysonSphere.LOGGER.info("LaserCooldownSyncPacket handle dsLaserCount: {}", dsLaserCount);
                    laserCooldowns.forEach((cooldown) -> {
                        // DysonSphere.LOGGER.info("LaserCooldownSyncPacket handle single laserCooldown: {}", cooldown);
                        // DysonSphere.LOGGER.info("LaserCooldownSyncPacket handle single laserCount: {}", laserCount);
                        laser.putLasersOnCooldown(Minecraft.getInstance().player.tickCount, cooldown.count(), cooldown.gameTick());
                    });
                    laser.setDysonSphereLaserCount(dsLaserCount);
                });
            });
            ctx.get().setPacketHandled(true);
        }
    }

}
