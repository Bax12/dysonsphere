package de.bax.dysonsphere.tileentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import de.bax.dysonsphere.capabilities.inputHatch.IInputAcceptor;
import de.bax.dysonsphere.capabilities.inputHatch.IInputProvider;
import de.bax.dysonsphere.capabilities.inputHatch.IInputProvider.ProviderType;
import de.bax.dysonsphere.capabilities.inputHatch.InputAcceptorHandler;
import de.bax.dysonsphere.capabilities.orbitalLaser.ILaserReceiver;
import de.bax.dysonsphere.color.ModColors.ITintableTile;
import de.bax.dysonsphere.recipes.LaserCraftingRecipe;
import de.bax.dysonsphere.recipes.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class LaserCrafterTile extends BaseTile implements ILaserReceiver, ITintableTile, IHeatTile{


    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static int ENERGY_BLEED_STATIC = 10000;
    public static double ENERGY_BLEED_SCALING = 0.05d;
    public static double MAX_HEAT = 1700;
    public static double ENERGY_INPUT_HEAT_RESISTANCE = 50d;
    public static double ENERGY_BLEED_TO_HEAT = 50d;


    public ItemStackHandler input = new ItemStackHandler(1) {
        
        public int getSlotLimit(int slot) {
            return 1;
        };
        
        protected void onContentsChanged(int slot) {
            setChanged();
        };
    };

    public ItemStackHandler output = new ItemStackHandler(1) {
        
        
        protected void onContentsChanged(int slot) {
            setChanged();
        };
    };

    public InvWrapper inventory = new InvWrapper();    

    public HeatHandler heatHandler = new HeatHandler(MAX_HEAT){
        @Override
        public double receiveHeat(double maxReceive, boolean simulate) {
            if(!simulate){
                setChanged();
            }
            return super.receiveHeat(maxReceive, simulate);
        }

        @Override
        public double extractHeat(double maxExtract, boolean simulate) {
            if(!simulate){
                setChanged();
            }
            return super.extractHeat(maxExtract, simulate);
        }
    };

    public InputAcceptorHandler acceptorHandler = new InputAcceptorHandler(this){
        public void addInputProvider(LazyOptional<IInputProvider> provider) {
            super.addInputProvider(provider);
            if(provider.isPresent()){
                setChanged();
            }
        };
    };

    

    protected LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> inventory);
    protected LazyOptional<ILaserReceiver> lazyLaserReceptor = LazyOptional.of(() -> this);
    protected LazyOptional<IHeatContainer> lazyHeat = LazyOptional.of(() -> heatHandler);
    protected LazyOptional<IInputAcceptor> lazyAcceptor = LazyOptional.of(() -> acceptorHandler);


    protected double energy = 0;
    protected LaserCraftingRecipe currentRecipe;

    protected boolean dirty = false;
    protected int ticksElapsed = 0;
    


    public LaserCrafterTile(BlockPos pos, BlockState state) {
        super(ModTiles.LASER_CRAFTER.get(), pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.LASER_RECEIVER) && side != Direction.DOWN){
            return lazyLaserReceptor.cast();
        }
        if(cap.equals(ForgeCapabilities.ITEM_HANDLER)){
            return lazyInv.cast();
        }
        if(cap.equals(DSCapabilities.HEAT)){
            return lazyHeat.cast();
        }
        if(cap.equals(DSCapabilities.INPUT_ACCEPTOR)){
            return lazyAcceptor.cast();
        }


        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyLaserReceptor.invalidate();
        lazyInv.invalidate();
        lazyHeat.invalidate();
    }

    @Override
    public void receiveLaserEnergy(double energy) {
        if(energy <= 0) return;
        energy = energy / Math.max(1, (getCurrentHeat() - HeatHandler.HEAT_AMBIENT) / ENERGY_INPUT_HEAT_RESISTANCE);
        this.energy = this.energy + energy;
        dirty = true;
    }
    
    // protected void craft(double energyInput){
    //     DysonSphere.LOGGER.info("LaserCrafterTile craft energy: {}", energyInput);
    //     DysonSphere.LOGGER.info("LaserCrafterTile craft recipes: {}", level.getRecipeManager().getAllRecipesFor(ModRecipes.LASER_CRAFTING_TYPE.get()).size());

    //     var recipe = level.getRecipeManager().getRecipeFor(ModRecipes.LASER_CRAFTING_TYPE.get(), new RecipeWrapper(input), level);

    //     if(recipe.isPresent()) {
    //         DysonSphere.LOGGER.info("LaserCrafterTile craft matching recipe: {}", recipe.get().getId());
    //         if(energyInput >= recipe.get().inputEnergy()){
    //             input.extractItem(0, 1, false);
    //             output.insertItem(0, recipe.get().output(), false);
    //         }
    //     }
    // }

    public void tick(){
        if(!level.isClientSide){
            ticksElapsed++;
            if(ticksElapsed % 5 == 0){
                if(energy > 0){
                    if(currentRecipe != null){
                        if((currentRecipe.matches(input.getStackInSlot(0), acceptorHandler.getInputs(ProviderType.PARALLEL)))){
                            if(energy >= currentRecipe.inputEnergy()){
                                if(canOutput()){
                                    input.extractItem(0, 1, false);
                                    output.insertItem(0, currentRecipe.output(), false);
                                    energy = Math.max(0, energy - currentRecipe.inputEnergy()); 
                                    currentRecipe = null;
                                }
                            }
                        } else {
                            currentRecipe = null;
                        }
                    }
                    if(currentRecipe == null){
                        if(!input.getStackInSlot(0).isEmpty()){
                            setCurrentRecipe();
                        }
                    }
                    bleedEnergy();
                    heatHandler.splitShare();
                }
                acceptorHandler.tick();
                if(dirty){
                    dirty = false;
                    sendSyncPackageToNearbyPlayers();
                    // level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), getBlockState(), getBlockState(), 11, 512);
                }
            }
        } else {
            level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), getBlockState(), getBlockState(), 2, 0);
            if(!input.getStackInSlot(0).isEmpty()){
                setCurrentRecipe();
            } else {
                currentRecipe = null;
            }
            acceptorHandler.tick();
            
        }
    }

    protected void bleedEnergy(){
        double toBleed = Math.min(energy, Math.max(ENERGY_BLEED_STATIC, ENERGY_BLEED_SCALING * this.energy));
        this.heatHandler.receiveHeat(toBleed / ENERGY_BLEED_TO_HEAT, false);
        this.energy -= toBleed;
        dirty = true;
    }

    protected void setCurrentRecipe(){
        List<LaserCraftingRecipe> recipes = new ArrayList<>(level.getRecipeManager().getAllRecipesFor(ModRecipes.LASER_CRAFTING_TYPE.get()));
        recipes.removeIf((recipe) -> {
            return !recipe.matches(this.input.getStackInSlot(0), acceptorHandler.getInputs(ProviderType.PARALLEL));
        });
        currentRecipe = recipes.size() >= 1 ? recipes.get(0) : null; //ambiguous recipe ==> whatever recipe
    }

    protected boolean canOutput(){
        return output.insertItem(0, currentRecipe.output(), true).isEmpty();
    }

    public double getCharge(){
        return energy;
    }

    public float getNeededChargeRatio(){
        return (float) (currentRecipe != null ? currentRecipe.inputEnergy() / energy : 0f);
    }

    public Optional<LaserCraftingRecipe> getRecipe(){
        return currentRecipe != null ? Optional.of(currentRecipe) : Optional.empty();
    }

    public double getCurrentHeat(){
        return heatHandler.getHeatStored();
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("invInput")){
            input.deserializeNBT(pTag.getCompound("invInput"));
        }
        if(pTag.contains("invOutput")){
            output.deserializeNBT(pTag.getCompound("invOutput"));
        }
        if(pTag.contains("energy")){
            energy = pTag.getDouble("energy");
        }
        if(pTag.contains("heat")){
            heatHandler.deserializeNBT(pTag.getCompound("heat"));
        }
        if(pTag.contains("acceptor")){
            acceptorHandler.deserializeNBT(pTag.getCompound("acceptor"));
        }
        // DysonSphere.LOGGER.info("laserCrafterTile load input: {}, output: {}, energy: {}", input.getStackInSlot(0), output.getStackInSlot(0), energy);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        setCurrentRecipe();
        heatHandler.updateNeighbors(level, worldPosition);
        acceptorHandler.updateNeighbors(level, worldPosition);
        acceptorHandler.markForRefresh();
    }

    public void onNeighborChange(){
        heatHandler.updateNeighbors(level, worldPosition);
        acceptorHandler.updateNeighbors(level, worldPosition);
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("invInput", input.serializeNBT());
        pTag.put("invOutput", output.serializeNBT());
        pTag.putDouble("energy", energy);
        pTag.put("heat", heatHandler.serializeNBT());
        pTag.put("acceptor", acceptorHandler.serializeNBT());
        // DysonSphere.LOGGER.info("laserCrafterTile saveAdditional input: {}, output: {}, energy: {}", input.getStackInSlot(0), output.getStackInSlot(0), energy);
    }

    public void dropContent() {
        for(int i = 0; i < inventory.getSlots(); i++){
            ItemEntity entity = new ItemEntity(level, getBlockPos().getX(),getBlockPos().getY(), getBlockPos().getZ(), inventory.getStackInSlot(i));
            level.addFreshEntity(entity);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.dirty = true;
    }

    @Override
    public int getTintColor(int tintIndex) {
        if(tintIndex == 0){
            int col = 0xFFFF0000;
            int offset = 255 - (int) Math.min(this.energy / 100000, 255);

            return col + offset + (offset << 8);
        } else if (tintIndex == 1){
            int col = 0xFFFF0000;
            int offset = 255 - (int) Math.min(Math.max(this.getCurrentHeat() - HeatHandler.HEAT_AMBIENT, 0) / 5, 255);

            return col + offset + (offset << 8);
        }
        return 0xFFFFFFFF;
    }
    private class InvWrapper implements IItemHandler {

        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return slot == SLOT_INPUT ? input.getStackInSlot(0) : output.getStackInSlot(0);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return slot == SLOT_INPUT ? input.insertItem(0, stack, simulate) : stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == SLOT_OUTPUT ? output.extractItem(0, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == SLOT_INPUT ? input.getSlotLimit(0) : output.getSlotLimit(0);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == SLOT_INPUT ? input.isItemValid(0, stack) : output.isItemValid(0, stack);
        }
        
    }
    @Override
    public IHeatContainer getHeatContainer() {
        return heatHandler;
    }

    public void onRemove() {
        this.dropContent();
        acceptorHandler.onRemove();
    }




}
