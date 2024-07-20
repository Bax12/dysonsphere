package de.bax.dysonsphere.network;

import java.util.function.Supplier;

import de.bax.dysonsphere.gui.components.IButtonPressHandler;
import de.bax.dysonsphere.tileentities.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class GuiButtonPressedPackage {
    
    public final int buttonId;
    public final BlockPos pos;

    public GuiButtonPressedPackage(BlockPos pos, int buttonId){
        this.pos = pos;
        this.buttonId = buttonId;
    }

    public void encode(FriendlyByteBuf buf){
        buf.writeBlockPos(pos);
        buf.writeInt(buttonId);
    }

    public static GuiButtonPressedPackage decode(FriendlyByteBuf buf){
        BlockPos pos = buf.readBlockPos();
        int buttonId = buf.readInt();
        return new GuiButtonPressedPackage(pos, buttonId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            Level level = ctx.get().getSender().level();
            if(level.isLoaded(pos)){
                BlockEntity tile = level.getBlockEntity(pos);
                if(tile != null && tile instanceof IButtonPressHandler handler){
                    handler.handleButtonPress(buttonId, ctx.get().getSender());
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }




}
