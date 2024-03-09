package de.bax.dysonsphere.tileentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public abstract class BaseTile extends BlockEntity {

    public BaseTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    @javax.annotation.Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    public void sendSyncPackageToNearbyPlayers() {
        ServerLevel serverLevel = (ServerLevel) this.getLevel();
        LevelChunk chunk = serverLevel.getChunk(this.getBlockPos().getX() >> 4, this.getBlockPos().getZ() >> 4);

        serverLevel.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false).forEach(e -> {
            e.connection.send(this.getUpdatePacket());
        });
    }

}
