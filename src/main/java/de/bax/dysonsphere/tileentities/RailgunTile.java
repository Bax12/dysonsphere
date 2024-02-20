package de.bax.dysonsphere.tileentities;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.tags.DSTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class RailgunTile extends BlockEntity {

    protected EnergyStorage energyStorage = new EnergyStorage(100000){
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if(!simulate) setChanged();
            return super.receiveEnergy(maxReceive, simulate);
        };

        public int extractEnergy(int maxExtract, boolean simulate) {
            if(!simulate) setChanged();
            return super.receiveEnergy(maxExtract, simulate);
        };
    };
    protected ItemStackHandler inventory = new ItemStackHandler(1) {
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
        public int getSlotLimit(int slot) {
            return 1;
        };
        public boolean isItemValid(int slot, net.minecraft.world.item.ItemStack stack) {
            return stack.is(DSTags.itemOrbitCapsule);
        };
    };

    protected LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    protected LazyOptional<IItemHandler> lazyInventory = LazyOptional.of(() -> inventory);

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
            // energyStorage.receiveEnergy(5, false);

            // DysonSphere.LOGGER.info("Railgun I: {}", inventory.getStackInSlot(0));

        }
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

    //TODO: Not dropping, item stack is passed correctly
    public void dropContent() {
        for(int i = 0; i < inventory.getSlots(); i++){
            DysonSphere.LOGGER.info("Dropping item: {}", inventory.getStackInSlot(i));
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getY(), inventory.getStackInSlot(i));
        }
    }

}
