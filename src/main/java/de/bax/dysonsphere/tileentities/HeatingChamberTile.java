package de.bax.dysonsphere.tileentities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.heat.HeatHandler;
import de.bax.dysonsphere.capabilities.heat.IHeatContainer;
import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import de.bax.dysonsphere.capabilities.items.SimpleItemStackHandler;
import de.bax.dysonsphere.color.ModColors.ITintableTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class HeatingChamberTile extends BaseTile implements ITintableTile, IHeatTile{

    public static final int SLOTS_INPUT = 8;
    public static final int SLOTS_OUTPUT = 16;
    public static final int SLOTS = SLOTS_INPUT + SLOTS_OUTPUT; 
    public static final int INPUT_START = 0;
    public static final int OUTPUT_START = SLOTS_INPUT;
    public static final double MAX_HEAT = 1700;
    
    public static double heatConsumption = 1;

    public static double campingStart = 450;
    public static double smokingStart = 500;
    public static double smeltingStart = 800;
    public static double blastingStart = 1100;

    private static record recipe(Ingredient input, ItemStack output, int cookingTime, double minHeat){
        public ItemStack output(){
            return output.copy();
        }

        public ItemStack output(int count){
            return output.copyWithCount(count);
        }
    };
    public static List<recipe> possibleRecipes = new ArrayList<>();

    public SimpleItemStackHandler input = new SimpleItemStackHandler(SLOTS_INPUT){
        @Override 
        protected void onContentsChanged(int slot) {
            setChanged();
        };
    };

    public SimpleItemStackHandler output = new SimpleItemStackHandler(SLOTS_OUTPUT){
        @Override 
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
    public LazyOptional<IHeatContainer> lazyHeat = LazyOptional.of(() -> heatHandler);

    protected boolean dirty = false;
    protected int ticksElapsed = 0;
    protected recipe[] lastRecipes = new recipe[SLOTS_INPUT];
    protected int[] workTimes = new int[SLOTS_INPUT];
    

    public HeatingChamberTile(BlockPos pos, BlockState state) {
        super(ModTiles.HEATING_CHAMBER.get(), pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(ForgeCapabilities.ITEM_HANDLER)){
            return lazyInv.cast();
        } else if(cap.equals(DSCapabilities.HEAT)){
            return lazyHeat.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

    }

    public void tick(){
        if(!level.isClientSide){
            if(++ticksElapsed % 5 == 0){
                if(heatHandler.getHeatStored() > campingStart){
                    for(int i = 0; i < SLOTS_INPUT;  i++){
                        ItemStack inputStack = input.getStackInSlot(i);
                        if(lastRecipes[i] == null && !inputStack.isEmpty()){
                            lastRecipes[i] = possibleRecipes.stream().filter(r -> r.input.test(inputStack)).sorted((a,b) -> Integer.compare(a.cookingTime, b.cookingTime)).findFirst().orElse(null);
                        }
                        
                        if(lastRecipes[i] != null){
                            if(!lastRecipes[i].input.test(inputStack)){
                                lastRecipes[i] = null;
                                workTimes[i] = 0;
                                setChanged();
                            } else {
                                if(heatHandler.getHeatStored() >= lastRecipes[i].minHeat){
                                    if(workTimes[i] < lastRecipes[i].cookingTime){
                                        workTimes[i] += (int) (Math.log(heatHandler.getHeatStored() - lastRecipes[i].minHeat)) * 5; //compensating for running only once every 5 ticks
                                        heatHandler.extractHeat(heatConsumption * inputStack.getCount(), false);
                                    } else {
                                        int outputableCount = outputItemStack(lastRecipes[i].output(inputStack.getCount()), i, true);
                                        if(outputableCount > 0){
                                            inputStack.shrink(outputableCount);
                                            input.setStackInSlot(i, inputStack);
                                            outputItemStack(lastRecipes[i].output(outputableCount), i, false);
                                            workTimes[i] = 0;
                                        }
                                    }
                                }
                                
                            }
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

    /**
     * @return the amount that can be placed into the output slots
     */
    protected int outputItemStack(ItemStack stack, int slot, boolean simulate){
        ItemStack remainder = output.insertItem(slot, stack, simulate);
        if(remainder.isEmpty()) return stack.getCount();
        remainder = output.insertItem(slot + OUTPUT_START, stack, simulate);//assuming every input slot has 2 output slots.
        return stack.getCount() - remainder.getCount();
    }

    @Override
    public void load(@Nonnull CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("input")){
            input.deserializeNBT(pTag.getCompound("input"));
        }
        if(pTag.contains("output")){
            output.deserializeNBT(pTag.getCompound("output"));
        }
        if(pTag.contains("heat")){
            heatHandler.deserializeNBT(pTag.getCompound("heat"));
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("input", input.serializeNBT());
        pTag.put("output", output.serializeNBT());
        pTag.put("heat", heatHandler.serializeNBT());
    }

    @Override
    public void setChanged() {
        dirty = true;
        super.setChanged();
    }

    public void onRemove(){
        for(int i = 0; i < inventory.getSlots(); i++){
            ItemStack stack = inventory.getStackInSlot(i);
            if(!stack.isEmpty()){
                ItemEntity itemEntity = new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
                level.addFreshEntity(itemEntity);
            }
        }
    }

    public void onNeighborChange(){
        heatHandler.updateNeighbors(level, worldPosition);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if(possibleRecipes.isEmpty()){
            addAllRecipes(level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING), smeltingStart);
            addAllRecipes(level.getRecipeManager().getAllRecipesFor(RecipeType.BLASTING), blastingStart);
            addAllRecipes(level.getRecipeManager().getAllRecipesFor(RecipeType.CAMPFIRE_COOKING), campingStart);
            addAllRecipes(level.getRecipeManager().getAllRecipesFor(RecipeType.SMOKING), smokingStart);
        }
    }

    protected void addAllRecipes(Collection<? extends AbstractCookingRecipe> cookingRecipes, double minHeat){
        for(AbstractCookingRecipe cookingRecipe : cookingRecipes){
            ItemStack recipeOutput = cookingRecipe.getResultItem(level.registryAccess());
            if(!recipeOutput.isEmpty()){
                NonNullList<Ingredient> ingredients = cookingRecipe.getIngredients();
                if(ingredients.isEmpty()){
                    continue;
                }
                Ingredient ingredient = Ingredient.merge(ingredients);
                possibleRecipes.add(new recipe(ingredient, recipeOutput, cookingRecipe.getCookingTime(), minHeat));
            }
        }
    }

    private class InvWrapper implements IItemHandler {

        @Override
        public int getSlots() {
            return SLOTS;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return slot >= INPUT_START && slot < OUTPUT_START ? input.getStackInSlot(slot) : output.getStackInSlot(slot - OUTPUT_START);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return slot >= INPUT_START && slot < OUTPUT_START ? input.insertItem(slot, stack, simulate) : stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot >= OUTPUT_START ? output.extractItem(slot - OUTPUT_START, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot >= INPUT_START && slot < OUTPUT_START ? input.getSlotLimit(slot) : output.getSlotLimit(slot - OUTPUT_START);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot >= INPUT_START && slot < OUTPUT_START ? input.isItemValid(slot, stack) : output.isItemValid(slot - OUTPUT_START, stack);
        }
        
    }

    @Override
    public IHeatContainer getHeatContainer() {
        return heatHandler;
    }

    @Override
    public int getTintColor(int tintIndex) {
        if (tintIndex == 0){
            int col = 0xFFFF0000;
            int offset = 255 - (int) Math.min(Math.max(heatHandler.getHeatStored() - HeatHandler.HEAT_AMBIENT, 0) / 5, 255);

            return col + offset + (offset << 8);
        }
        return 0xFFFFFFFF;
    }

    
    
}
