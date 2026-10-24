package de.bax.dysonsphere.tileentities;

import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.fluid.FluidTankCustom;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import de.bax.dysonsphere.recipes.CrystallizerRecipe;
import de.bax.dysonsphere.recipes.ModRecipes;
import de.bax.dysonsphere.util.FluidIngredient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class SiliconCrystallizerTile extends BaseTile {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOTS = 2;
    public static final double heatExtraction = 100; //heat generated per working tick. Slow down the hotter it gets.
    public static int fluidCapacity = 20000;
    public static double MAX_HEAT = 1700;

    public static List<FluidIngredient> inputs;


    public FluidTankCustom inputTank = new FluidTankCustom(fluidCapacity){
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            setChanged();
        };

        public boolean isFluidValid(FluidStack stack) {
            return inputs.stream().anyMatch(i -> i.matchesFluid(stack));
        };
    };

    public ItemStackHandler seedInput = new ItemStackHandler(1){
        protected void onContentsChanged(int slot) {
            setChanged();
        };
    };

    public ItemStackHandler output = new ItemStackHandler(1){
        protected void onContentsChanged(int slot) {
            setChanged();
        };
    };

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

    public InvWrapper inventory = new InvWrapper();

    public LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> inventory);
    public LazyOptional<IFluidHandler> lazyFluid = LazyOptional.of(() -> inputTank);
    public LazyOptional<IHeatContainer> lazyHeat = LazyOptional.of(() -> heatHandler);

    protected CrystallizerRecipe currentRecipe = null;
    protected double extractedHeat = 0;
    protected double recipeHeat = 0;
    protected boolean dirty;
    protected int ticksElapsed = 0;

    public SiliconCrystallizerTile(BlockPos pos, BlockState state) {
        super(ModTiles.SILICON_CRYSTALLIZER.get(), pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER){
            return lazyInv.cast();
        } else if (cap == ForgeCapabilities.FLUID_HANDLER){
            return lazyFluid.cast();
        } else if (cap == DSCapabilities.HEAT){
            return lazyHeat.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyInv.invalidate();
        lazyFluid.invalidate();
        lazyHeat.invalidate();
    }

    @Override
    public void setChanged() {
        dirty = true;
        super.setChanged();
    }
    

    public void tick(){
        if(!level.isClientSide){
            if(++ticksElapsed % 5 == 0){
                
                if((currentRecipe == null && !inputTank.getFluid().isEmpty()) || (currentRecipe != null && !currentRecipe.matches(inputTank.getFluid()))){
                    setRecipe();
                }

                if(currentRecipe != null && heatHandler.getHeatStored() <= currentRecipe.maxHeat()){
                    double heatToExtract = heatExtraction * Math.max(0, 1 - (heatHandler.getHeatStored() / currentRecipe.maxHeat()));
                    heatHandler.receiveHeat(heatToExtract, false);
                    extractedHeat += heatToExtract;
                    setChanged();

                    if(extractedHeat > currentRecipe.heatCapacity()){
                        ItemStack outputStack = currentRecipe.output();
                        if(output.insertItem(0, outputStack, true).isEmpty()){
                            inputTank.drainInternal(currentRecipe.input().getAmount(), FluidAction.EXECUTE);
                            
                            extractedHeat = 0;
                            if(!seedInput.getStackInSlot(0).isEmpty()){
                                outputStack.getCapability(DSCapabilities.SEEDED).ifPresent((seeded) -> {
                                    seeded.setSeed(seedInput.getStackInSlot(0));
                                    seedInput.extractItem(0, 1, false);
                                });
                            }
                            
                            output.insertItem(0, outputStack, false);
                        }
                    }

                }

                heatHandler.splitShare();

                if(dirty){
                    dirty = false;
                    sendSyncPackageToNearbyPlayers();
                }
            }
        }
    }

    protected void setRecipe(){
        currentRecipe = level.getRecipeManager().getAllRecipesFor(ModRecipes.CRYSTALLIZER_TYPE.get()).stream().filter((recipe) -> recipe.matches(inputTank.getFluid())).findFirst().orElse(null);
        if(currentRecipe != null){
            recipeHeat = currentRecipe.heatCapacity();
        } else {
            recipeHeat = 0;
        }
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("inputTank")){
            inputTank.readFromNBT(pTag.getCompound("inputTank"));
        }
        if(pTag.contains("seedInput")){
            seedInput.deserializeNBT(pTag.getCompound("seedInput"));
        }
        if(pTag.contains("output")){
            output.deserializeNBT(pTag.getCompound("output"));
        }
        if(pTag.contains("heat")){
            heatHandler.deserializeNBT(pTag.getCompound("heat"));
        }
        if(pTag.contains("heatExtracted")){
            extractedHeat = pTag.getDouble("heatExtracted");
        }
        if(pTag.contains("recipeHeat")){
            recipeHeat = pTag.getDouble("recipeHeat");
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inputTank", inputTank.writeToNBT(new CompoundTag()));
        pTag.put("seedInput", seedInput.serializeNBT());
        pTag.put("output", output.serializeNBT());
        pTag.put("heat", heatHandler.serializeNBT());
        pTag.putDouble("heatExtracted", extractedHeat);
        pTag.putDouble("recipeHeat", recipeHeat);
    }

    public void onRemove(){
        for(int i = 0; i < inventory.getSlots(); i++){
            ItemStack stack = inventory.getStackInSlot(i);
            if(!stack.isEmpty()){
                ItemEntity entity = new ItemEntity(level, getBlockPos().getX(),getBlockPos().getY(), getBlockPos().getZ(), stack);
                level.addFreshEntity(entity);
            }
        }
    }

    public void onNeighborChange(){
        heatHandler.updateNeighbors(level, worldPosition);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(inputs == null){
            inputs = level.getRecipeManager().getAllRecipesFor(ModRecipes.CRYSTALLIZER_TYPE.get()).stream().map(r -> r.input()).toList();
        }
        heatHandler.updateNeighbors(level, worldPosition);
    }

    public int getProgressScaled(int scale){
        return (int) (scale * extractedHeat / recipeHeat);
    }

    private class InvWrapper implements IItemHandler {

        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return slot == SLOT_INPUT ? seedInput.getStackInSlot(0) : output.getStackInSlot(0);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return slot == SLOT_INPUT ? seedInput.insertItem(0, stack, simulate) : stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == SLOT_OUTPUT ? output.extractItem(0, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == SLOT_INPUT ? seedInput.getSlotLimit(0) : output.getSlotLimit(0);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == SLOT_INPUT ? seedInput.isItemValid(0, stack) : output.isItemValid(0, stack);
        }
        
    }
}
