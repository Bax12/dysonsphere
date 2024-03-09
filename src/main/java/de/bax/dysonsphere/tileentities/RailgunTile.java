package de.bax.dysonsphere.tileentities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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

    public static final int LAUNCH_ENERGY = 90000;

    public EnergyStorage energyStorage = new EnergyStorage(150000);
    public ItemStackHandler inventory = new ItemStackHandler(1) {
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
        public int getSlotLimit(int slot) {
            return 1;
        };
        public boolean isItemValid(int slot, net.minecraft.world.item.ItemStack stack) {
            return stack.getCapability(DSCapabilities.DS_PART).isPresent();
        };
    };

    public LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    public LazyOptional<IItemHandler> lazyInventory = LazyOptional.of(() -> inventory);

    protected int ticksElapsed = 0;
    protected int lastEnergy = 0;

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
            ItemStack invStack = inventory.getStackInSlot(0);
            if(energyStorage.getEnergyStored() >= LAUNCH_ENERGY && !invStack.isEmpty()){
                level.getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((ds) -> {
                    if(ds.addDysonSpherePart(invStack.copyWithCount(1), false)){
                        DysonSphere.LOGGER.info("Railgun Launched: {}", invStack);
                        invStack.shrink(1);
                        inventory.setStackInSlot(0, invStack);
                        energyStorage.extractEnergy(LAUNCH_ENERGY, false);
                    }
                });
            }
            if(ticksElapsed++ % 5 == 0 && lastEnergy != energyStorage.getEnergyStored()){
                this.setChanged();
                lastEnergy = energyStorage.getEnergyStored();
                
                sendSyncPackageToNearbyPlayers();
            }   
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

    public void dropContent() {
        for(int i = 0; i < inventory.getSlots(); i++){
            DysonSphere.LOGGER.info("Dropping item: {}", inventory.getStackInSlot(i));
            // Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getY(), inventory.getStackInSlot(i));
            ItemEntity entity = new ItemEntity(level, getBlockPos().getX(),getBlockPos().getY(), getBlockPos().getZ(), inventory.getStackInSlot(i));
            level.addFreshEntity(entity);
        }
    }

    

}
