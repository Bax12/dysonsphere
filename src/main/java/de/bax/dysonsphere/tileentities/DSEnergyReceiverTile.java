package de.bax.dysonsphere.tileentities;

import java.util.Optional;

import org.joml.Math;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.dsEnergyReciever.IDSEnergyReceiver;
import de.bax.dysonsphere.capabilities.dysonSphere.IDysonSphereContainer;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

public class DSEnergyReceiverTile extends BaseTile {

    public static final double maxHeat = 1700;

    protected int dsPowerDraw;

    public HeatHandler heatHandler = new HeatHandler(300, maxHeat){
        public double getMaxSplitShareAmount() {
            return 500d;
        };
    };
    public IDSEnergyReceiver dsReceiver = new IDSEnergyReceiver() {

        @Override
        public boolean canReceive() {
            return getLevel().canSeeSky(getBlockPos());
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

        @Override
        public int getCurrentReceive(IDysonSphereContainer dysonSphere) {
            int recieve = getMaxReceive();
            if(dysonSphere.getUtilization() > 100){ //TODO handle overuse nicer
                recieve = recieve / 2;
            }
            return canReceive() ? recieve : 0;
        }
        
    };

    protected LazyOptional<IHeatContainer> lazyHeatContainer = LazyOptional.of(() -> heatHandler);
    protected LazyOptional<IDSEnergyReceiver> lazyDSReceiver = LazyOptional.of(() -> dsReceiver);

    protected int ticksElapsed = 0;
    protected double lastHeat = 0;

    public DSEnergyReceiverTile(BlockPos pos, BlockState state) {
        super(ModTiles.DS_ENERGY_RECEIVER.get(), pos, state);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyDSReceiver.invalidate();
        lazyHeatContainer.invalidate();
    }

    public void tick(){
        // DysonSphere.LOGGER.info("DSEnergyReceiverTile CurrentHeat: {}", heatHandler.getHeatStored());
        if(!level.isClientSide){
            Optional<IDysonSphereContainer> dysonsphere = level.getCapability(DSCapabilities.DYSON_SPHERE).map((ds) -> {return ds;});
            if(dysonsphere.isPresent()){
                int recieve = dsReceiver.getCurrentReceive(dysonsphere.get());
                if(recieve >= 0){
                    heatHandler.receiveHeat(recieve / 10f, false);
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
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("Heat")) {
            heatHandler.deserializeNBT(tag.getCompound("Heat"));
        }
        dsPowerDraw = tag.getInt("Target");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Heat", heatHandler.serializeNBT());
        tag.putInt("Target", this.dsPowerDraw);

    }

    public int getDsPowerDraw() {
        return dsPowerDraw;
    }

    public void setDsPowerDraw(int dsPowerDraw) {
        this.dsPowerDraw = dsPowerDraw;
        DysonSphere.LOGGER.info("DsEnergyReceiverTile dsPowerDraw set: {}", dsPowerDraw);
    }

    
    



}
