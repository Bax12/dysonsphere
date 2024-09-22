package de.bax.dysonsphere.tileentities;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.dsEnergyReciever.IDSEnergyReceiver;
import de.bax.dysonsphere.capabilities.dysonSphere.IDysonSphereContainer;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import de.bax.dysonsphere.network.IUpdateReceiverTile;
import de.bax.dysonsphere.network.ModPacketHandler;
import de.bax.dysonsphere.network.TileUpdatePackage;
import de.bax.dysonsphere.sounds.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class DSEnergyReceiverTile extends BaseTile implements IUpdateReceiverTile, IHeatTile {

    public static double maxHeat = 1700;

    protected int dsPowerDraw;
    protected boolean canReceive = false;

    public HeatHandler heatHandler = new HeatHandler(maxHeat);
    public IDSEnergyReceiver dsReceiver = new IDSEnergyReceiver() {

        @Override
        public boolean canReceive() {
            return getLevel() != null && getLevel().canSeeSky(getBlockPos().above()); //we check above as our own block blocks the sky access from our own pos.
        }

        @Override
        public int getMaxReceive() {
            return Math.min((int) Math.ceil(heatHandler.getMaxHeatStored() - heatHandler.getHeatStored()), dsPowerDraw);
        }

        @Override
        public void registerToDysonSphere(IDysonSphereContainer dysonSphere) {
            dysonSphere.registerEnergyReceiver(lazyDSReceiver);
        }

        @Override
        public void removeFromDysonSphere(IDysonSphereContainer dysonSphere) {
            dysonSphere.removeEnergyReceiver(lazyDSReceiver);
        }
        
    };

    protected LazyOptional<IHeatContainer> lazyHeatContainer = LazyOptional.of(() -> heatHandler);
    protected LazyOptional<IDSEnergyReceiver> lazyDSReceiver = LazyOptional.of(() -> dsReceiver);

    protected int ticksElapsed = 0;
    protected double lastHeat = 0;

    public DSEnergyReceiverTile(BlockPos pos, BlockState state) {
        super(ModTiles.DS_ENERGY_RECEIVER.get(), pos, state);
    }

@   Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.HEAT)){
            return lazyHeatContainer.cast();
        } else if (cap.equals(DSCapabilities.DS_ENERGY_RECEIVER)){
            return lazyDSReceiver.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyDSReceiver.invalidate();
        lazyHeatContainer.invalidate();
    }

    public void tick(){
        if(!level.isClientSide){
            Optional<IDysonSphereContainer> dysonsphere = level.getCapability(DSCapabilities.DYSON_SPHERE).map((ds) -> {return ds;});
            if(dysonsphere.isPresent()){
                canReceive = dsReceiver.canReceive();
                int receive = dsReceiver.getCurrentReceive(dysonsphere.get());
                if(receive > 0){
                    heatHandler.receiveHeat(receive / 10f, false);
                    if(ticksElapsed % 100 == 0){
                        level.playSound(null, worldPosition, ModSounds.DS_ENERGY_RECEIVER_WORK.get(), SoundSource.BLOCKS, 0.2f, 0.8f);
                    }
                }
            }
            //splitshare heat
            heatHandler.splitShare();
            if(ticksElapsed++ % 5 == 0 && lastHeat != heatHandler.getHeatStored()){
                this.setChanged();
                lastHeat = heatHandler.getHeatStored();

                sendSyncPackageToNearbyPlayers();
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        level.getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((ds) -> {
            dsReceiver.registerToDysonSphere(ds);
        });       
        updateNeighbors();
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        if(tag.contains("Heat")) {
            heatHandler.deserializeNBT(tag.getCompound("Heat"));
        }
        dsPowerDraw = tag.getInt("Target");
        canReceive = tag.getBoolean("canReceive");
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Heat", heatHandler.serializeNBT());
        tag.putInt("Target", this.dsPowerDraw);
        tag.putBoolean("canReceive", canReceive);
    }

    public int getDsPowerDraw() {
        return dsPowerDraw;
    }

    public void setDsPowerDraw(int dsPowerDraw) {
        this.dsPowerDraw = dsPowerDraw;
    }

    public void onNeighborChange() {
        updateNeighbors();
    }

    public boolean canReceive(){
        return canReceive;
    }
    
    protected void updateNeighbors(){
        heatHandler.updateNeighbors(level, worldPosition);
    }

    @Override
    public void handleUpdate(CompoundTag updateTag, Player player) {
        setDsPowerDraw(updateTag.getInt("target"));
    }

    @Override
    public void sendGuiUpdate(){
        CompoundTag tag = new CompoundTag();
        tag.putInt("target", dsPowerDraw);
        ModPacketHandler.INSTANCE.sendToServer(new TileUpdatePackage(tag, getBlockPos()));
    }

    @Override
    public IHeatContainer getHeatContainer() {
        return heatHandler;
    }

}
