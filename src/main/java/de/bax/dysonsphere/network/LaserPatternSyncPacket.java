package de.bax.dysonsphere.network;

import java.util.function.Supplier;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import de.bax.dysonsphere.tileentities.LaserPatternControllerTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class LaserPatternSyncPacket {
    
    public final OrbitalLaserAttackPattern pattern;
    public final BlockPos pos;

    public LaserPatternSyncPacket(OrbitalLaserAttackPattern pattern, BlockPos pos){
        this.pattern = pattern;
        this.pos = pos;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeNbt(pattern.serializeNBT());
        buf.writeBlockPos(pos);
    }

    public static LaserPatternSyncPacket decode(FriendlyByteBuf buf){
        OrbitalLaserAttackPattern pattern = new OrbitalLaserAttackPattern();
        pattern.deserializeNBT(buf.readNbt());
        BlockPos pos = buf.readBlockPos();
        return new LaserPatternSyncPacket(pattern, pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)){
            ctx.get().enqueueWork(() -> {
                Level level = ctx.get().getSender().level();
                if(pattern.isValid() && level.isLoaded(pos)){
                    BlockEntity tile = level.getBlockEntity(pos);
                        if(tile != null && tile.getType().equals(ModTiles.LASER_PATTERN_CONTROLLER.get())){
                            ItemStack stack = ((LaserPatternControllerTile) tile).inventory.getStackInSlot(0).copy();
                            if(!stack.isEmpty()){
                                stack.getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER).ifPresent((container) -> {
                                    container.setPattern(pattern);
                                });
                                ((LaserPatternControllerTile) tile).inventory.setStackInSlot(0, stack);
                            }
                        }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

}
