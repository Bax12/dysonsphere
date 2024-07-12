package de.bax.dysonsphere.network;

import java.util.Optional;

import de.bax.dysonsphere.DysonSphere;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModPacketHandler {
    
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DysonSphere.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void init(){
        int id = 0;
        INSTANCE.registerMessage(id++, DSEnergyReceiverGuiUpdatePacket.class, DSEnergyReceiverGuiUpdatePacket::encode, (buf) -> DSEnergyReceiverGuiUpdatePacket.decode(buf), DSEnergyReceiverGuiUpdatePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(id++, LaserPatternSyncPacket.class, LaserPatternSyncPacket::encode, (buf) -> LaserPatternSyncPacket.decode(buf), LaserPatternSyncPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(id++, LaserCooldownSyncRequestPackage.class, LaserCooldownSyncRequestPackage::encode, (buf) -> LaserCooldownSyncRequestPackage.decode(buf), LaserCooldownSyncRequestPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(id++, LaserPatternControllerGuiSwapPackage.class, LaserPatternControllerGuiSwapPackage::encode, (buf) -> LaserPatternControllerGuiSwapPackage.decode(buf), LaserPatternControllerGuiSwapPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(id++, LaserPatternActivatedPackage.class, LaserPatternActivatedPackage::encode, (buf) -> LaserPatternActivatedPackage.decode(buf), LaserPatternActivatedPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        

        INSTANCE.registerMessage(id++, LaserCooldownSyncPacket.class, LaserCooldownSyncPacket::encode, (buf) -> LaserCooldownSyncPacket.decode(buf), LaserCooldownSyncPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

}
