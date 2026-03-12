package de.bax.dysonsphere.tileentities;

import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.items.ItemStackHandlerProxy;
import de.bax.dysonsphere.items.ModItems;
import de.bax.dysonsphere.network.IUpdateReceiverTile;
import de.bax.dysonsphere.network.ModPacketHandler;
import de.bax.dysonsphere.network.TileUpdatePackage;
import de.bax.dysonsphere.recipes.ListenerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class ListenerTile extends BaseTile implements IUpdateReceiverTile {

    public static int energyCapacity = 200000;
    public static int workDuration = 120;
    public static int energyToConsume = 1000;

    public static final int slotGridStart = 0;
    public static final int slotGridEnd = 8;
    public static final int slotShards = 9;
    public static final int slotOutput = 10;

    public ItemStackHandler inventory = new ItemStackHandler(11){
        @Override
        protected void onContentsChanged(int slot) {
            shouldUpdate = true;
        };

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if(slot >= slotGridStart && slot <= slotGridEnd){
                return true;
            }
            if(slot == slotShards){
                return stack.is(ModItems.UNIVERSE_WHISPER.get());
            }
            if(slot == slotOutput){
                return true;
            }
            return false;
        }
    };

    public ItemStackHandlerProxy proxyInventory = new ItemStackHandlerProxy(inventory){ //different rules for capability access -> external automation
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if(slot != slotShards){
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        };

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if(slot != slotOutput){
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
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

    protected LazyOptional<IItemHandler> lazyInventory = LazyOptional.of(() -> proxyInventory);
    protected LazyOptional<IEnergyStorage> lazyEnergy = LazyOptional.of(() -> energyStorage);

    protected boolean shouldUpdate = false;
    protected int ticksElapsed = 0;
    protected int workTime = 0;
    public boolean running = false;
    // protected ListenerRecipe curRecipe; //not needed since result is decided on finish?

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
        if(!level.isClientSide()){
            if(ticksElapsed++ % 5 == 0){
                if(running && energyStorage.getEnergyStored() >= energyToConsume){
                    if(!inventory.getStackInSlot(slotShards).isEmpty()){
                        if(++workTime > workDuration){
                            if(canOutput()){
                                workTime = 0;
                                finnishWorking();
                            }
                        } else {
                            if(level.getRandom().nextFloat() < 0.1){
                                inventory.extractItem(slotShards, 1, false);
                            }
                            energyStorage.extractEnergy(energyToConsume, false);
                        }
                        shouldUpdate = true;
                    }
                } else {
                    workTime = 0;
                }
                if(shouldUpdate){
                    this.sendSyncPackageToNearbyPlayers();
                    shouldUpdate = false;
                }
            }
        }
    }

    protected boolean canOutput(){
        return inventory.getStackInSlot(slotOutput).isEmpty();
    }

    protected void finnishWorking(){
        RecipeWrapper wrapper = new RecipeWrapper(inventory);
        List<ListenerRecipe> recipes = ListenerRecipe.getRecipes(level).stream().filter((rec) -> {
            return ((ListenerRecipe) rec).matches(wrapper, level);
        }).toList();

        ItemStack output = recipes.isEmpty() ? ItemStack.EMPTY : recipes.get(level.random.nextInt(recipes.size())).getResultItem(level.registryAccess()).copy();

        // if(!output.isEmpty()){} //recipe invalid? Don't care //maybe add scrap item and produce scrap?
        output.setCount(1);
        for(int i = slotGridStart; i <= slotGridEnd; i++){
            inventory.extractItem(i, 1, false);
        }
        inventory.insertItem(slotOutput, output, false);
        
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        energyStorage.deserializeNBT(pTag.get("energy"));
        inventory.deserializeNBT(pTag.getCompound("inventory"));
        workTime = pTag.getInt("work");
        running = pTag.getBoolean("running");
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("energy", energyStorage.serializeNBT());
        pTag.put("inventory", inventory.serializeNBT());
        pTag.putInt("work", workTime);
        pTag.putBoolean("running", running);
    }

    public void onRemove(){
        for(int i = 0; i < inventory.getSlots(); i++){
            ItemEntity entity = new ItemEntity(level, getBlockPos().getX(),getBlockPos().getY(), getBlockPos().getZ(), inventory.getStackInSlot(i));
            level.addFreshEntity(entity);
        }
    }

    public int getProgressScaled(int scale){
        return scale * workTime / workDuration;
    }

    @Override
    public void handleUpdate(CompoundTag updateTag, Player player) {
        running = updateTag.getBoolean("running");
    }

    @Override
    public void sendGuiUpdate() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("running", running);
        ModPacketHandler.INSTANCE.sendToServer(new TileUpdatePackage(tag, getBlockPos()));
    }
    
}
