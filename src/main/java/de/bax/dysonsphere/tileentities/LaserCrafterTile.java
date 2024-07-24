package de.bax.dysonsphere.tileentities;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.ILaserReceiver;
import de.bax.dysonsphere.color.ModColors.ITintableTile;
import de.bax.dysonsphere.recipes.LaserCraftingRecipe;
import de.bax.dysonsphere.recipes.ModRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftPieces.MineShaftCorridor;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class LaserCrafterTile extends BaseTile implements ILaserReceiver, ITintableTile{


    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int ENERGY_BLEED_STATIC = 1000;
    public static final float ENERGY_BLEED_SCALING = 0.01f;


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

    

    protected LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> inventory);
    protected LazyOptional<ILaserReceiver> lazyLaserReceptor = LazyOptional.of(() -> this);


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


        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyLaserReceptor.invalidate();
        lazyInv.invalidate();
    }

    public void receiveLaserEnergy(double energy) {
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
            if(energy > 0){
                if(currentRecipe != null){
                    if((currentRecipe.input().test(input.getStackInSlot(0)))){
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
            }
            if(ticksElapsed++ % 5 == 0 && dirty){
                dirty = false;
                sendSyncPackageToNearbyPlayers();
                // level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), getBlockState(), getBlockState(), 11, 512);
            }
        } else {
            level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), getBlockState(), getBlockState(), 2, 0);
        }
    }

    protected void bleedEnergy(){
        double toBleed = Math.max(ENERGY_BLEED_STATIC, ENERGY_BLEED_SCALING * this.energy);
        this.energy = Math.max(0, energy - toBleed);
        dirty = true;
    }

    protected void setCurrentRecipe(){
        Optional<LaserCraftingRecipe> recipe = level.getRecipeManager().getRecipeFor(ModRecipes.LASER_CRAFTING_TYPE.get(), new RecipeWrapper(input), level);
        currentRecipe = recipe.orElse(null);
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

    @Override
    public void load(CompoundTag pTag) {
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
        // DysonSphere.LOGGER.info("laserCrafterTile load input: {}, output: {}, energy: {}", input.getStackInSlot(0), output.getStackInSlot(0), energy);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        setCurrentRecipe();
    }


    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("invInput", input.serializeNBT());
        pTag.put("invOutput", output.serializeNBT());
        pTag.putDouble("energy", energy);
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
        int col = 0xFFFF0000;
        int offset = 255 - (int) Math.min(this.energy / 100000, 255);

        return col + offset + (offset << 8);
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




}
