package de.bax.dysonsphere.tileentities;

import org.antlr.v4.parse.ANTLRParser.prequelConstruct_return;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.recipes.ModRecipes;
import mekanism.api.lasers.ILaserReceptor;
import mekanism.api.math.FloatingLong;
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
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

public class LaserCrafterTile extends BaseTile {


    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;


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

    

    protected ILaserReceptor laserReceptor = new ILaserReceptor() {

        @Override
        public void receiveLaserEnergy(@NotNull FloatingLong energy) {
            craft(energy.doubleValue());
        }

        @Override
        public boolean canLasersDig() {
            return false;
        }
        
    };

    protected LazyOptional<IItemHandler> lazyInv = LazyOptional.of(() -> inventory);
    protected LazyOptional<ILaserReceptor> lazyLaserReceptor = LazyOptional.of(() -> laserReceptor);


    protected boolean dirty = false;
    protected int ticksElapsed = 0;


    public LaserCrafterTile(BlockPos pos, BlockState state) {
        super(ModTiles.LASER_CRAFTER.get(), pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap.equals(DSCapabilities.LASER_RECEPTOR) && side != Direction.DOWN){
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
    
    protected void craft(double energyInput){
        DysonSphere.LOGGER.info("LaserCrafterTile craft energy: {}", energyInput);
        DysonSphere.LOGGER.info("LaserCrafterTile craft recipes: {}", level.getRecipeManager().getAllRecipesFor(ModRecipes.LASER_CRAFTING_TYPE.get()).size());

        var recipe = level.getRecipeManager().getRecipeFor(ModRecipes.LASER_CRAFTING_TYPE.get(), new RecipeWrapper(input), level);

        if(recipe.isPresent()) {
            DysonSphere.LOGGER.info("LaserCrafterTile craft matching recipe: {}", recipe.get().getId());
            if(energyInput >= recipe.get().inputEnergy()){
                input.extractItem(0, 1, false);
                output.insertItem(0, recipe.get().output(), false);
            }
        }
    }

    public void tick(){
        if(!level.isClientSide){
            if(ticksElapsed++ % 5 == 0 && dirty){
                dirty = false;
                sendSyncPackageToNearbyPlayers();
            }
        }
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
        
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("invInput", input.serializeNBT());
        pTag.put("invOutput", output.serializeNBT());
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
