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
        // INSTANCE.registerMessage(id++, DSEnergyReceiverGuiUpdatePackage.class, DSEnergyReceiverGuiUpdatePackage::encode, (buf) -> DSEnergyReceiverGuiUpdatePackage.decode(buf), DSEnergyReceiverGuiUpdatePackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        // INSTANCE.registerMessage(id++, LaserPatternSyncPacket.class, LaserPatternSyncPacket::encode, (buf) -> LaserPatternSyncPacket.decode(buf), LaserPatternSyncPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(id++, LaserCooldownSyncRequestPackage.class, LaserCooldownSyncRequestPackage::encode, (buf) -> LaserCooldownSyncRequestPackage.decode(buf), LaserCooldownSyncRequestPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        // INSTANCE.registerMessage(id++, LaserPatternControllerGuiSwapPackage.class, LaserPatternControllerGuiSwapPackage::encode, (buf) -> LaserPatternControllerGuiSwapPackage.decode(buf), LaserPatternControllerGuiSwapPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(id++, LaserPatternActivatedPackage.class, LaserPatternActivatedPackage::encode, (buf) -> LaserPatternActivatedPackage.decode(buf), LaserPatternActivatedPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(id++, GuiButtonPressedPackage.class, GuiButtonPressedPackage::encode, (buf) -> GuiButtonPressedPackage.decode(buf), GuiButtonPressedPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(id++, TileUpdatePackage.class, TileUpdatePackage::encode, (buf) -> TileUpdatePackage.decode(buf), TileUpdatePackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(id++, GrapplingHookActionPackage.class, GrapplingHookActionPackage::encode, (buf) -> GrapplingHookActionPackage.decode(buf), GrapplingHookActionPackage::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(id++, LaserCooldownSyncPackage.class, LaserCooldownSyncPackage::encode, (buf) -> LaserCooldownSyncPackage.decode(buf), LaserCooldownSyncPackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(id++, GrapplingHookSyncPackage.class, GrapplingHookSyncPackage::encode, (buf) -> GrapplingHookSyncPackage.decode(buf), GrapplingHookSyncPackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(id++, DSLightSyncPackage.class, DSLightSyncPackage::encode, (buf) -> DSLightSyncPackage.decode(buf), DSLightSyncPackage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

}
