package de.bax.dysonsphere.network;

import java.util.function.Supplier;

import de.bax.dysonsphere.capabilities.dysonSphere.DysonSphereContainer;
import de.bax.dysonsphere.capabilities.dysonSphere.IDysonSphereContainer;
import de.bax.dysonsphere.constructs.ModConstructs;
import de.bax.dysonsphere.util.SkyLightUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class DSLightSyncPackage {
    
    public final float darkenBy;

    public DSLightSyncPackage(float darkenBy){
        this.darkenBy = darkenBy;
    }

    public DSLightSyncPackage(IDysonSphereContainer ds){
        this.darkenBy = ds.getEnabledConstructs().contains(ModConstructs.SOLAR_LAMP.get()) ? 0 : ds.getCompletionPercentage() / DysonSphereContainer.DS_COMPLETED; //completion is 0 - 100, light is 0-1
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeFloat(darkenBy);
    }

    public static DSLightSyncPackage decode(FriendlyByteBuf buf){
        return new DSLightSyncPackage(buf.readFloat());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)){
            SkyLightUtil.darkenBy = this.darkenBy;
            ctx.get().setPacketHandled(true);
        }
    }

}
