package de.bax.dysonsphere.tileentities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import de.bax.dysonsphere.containers.LaserPatternControllerContainer;
import de.bax.dysonsphere.containers.LaserPatternControllerInventoryContainer;
import de.bax.dysonsphere.network.IUpdateReceiverTile;
import de.bax.dysonsphere.network.ModPacketHandler;
import de.bax.dysonsphere.network.TileUpdatePackage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

public class LaserPatternControllerTile extends BaseTile implements IUpdateReceiverTile {

    public static int energyCapacity = 5000;
    public static int encodeEnergyUsage = 100;

    public OrbitalLaserAttackPattern inputPattern = OrbitalLaserAttackPattern.EMPTY;

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
        };
        public int getSlotLimit(int slot) {
            return 1;
        };

        public boolean isItemValid(int slot, net.minecraft.world.item.ItemStack stack) {
            return stack.getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER).isPresent();
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
        } else if (cap.equals(ForgeCapabilities.ITEM_HANDLER) && side == null) {
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

    public boolean hasMinEnergy(){
        return energyStorage.getEnergyStored() >= encodeEnergyUsage;
    }

    public void consumeEnergy(){
        energyStorage.extractEnergy(encodeEnergyUsage, false);
    }

    //without the item rendering doesn't work before opening the ui.
    @Override
    public void setChanged() {
        super.setChanged();
        if(!level.isClientSide){
            sendSyncPackageToNearbyPlayers();
        }
    }

    @Override
    public void handleUpdate(CompoundTag updateTag, Player player) {
        if(updateTag.contains("pattern")){
            OrbitalLaserAttackPattern pattern = new OrbitalLaserAttackPattern();
            pattern.deserializeNBT(updateTag.getCompound("pattern"));
            ItemStack stack = inventory.getStackInSlot(0).copy();
            if(!stack.isEmpty()){
                stack.getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER).ifPresent((container) -> {
                    container.setPattern(pattern);
                });
                inventory.setStackInSlot(0, stack);
            }
        }
        if(updateTag.contains("swapToInventory")){
            boolean swapToInventoryView = updateTag.getBoolean("swapToInventory");
            if(!swapToInventoryView){
                if(this.hasMinEnergy()){
                    this.consumeEnergy();
                } else {
                    player.displayClientMessage(Component.translatable("tooltip.dysonsphere.laser_pattern_controller_missing_energy"), true);
                    return;
                }
            }
            NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
            swapToInventoryView ? new LaserPatternControllerInventoryContainer(containerId, playerInventory, this) : new LaserPatternControllerContainer(containerId, playerInventory, this), Component.translatable("container.dysonsphere.laser_pattern_controller")), getBlockPos());
        }
    }

    @Override
    public void sendGuiUpdate() {
        CompoundTag tag = new CompoundTag();
        tag.put("pattern", inputPattern.serializeNBT());
        ModPacketHandler.INSTANCE.sendToServer(new TileUpdatePackage(tag, getBlockPos()));
    }

    public void sendGuiSwap(boolean swapToInventory) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("swapToInventory", swapToInventory);
        ModPacketHandler.INSTANCE.sendToServer(new TileUpdatePackage(tag, getBlockPos()));
    }

    
    

}
