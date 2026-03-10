package de.bax.dysonsphere.tileentities;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.recipes.ListenerRecipe;
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

public class ListenerTile extends BaseTile {

    public static int energyCapacity = 15000;
    public static int workDuration = 1200;
    public static int energyToConsume = 100;

    public static final int slotGridStart = 0;
    public static final int slotGridEnd = 8;
    public static final int slotShards = 9;
    public static final int slotOutput = 10;

    public ItemStackHandler inventory = new ItemStackHandler(11){
        @Override
        protected void onContentsChanged(int slot) {
            shouldUpdate = true;
        };
    };

    public EnergyStorage energyStorage = new EnergyStorage(energyCapacity){
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if(!simulate){
                shouldUpdate = true;
            }
            return super.receiveEnergy(maxReceive, simulate);
        };

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if(!simulate){
                shouldUpdate = true;
            }
            return super.extractEnergy(maxExtract, simulate);
        };
    };

    protected LazyOptional<IItemHandler> lazyInventory = LazyOptional.of(() -> inventory);
    protected LazyOptional<IEnergyStorage> lazyEnergy = LazyOptional.of(() -> energyStorage);

    protected boolean shouldUpdate = false;
    protected int ticksElapsed = 0;
    protected int workTime = 0;
    protected ListenerRecipe curRecipe;

    public ListenerTile(BlockPos pos, BlockState state) {
        super(ModTiles.LISTENER.get(), pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(ForgeCapabilities.ITEM_HANDLER)){
            return lazyInventory.cast();
        } else if(cap.equals(ForgeCapabilities.ENERGY)) {
            return lazyEnergy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyInventory.invalidate();
        lazyEnergy.invalidate();
    }

    public void tick(){
        if(!level.isClientSide){
            if(ticksElapsed++ % 5 == 0){

            }
        }
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
    }

    public void onRemove(){
        for(int i = 0; i < inventory.getSlots(); i++){
            ItemEntity entity = new ItemEntity(level, getBlockPos().getX(),getBlockPos().getY(), getBlockPos().getZ(), inventory.getStackInSlot(i));
            level.addFreshEntity(entity);
        }
    }
    
}
