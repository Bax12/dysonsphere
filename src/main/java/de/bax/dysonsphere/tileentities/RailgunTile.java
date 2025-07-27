package de.bax.dysonsphere.tileentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.energy.AcceptorEnergyWrapper;
import de.bax.dysonsphere.capabilities.inputHatch.IInputAcceptor;
import de.bax.dysonsphere.capabilities.inputHatch.IInputProvider;
import de.bax.dysonsphere.capabilities.inputHatch.IInputProvider.ProviderType;
import de.bax.dysonsphere.capabilities.inputHatch.InputAcceptorHandler;
import de.bax.dysonsphere.compat.ModCompat;
import de.bax.dysonsphere.compat.ad_astra.AdAstra;
import de.bax.dysonsphere.recipes.ModRecipes;
import de.bax.dysonsphere.recipes.OrbitalLaunchRecipe;
import de.bax.dysonsphere.sounds.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class RailgunTile extends BaseTile {

    public static int baseLaunchEnergy = 90000;
    public static int energyCapacity = 150000;

    public EnergyStorage energyStorage = new EnergyStorage(energyCapacity){
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if(!simulate){
                setChanged();
            }
            return super.receiveEnergy(maxReceive, simulate);
        };

        public int extractEnergy(int maxExtract, boolean simulate) {
            if(!simulate){
                setChanged();
            }
            return super.extractEnergy(maxExtract, simulate);
        };
    };
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

    public InputAcceptorHandler acceptorHandler = new InputAcceptorHandler(this){
        public void addInputProvider(LazyOptional<IInputProvider> provider) {
            super.addInputProvider(provider);
            if(provider.isPresent()){
                setChanged();
            }//TODO: Sync issues: Removing a hatch does not remove dependend hatches on the client side only.
        };

        public void refreshProvider() {
            super.refreshProvider();
            setChanged();
        };
    };

    public LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    public LazyOptional<IItemHandler> lazyInventory = LazyOptional.of(() -> inventory);
    protected LazyOptional<IInputAcceptor> lazyAcceptor = LazyOptional.of(() -> acceptorHandler);

    public AcceptorEnergyWrapper acceptorStorage = new AcceptorEnergyWrapper(lazyEnergyStorage, acceptorHandler);

    protected int ticksElapsed = 0;
    protected boolean dirty = false;
    protected OrbitalLaunchRecipe currentRecipe;

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
        } else if (cap.equals(DSCapabilities.INPUT_ACCEPTOR)){
            return lazyAcceptor.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyEnergyStorage.invalidate();
        lazyInventory.invalidate();
        lazyAcceptor.invalidate();
    }
    
    public void tick() {
        if(!level.isClientSide){
            if(ticksElapsed == 20){
                acceptorHandler.markForRefresh();
            }
            acceptorHandler.tick();
            canAddToDS = true;
            ItemStack invStack = inventory.getStackInSlot(0);

            // if(energyStorage.getEnergyStored() >= getLaunchEnergy() && !invStack.isEmpty() && canSeeSky()){
            //     level.getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((ds) -> {
            //         if(ds.addDysonSpherePart(invStack.copyWithCount(1), false)){
            //             invStack.shrink(1);
            //             inventory.setStackInSlot(0, invStack);
            //             energyStorage.extractEnergy(getLaunchEnergy(), false);
            //             level.playSound(null, worldPosition, ModSounds.RAILGUN_SHOT.get(), SoundSource.BLOCKS);
            //         } else {
            //             //set unable to add flag
            //             canAddToDS = false;
            //         }
            //     });
            // }
            
            if(currentRecipe != null  && canSeeSky()){
                if(currentRecipe.matches(invStack, acceptorHandler.getItemInputs(ProviderType.PARALLEL), acceptorHandler.getFluidInputs()) && acceptorStorage.extractEnergy(getLaunchEnergy(), true) >= getLaunchEnergy()){
                    level.getCapability(DSCapabilities.DYSON_SPHERE).ifPresent((ds) -> {
                        if(ds.addDysonSpherePart(currentRecipe.launchStack(), false)){
                            List<Ingredient> ingredients = acceptorHandler.consumeItemInputs(currentRecipe.allInputs());
                            if(ingredients.contains(currentRecipe.input())){
                                invStack.shrink(1);
                                inventory.setStackInSlot(0, invStack);
                            }
                            acceptorStorage.extractEnergy(getLaunchEnergy(), false);
                            acceptorHandler.consumeFluidInputs(currentRecipe.fluidInputs()); //we checked the recipe and have no internal tank. there should never be a returned fluid here.
                            level.playSound(null, worldPosition, ModSounds.RAILGUN_SHOT.get(), SoundSource.BLOCKS);
                        } else {
                            // set unable to add flag
                            canAddToDS = false;
                        }
                    });
                } else {
                    currentRecipe = null;
                }
            }
            if(currentRecipe == null){
                if(!invStack.isEmpty()){
                    setCurrentRecipe();
                }
            }
            
            if(ticksElapsed++ % 5 == 0 && dirty){
                // lastEnergy = energyStorage.getEnergyStored();
                dirty = false;
                sendSyncPackageToNearbyPlayers();
            } 
            if(ticksElapsed % 200 == 20 && ModCompat.isLoaded(ModCompat.MODID.AD_ASTRA)){ //recheck the launch multiplier every 10 seconds. Gravity should not change so frequently, right?
                float last = launchMult;
                launchMult = AdAstra.getOrbitalLaunchMult(level, worldPosition);
                if(last != launchMult){
                    this.setChanged();
                    // lastEnergy = energyStorage.getEnergyStored();
                    sendSyncPackageToNearbyPlayers();
                }
            }
        } else {
            level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), getBlockState(), getBlockState(), 2, 0);
            if(!inventory.getStackInSlot(0).isEmpty()){
                setCurrentRecipe();
            } else {
                currentRecipe = null;
            }
            acceptorHandler.tick();
        }
    }

    protected void setCurrentRecipe(){
        List<OrbitalLaunchRecipe> recipes = new ArrayList<>(Objects.requireNonNull(getLevel()).getRecipeManager().getAllRecipesFor(ModRecipes.ORBITAL_LAUNCH_TYPE.get()));
        ItemStack input = this.inventory.getStackInSlot(0);
        List<ItemStack> extraInputs = acceptorHandler.getItemInputs(ProviderType.PARALLEL);
        List<FluidStack> fluidInputs = acceptorHandler.getFluidInputs();
        recipes.removeIf((recipe) -> {
            return !recipe.matches(input, extraInputs, fluidInputs);
        });
        currentRecipe = recipes.size() >= 1 ? recipes.get(0) : null;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        dirty = true;
    }

    public boolean canSeeSky(){
        return level != null && level.canSeeSky(worldPosition.above());
    }

    public boolean canAddToDS(){
        return canAddToDS;
    }

    public Optional<OrbitalLaunchRecipe> getRecipe(){
        return currentRecipe != null ? Optional.of(currentRecipe) : Optional.empty();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(!level.isClientSide && ModCompat.isLoaded(ModCompat.MODID.AD_ASTRA)){
            launchMult = AdAstra.getOrbitalLaunchMult(level, worldPosition);
        }
        acceptorHandler.updateNeighbors(level, worldPosition);
        acceptorHandler.markForRefresh();
    }

    public void onNeighborChange(){
        acceptorHandler.updateNeighbors(level, worldPosition);
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
        if(tag.contains("acceptor")){
            acceptorHandler.deserializeNBT(tag.getCompound("acceptor"));
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("Inventory", inventory.serializeNBT());
        tag.putFloat("launchMult", launchMult);
        tag.putBoolean("canAdd", canAddToDS);
        tag.put("acceptor", acceptorHandler.serializeNBT());
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
        return currentRecipe != null ? (int) (currentRecipe.baseEnergy() * launchMult) : 0;
    }

    public int getEnergyScaled(float scale){
        int launch = getLaunchEnergy();
        if(launch == 0){
            return 0;
        }
        return (int) (energyStorage.getEnergyStored() * scale / Math.max(getLaunchEnergy(), 1));
    }

    public void onRemove() {
        this.dropContent();
        acceptorHandler.onRemove();
    }
    

}
