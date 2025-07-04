package de.bax.dysonsphere.tileentities;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.compat.ModCompat;
import de.bax.dysonsphere.compat.ad_astra.AdAstra;
import de.bax.dysonsphere.sounds.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class RailgunTile extends BaseTile {

    public static int baseLaunchEnergy = 90000;
    public static int energyCapacity = 150000;

    public EnergyStorage energyStorage = new EnergyStorage(energyCapacity);
    public ItemStackHandler inventory = new ItemStackHandler(1) {
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
        public int getSlotLimit(int slot) {
            return 1;
        };
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getCapability(DSCapabilities.DS_PART).isPresent();
        };
    };

    public LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    public LazyOptional<IItemHandler> lazyInventory = LazyOptional.of(() -> inventory);

    protected int ticksElapsed = 0;
    protected int lastEnergy = 0;

    protected float launchMult = 1f;

    protected boolean canAddToDS = true;

    public RailgunTile(BlockPos pos, BlockState state) {
        super(ModTiles.RAILGUN.get(), pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(ForgeCapabilities.ENERGY)){
            return lazyEnergyStorage.cast();
        } else if (cap.equals(ForgeCapabilities.ITEM_HANDLER)) {
            return lazyInventory.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyStorage.invalidate();
        lazyInventory.invalidate();
    }
    
    public void tick() {
        if(!level.isClientSide){
            // DysonSphere.LOGGER.info("Railgun E: {}", energyStorage.getEnergyStored());
            // energyStorage.receiveEnergy(50000, false);

            // DysonSphere.LOGGER.info("Railgun I: {}", inventory.getStackInSlot(0));

            canAddToDS = true;
            ItemStack invStack = inventory.getStackInSlot(0);
            if(energyStorage.getEnergyStored() >= getLaunchEnergy() && !invStack.isEmpty() && canSeeSky()){
                level.getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((ds) -> {
                    if(ds.addDysonSpherePart(invStack.copyWithCount(1), false)){
                        invStack.shrink(1);
                        inventory.setStackInSlot(0, invStack);
                        energyStorage.extractEnergy(getLaunchEnergy(), false);
                        level.playSound(null, worldPosition, ModSounds.RAILGUN_SHOT.get(), SoundSource.BLOCKS);
                    } else {
                        //set unable to add flag
                        canAddToDS = false;
                    }
                });
            }
            if(ticksElapsed++ % 5 == 0 && lastEnergy != energyStorage.getEnergyStored()){
                this.setChanged();
                lastEnergy = energyStorage.getEnergyStored();
                
                sendSyncPackageToNearbyPlayers();
            } 
            if(ticksElapsed % 200 == 20 && ModCompat.isLoaded(ModCompat.MODID.AD_ASTRA)){ //recheck the launch multiplier every 10 seconds. Gravity should not change so frequently, right?
                float last = launchMult;
                launchMult = AdAstra.getOrbitalLaunchMult(level, worldPosition);
                if(last != launchMult){
                    this.setChanged();
                    lastEnergy = energyStorage.getEnergyStored();
                    sendSyncPackageToNearbyPlayers();
                }
            }
        }
    }

    public boolean canSeeSky(){
        return level != null && level.canSeeSky(worldPosition.above());
    }

    public boolean canAddToDS(){
        return canAddToDS;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(!level.isClientSide && ModCompat.isLoaded(ModCompat.MODID.AD_ASTRA)){
            launchMult = AdAstra.getOrbitalLaunchMult(level, worldPosition);
        }
    }


    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        if(tag.contains("Energy")){
            energyStorage.deserializeNBT(tag.get("Energy"));
        } 
        if(tag.contains("Inventory")) {
            inventory.deserializeNBT(tag.getCompound("Inventory"));
        }
        if(tag.contains("launchMult")) {
            launchMult = tag.getFloat("launchMult");
        }
        if(tag.contains("canAdd")){
            canAddToDS = tag.getBoolean("canAdd");
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("Inventory", inventory.serializeNBT());
        tag.putFloat("launchMult", launchMult);
        tag.putBoolean("canAdd", canAddToDS);
    }

    public void dropContent() {
        for(int i = 0; i < inventory.getSlots(); i++){
            // DysonSphere.LOGGER.info("Dropping item: {}", inventory.getStackInSlot(i));
            // Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getY(), inventory.getStackInSlot(i));
            ItemEntity entity = new ItemEntity(level, getBlockPos().getX(),getBlockPos().getY(), getBlockPos().getZ(), inventory.getStackInSlot(i));
            level.addFreshEntity(entity);
        }
    }


    public int getLaunchEnergy() {
        return (int) (baseLaunchEnergy * launchMult);
    }
    

}
