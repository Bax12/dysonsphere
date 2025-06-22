package de.bax.dysonsphere.capabilities.inputHatch;

import java.util.Arrays;
import java.util.Set;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;

public abstract class InputProviderHandler implements IInputProvider, INBTSerializable<CompoundTag> {

    public static final int MAX_DISTANCE = 256;

    
    protected IItemHandler inventory;

    protected int acceptorDistance = MAX_DISTANCE;
    protected Direction uplinkDirection = null;
    protected LazyOptional<IInputAcceptor> lazyAcceptor = LazyOptional.empty();
    protected BlockPos acceptorPos;

    protected LazyOptional<IItemHandler> lazyInventory;
    protected LazyOptional<IEnergyStorage> lazyEnergy;

    protected final BlockEntity tile;

    @SuppressWarnings("unchecked")
    protected LazyOptional<IInputProvider>[] neighborList = new LazyOptional[6];

    public LazyOptional<IInputProvider> lazyProvider = LazyOptional.of(() -> this);


    //all values are calculated or references. So we do not need to save anything to nbt


    public InputProviderHandler(BlockEntity containingTile, IItemHandler providedInventory){
        this.inventory = providedInventory;
        this.tile = containingTile;
        lazyInventory = LazyOptional.of(() -> inventory);
        Arrays.fill(neighborList, LazyOptional.empty());
    }

    @Override
    public BlockEntity getTile() {
        return tile;
    }

    @Override
    public LazyOptional<IItemHandler> getInventory() {
        return lazyInventory;
    }

    @Override
    public int getAcceptorDistance() {
        return acceptorDistance;
    }

    @SuppressWarnings("null")
    public void updateNeighbors(){
        // boolean updateUplink = false;
        for(Direction dir : Direction.values()){
            BlockEntity neighbor = tile.getLevel().getBlockEntity(tile.getBlockPos().relative(dir));
            if(neighbor != null){
                LazyOptional<IInputProvider> neighborHandler = neighbor.getCapability(DSCapabilities.INPUT_PROVIDER, dir.getOpposite());
                if(neighborHandler.isPresent() && !neighborHandler.equals(neighborList[dir.ordinal()])){
                    neighborList[dir.ordinal()] = neighborHandler;
                    //relevant neighbor changed! check uplink path to acceptor and distance
                    // updateUplink = true;
                } else if(!neighborHandler.isPresent() && !LazyOptional.empty().equals(neighborList[dir.ordinal()])){
                    neighborList[dir.ordinal()] = LazyOptional.empty();
                    // updateUplink = true;
                }
                // if(neighbor.getCapability(DSCapabilities.INPUT_ACCEPTOR, dir.getOpposite()).isPresent()){ //worst case we update the uplink every time something changes right next to the acceptor. Should not be to expensive
                //     updateUplink = true;
                // }
            } else {
                if(!LazyOptional.empty().equals(neighborList[dir.ordinal()])){
                    neighborList[dir.ordinal()] = LazyOptional.empty();
                    //relevant neighbor changed! check uplink path to acceptor and distance
                    // updateUplink = true;
                }
            }
        }
        // if(updateUplink){
        //     updateUplink();
        // }
        // getAcceptor().ifPresent((acceptor) -> {
        //     acceptor.addInputProvider(lazyProvider);
        // });

        
    }

    @Override
    public void resetUplink() {
        acceptorDistance = MAX_DISTANCE;
        uplinkDirection = null;
        lazyAcceptor = LazyOptional.empty();
        onUplinkChange();
    }

    @SuppressWarnings("null")
    @Override
    public void updateUplink(){
        int minDistance = MAX_DISTANCE; //this creates a hard limit for the provider chaining distance. Probably fails for other reasons way before that.
        Direction newDirection = null;
        for(Direction dir : Direction.values()){
            LazyOptional<IInputProvider> neighborHandler = neighborList[dir.ordinal()];
            int distance = neighborHandler.map((handler) -> {
                return handler.getAcceptorDistance() + 1; // we are one block further away from the acceptor then the neighbor we ask for the distance.
            }).orElse(MAX_DISTANCE);
            if(distance >= MAX_DISTANCE){
                BlockEntity neighbor = tile.getLevel().getBlockEntity(tile.getBlockPos().relative(dir));
                if(neighbor != null && neighbor.getCapability(DSCapabilities.INPUT_ACCEPTOR, dir.getOpposite()).isPresent()){
                    distance = 1; //if we are right next to an acceptor our distance is 1
                }
            } else{
                boolean isFacingOpposite = neighborHandler.map((handler) -> {
                    Direction neighborUplink = handler.getAcceptorUplinkDirection();
                    return neighborUplink != null && neighborUplink.getOpposite().equals(this.getAcceptorUplinkDirection());
                }).orElse(false);
                if(isFacingOpposite){
                    continue; //Prevent setting an uplink that has us as uplink. Aka two input provider facing each other.
                }
            } 
            if(distance < minDistance){
                minDistance = distance;
                newDirection = dir;
            }
        }
        
        acceptorDistance = minDistance; 
        if(acceptorDistance >= MAX_DISTANCE){
            lazyAcceptor = LazyOptional.empty();
            if(uplinkDirection != null){
                uplinkDirection = null;
                onUplinkChange();
            }
        } else {
            if(newDirection != null && !newDirection.equals(uplinkDirection)){
                uplinkDirection = newDirection;
                onUplinkChange();
            }
        }
    }

    @SuppressWarnings("null")
    public void onUplinkChange(){
        if(acceptorDistance == 1){
            lazyAcceptor = tile.getLevel().getBlockEntity(tile.getBlockPos().relative(uplinkDirection)).getCapability(DSCapabilities.INPUT_ACCEPTOR);
        } else if(acceptorDistance < MAX_DISTANCE) {
            lazyAcceptor = neighborList[uplinkDirection.ordinal()].map((provider) -> {
                return provider.getAcceptor();
            }).orElse(LazyOptional.empty());
        }
        if(getAcceptor().isPresent()){
            getAcceptor().addListener((acceptor) -> {
                this.updateUplink();
            });
            getAcceptor().resolve().get().addInputProvider(lazyProvider);
        } else {
            uplinkDirection = null;
            acceptorDistance = MAX_DISTANCE;
        }
    };

    @Override
    public LazyOptional<IInputAcceptor> getAcceptor() {
        if(acceptorPos != null){
            Level level = this.tile.getLevel();
            if(level != null){
                BlockEntity tile = level.getBlockEntity(acceptorPos);
                if(tile != null) {
                    lazyAcceptor = tile.getCapability(DSCapabilities.INPUT_ACCEPTOR);
                }
                acceptorPos = null;
            }
        }
        return lazyAcceptor;
    }

    @Override
    public Set<LazyOptional<IInputProvider>> getSubProviders(Set<LazyOptional<IInputProvider>> providerSet) {
        for(Direction dir : Direction.values()){
            if(dir.equals(uplinkDirection)) continue; //Skip uplink, we only add the sub providers.
            LazyOptional<IInputProvider> neighborHandler = neighborList[dir.ordinal()];
            if(neighborHandler.isPresent()){ //we only scan the sub-provider if it is new in the set
                providerSet.add(neighborHandler);
            }
        }
        return providerSet;
    }

    @Override
    public Direction getAcceptorUplinkDirection() {
        return uplinkDirection;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        getAcceptor().ifPresent((provider) -> {
            tag.putLong("pos", provider.getTile().getBlockPos().asLong());
        });
        if(uplinkDirection != null){
            tag.putInt("uplink", uplinkDirection.ordinal());
        }
        if(acceptorDistance < MAX_DISTANCE){
            tag.putInt("distance", acceptorDistance);
        }
        

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("pos")){
            acceptorPos = BlockPos.of(nbt.getLong("pos"));
        }
        if(nbt.contains("uplink")){
            uplinkDirection = Direction.values()[nbt.getInt("uplink")];
            onUplinkChange();
        }
        if(nbt.contains("distance")){
            acceptorDistance = nbt.getInt("distance");
        }
    }

    public void onPlacedInWorld(){
        updateNeighbors();
        getAcceptor().ifPresent((acceptor) -> {
            acceptor.addInputProvider(lazyProvider);
            // acceptor.markForRefresh(); //marking for refresh here causes a lot of unneeded refreshes
        });
        updateUplink();
    }

    public void onRemove(){
        getAcceptor().ifPresent((acceptor) -> {
            acceptor.markForRefresh();
        });
    }
    
    public void setEnergy(LazyOptional<IEnergyStorage> lazyEnergy) {
        this.lazyEnergy = lazyEnergy;
    }

    @Override
    public LazyOptional<IEnergyStorage> getEnergy() {
        return lazyEnergy;
    }
    
}
