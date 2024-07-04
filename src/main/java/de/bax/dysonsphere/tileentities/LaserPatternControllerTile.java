package de.bax.dysonsphere.tileentities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class LaserPatternControllerTile extends BaseTile {

    public static int energyCapacity = 5000;
    public static int encodeEnergyUsage = 100;

    

    public EnergyStorage energyStorage = new EnergyStorage(energyCapacity){
        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if(!simulate) setChanged();
            return super.extractEnergy(maxExtract, simulate);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if(!simulate) setChanged();
            return super.receiveEnergy(maxReceive, simulate);
        }
    };

    public ItemStackHandler inventory = new ItemStackHandler(1){
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
            sendSyncPackageToNearbyPlayers();
        };
        public int getSlotLimit(int slot) {
            return 1;
        };

        public boolean isItemValid(int slot, net.minecraft.world.item.ItemStack stack) {
            return true;
        };
    };

    public LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    public LazyOptional<IItemHandler> lazyInventory = LazyOptional.of(() -> inventory);

    public LaserPatternControllerTile(BlockPos pos, BlockState state) {
        super(ModTiles.LASER_PATTERN_CONTROLLER.get(), pos, state);
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

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("Energy")){
            energyStorage.deserializeNBT(tag.get("Energy"));
        } 
        if(tag.contains("Inventory")) {
            inventory.deserializeNBT(tag.getCompound("Inventory"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("Inventory", inventory.serializeNBT());
    }

    public void dropContent() {
        for(int i = 0; i < inventory.getSlots(); i++){
            ItemEntity entity = new ItemEntity(level, getBlockPos().getX(),getBlockPos().getY(), getBlockPos().getZ(), inventory.getStackInSlot(i));
            level.addFreshEntity(entity);
        }
    }
    
    

}
