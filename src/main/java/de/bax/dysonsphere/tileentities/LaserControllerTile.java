package de.bax.dysonsphere.tileentities;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.OrbitalLaserAttackPattern;
import de.bax.dysonsphere.entities.TargetDesignatorEntity;
import de.bax.dysonsphere.gui.components.IButtonPressHandler;
import de.bax.dysonsphere.network.IUpdateReceiverTile;
import de.bax.dysonsphere.network.ModPacketHandler;
import de.bax.dysonsphere.network.TileUpdatePackage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class LaserControllerTile extends BaseTile implements IButtonPressHandler, IUpdateReceiverTile{

    public static int energyUsage = 50;
    public static int energyCapacity = 50000;
    public static int cooldownTime = 100;
    public static int workTime = 400;

    public EnergyStorage energyStorage = new EnergyStorage(energyCapacity);

    public ItemStackHandler inventory = new ItemStackHandler(1){
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setInventoryChanged();
        };

        public int getSlotLimit(int slot) {
            return 1;
        };
        public boolean isItemValid(int slot, net.minecraft.world.item.ItemStack stack) {
            return stack.getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER).map((container) -> {
                return !container.isEmpty();
            }).orElse(false);
        };
    };

    public LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    public LazyOptional<IItemHandler> lazyInventory = LazyOptional.of(() -> inventory);


    protected int currentCooldown = 0;
    protected int currentWorkTime = 0;
    protected int targetX,targetY,targetZ;
    protected Player owner;
    protected UUID ownerUuid;

    protected int ticksElapsed = 0;
    protected int lastEnergy = 0;
    protected int lastCooldown = 0;
    protected boolean dirty = false;
    protected boolean working;

    protected OrbitalLaserAttackPattern pattern;

    public LaserControllerTile(BlockPos pos, BlockState state) {
        super(ModTiles.LASER_CONTROLLER.get(), pos, state);
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
            if(currentCooldown > 0){
                currentCooldown--;
            } else if(working && canWorkOn()){
                energyStorage.extractEnergy(energyUsage, false);
                currentWorkTime++;
            } else {
                currentWorkTime = 0;
            }
            if(currentWorkTime >= workTime){
                launch();
                currentWorkTime = 0;
                currentCooldown = cooldownTime;
            }
            
            
            
            if(ticksElapsed++ % 5 == 0 && (lastEnergy != energyStorage.getEnergyStored() || currentCooldown != lastCooldown || dirty)){
                this.setChanged();
                lastEnergy = energyStorage.getEnergyStored();
                lastCooldown = currentCooldown;
                dirty = false;
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
            onInventoryChange();
        }
        currentCooldown = tag.getInt("cooldown");
        currentWorkTime = tag.getInt("workTime");
        working = tag.getBoolean("working");
        targetX = tag.getInt("targetX");
        targetY = tag.getInt("targetY");
        targetZ = tag.getInt("targetZ");
        if(tag.contains("owner")){
            ownerUuid = tag.getUUID("owner");
        }

    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("Inventory", inventory.serializeNBT());
        if(currentCooldown != 0){
            tag.putInt("cooldown", currentCooldown);
        }
        if(currentWorkTime != 0){
            tag.putInt("workTime", currentWorkTime);
        }
        tag.putBoolean("working", working);
        tag.putInt("targetX", targetX);
        tag.putInt("targetY", targetY);
        tag.putInt("targetZ", targetZ);
        if(ownerUuid != null){
            tag.putUUID("owner", ownerUuid);
        }
    }

        public void dropContent() {
        for(int i = 0; i < inventory.getSlots(); i++){
            // Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getY(), inventory.getStackInSlot(i));
            ItemEntity entity = new ItemEntity(level, getBlockPos().getX(),getBlockPos().getY(), getBlockPos().getZ(), inventory.getStackInSlot(i));
            level.addFreshEntity(entity);
        }
    }

    protected void onInventoryChange(){
        this.pattern = inventory.getStackInSlot(0).getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER).lazyMap((laser) -> {
            return laser.getPattern();
        }).orElse(OrbitalLaserAttackPattern.EMPTY);
    }   

    public void setInventoryChanged(){
        onInventoryChange();
        setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        dirty = true;
    }

    public boolean isOnCooldown(){
        return currentCooldown > 0;
    }

    public boolean hasPattern(){
        return !pattern.isEmpty();
    }

    public boolean canWorkOn(){
        boolean flag = !isOnCooldown() && hasPattern() && energyStorage.getEnergyStored() >= energyUsage;
        if(!flag){
            working = false;
            setChanged();
        }
        return flag;
    }

    public void startLaunch(){
        if(canWorkOn()){
            this.working = true;
            setChanged();
        }
    }

    public boolean isWorking() {
        return working;
    }

    protected void launch(){
        if(canWorkOn()){
            DysonSphere.LOGGER.info("LaserControllerTile launch owner: {}", getOwner());
            getOwner().getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((laser) -> {
                var designator = new TargetDesignatorEntity(getOwner(), level, targetX + 0.5f, targetY + 0.5f, targetZ + 0.5f);
                inventory.getStackInSlot(0).getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER).ifPresent((patternContainer) -> {
                    var pattern = patternContainer.getPattern();
                    if(laser.getLasersAvailable(getOwner().tickCount) >= pattern.getLasersRequired()){
                        designator.setOrbitalAttackPattern(pattern);
                        laser.putLasersOnCooldown(getOwner().tickCount, pattern.getLasersRequired(), pattern.getRechargeTime());
                        level.addFreshEntity(designator);
                    }
                });
                
            });
            
            
            
            
            
            currentCooldown = cooldownTime;
            working = false;
            setChanged();
        }
    }

    @Override
    public void handleButtonPress(int buttonId, Player player) {
        switch (buttonId) {
            case 0:
                startLaunch();
                break;
            case 1:
                setOwner(player);
                break;
        }
    }

    public void setOwner(Player owner) {
        if(this.owner != owner){
            setChanged();
        }
        this.owner = owner;
        if(owner != null){
            this.ownerUuid = owner.getUUID();
        } else {
            ownerUuid = null;
        }
        
    }

    public Player getOwner() {
        if(owner == null && ownerUuid != null){
            owner = level.getPlayerByUUID(ownerUuid);
        }
        return owner;
    }

    public void setTargetX(int targetX) {
        if(this.targetX != targetX){
            setChanged();
        }
        this.targetX = targetX;

    }

    public void setTargetY(int targetY) {
        if(this.targetY != targetY){
            setChanged();
        }
        this.targetY = targetY;
    }
    
    public void setTargetZ(int targetZ) {
        if(this.targetZ != targetZ){
            setChanged();
        }
        this.targetZ = targetZ;
    }

    public BlockPos getTarget() {
        return new BlockPos(targetX, targetY, targetZ);
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public int getTargetZ() {
        return targetZ;
    }

    @Override
    public void handleUpdate(CompoundTag updateTag, Player player) {
        setTargetX(updateTag.getInt("x"));
        setTargetY(updateTag.getInt("y"));
        setTargetZ(updateTag.getInt("z"));
    }

    @Override
    public void sendGuiUpdate(){
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", targetX);
        tag.putInt("y", targetY);
        tag.putInt("z", targetZ);
        ModPacketHandler.INSTANCE.sendToServer(new TileUpdatePackage(tag, getBlockPos()));
    }

    public OrbitalLaserAttackPattern getPattern() {
        return pattern;
    }
}
