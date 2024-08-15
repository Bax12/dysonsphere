package de.bax.dysonsphere.network;

import java.util.function.Supplier;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class GrapplingHookActionPackage {
    
    protected final int action; //0 = deploy, 1 = recall single, 2 = recall all, 3 = start/stop pulling, 4 = start/stop unwinding

    public GrapplingHookActionPackage(int action){
        this.action = action;
    }


    public void encode(FriendlyByteBuf buf){
        buf.writeInt(action);
    }

    public static GrapplingHookActionPackage decode(FriendlyByteBuf buf){
        return new GrapplingHookActionPackage(buf.readInt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)){
            ctx.get().enqueueWork(() -> {
                Player player = ctx.get().getSender();
                player.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
                    switch (action) {
                        case 0:
                            hookContainer.deployHook();
                            break;
                        case 1:
                            hookContainer.recallSingleHook();
                            break;
                        case 2:
                            hookContainer.recallAll();
                            break;
                        case 3:
                            hookContainer.togglePulling();
                            break;
                        case 4:
                            hookContainer.toggleUnwinding();
                            break;
                    }
                });
                DysonSphere.LOGGER.info("GrapplingHookActionPackage: Action: {}", action);
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendDeployPackage(){
        ModPacketHandler.INSTANCE.sendToServer(new GrapplingHookActionPackage(0));
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendRecallSinglePackage(){
        ModPacketHandler.INSTANCE.sendToServer(new GrapplingHookActionPackage(1));
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendRecallAllPackage(){
        ModPacketHandler.INSTANCE.sendToServer(new GrapplingHookActionPackage(2));
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendPullPackage(){
        ModPacketHandler.INSTANCE.sendToServer(new GrapplingHookActionPackage(3));
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendUnwindPackage(){
        ModPacketHandler.INSTANCE.sendToServer(new GrapplingHookActionPackage(4));
    }

}
