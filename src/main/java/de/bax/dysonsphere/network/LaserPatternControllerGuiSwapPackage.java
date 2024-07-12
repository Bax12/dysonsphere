package de.bax.dysonsphere.network;

import java.util.function.Supplier;

import de.bax.dysonsphere.containers.LaserPatternControllerContainer;
import de.bax.dysonsphere.containers.LaserPatternControllerInventoryContainer;
import de.bax.dysonsphere.tileentities.LaserPatternControllerTile;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

public class LaserPatternControllerGuiSwapPackage {
    
    public final boolean swapToInventoryView;
    public final BlockPos pos;

    public LaserPatternControllerGuiSwapPackage(boolean swapToInventoryView, BlockPos pos){
        this.swapToInventoryView = swapToInventoryView;
        this.pos = pos;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeBoolean(swapToInventoryView);
        buf.writeBlockPos(pos);
    }

    public static LaserPatternControllerGuiSwapPackage decode(FriendlyByteBuf buf){
        boolean invView = buf.readBoolean();
        BlockPos pos = buf.readBlockPos();
        return new LaserPatternControllerGuiSwapPackage(invView, pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)){
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                Level level = player.level();
                if(level.isLoaded(pos)){
                    BlockEntity tile = level.getBlockEntity(pos);
                    if(tile != null && tile.getType().equals(ModTiles.LASER_PATTERN_CONTROLLER.get())){
                        if(!swapToInventoryView){
                            if(((LaserPatternControllerTile)tile).hasMinEnergy()){
                                ((LaserPatternControllerTile)tile).consumeEnergy();
                            } else {
                                player.displayClientMessage(Component.literal("not enough energy"), true);
                                return;
                            }
                        }
                        NetworkHooks.openScreen(player, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                        swapToInventoryView ? new LaserPatternControllerInventoryContainer(containerId, playerInventory, (LaserPatternControllerTile) tile) : new LaserPatternControllerContainer(containerId, playerInventory, (LaserPatternControllerTile) tile), Component.translatable("container.dysonsphere.laser_pattern_controller")), pos);
                    }
                }
            });
        }
    }

}
