package de.bax.dysonsphere.capabilities.inputHatch;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.inputHatch.IInputProvider.ProviderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;

public class InputAcceptorHandler implements IInputAcceptor, INBTSerializable<CompoundTag> {

    public Set<LazyOptional<IInputProvider>> inputProviders = new HashSet<>();
    protected Set<BlockPos> inputProviderPosSet = new HashSet<>();

    protected final BlockEntity tile;

    protected boolean shouldRefreshProvider = false;
    protected boolean unpackPosList = false;

    protected Set<LazyOptional<IInputProvider>> updateSet = new HashSet<>();
    protected Set<LazyOptional<IInputProvider>> completedUpdateSet = new HashSet<>();

    @SuppressWarnings("unchecked")
    protected LazyOptional<IInputProvider>[] neighborList = new LazyOptional[6];

    public InputAcceptorHandler(BlockEntity containingTile){
        this.tile = containingTile;

        Arrays.fill(neighborList, LazyOptional.empty());
    }

    @Override
    public List<ItemStack> getItemInputs(ProviderType type) {
        return getInventoryFromProviders(getProviders(type));
    }

    public List<LazyOptional<IInputProvider>> getProviders(ProviderType type){
        return inputProviders.stream().filter((entry -> entry.map((provider) -> {return provider.getType().equals(type);}).orElse(false))).collect(Collectors.toList());
    }

    protected List<ItemStack> getInventoryFromProviders(List<LazyOptional<IInputProvider>> lazyProviders){
        List<ItemStack> stacks = new ArrayList<ItemStack>();
        for(LazyOptional<IInputProvider> lazyProvider : lazyProviders){
            lazyProvider.ifPresent((provider) -> {
                if(provider.getAcceptor().map((acceptor) -> { //prevent multiple acceptors from using the same input
                    return acceptor.equals(this);
                }).orElse(false)){
                    provider.getInventory().ifPresent((inv) -> {
                        for(int i = 0; i < inv.getSlots(); i++){
                            ItemStack stack = inv.getStackInSlot(i);
                            // DysonSphere.LOGGER.debug("stack to add: {}", stack);
                            if(!stack.isEmpty()){
                                // DysonSphere.LOGGER.debug("stack added: {}", stack);
                                stacks.add(stack);
                            }
                        }
                    });
                }
            });
        }
        // DysonSphere.LOGGER.debug("stacks size: {}", stacks.size());
        return stacks;
    }

    public List<Ingredient> consumeItemInputs(List<Ingredient> ingredient){
        List<Ingredient> mutableIngredients = new ArrayList<>(ingredient);
        consumeItemInputType(mutableIngredients, ProviderType.SERIAL);
        consumeItemInputType(mutableIngredients, ProviderType.PARALLEL);
        return mutableIngredients;
    }

    protected List<Ingredient> consumeItemInputType(List<Ingredient> ingredients, ProviderType type){
        for(LazyOptional<IInputProvider> lazyProvider : getProviders(type)){
            lazyProvider.ifPresent((provider) -> {
                provider.getInventory().ifPresent((inventory) -> {
                    for(int i = inventory.getSlots()-1; i >= 0; i--){
                        int slotNumber = i; //to satisfy the non-changing constraint of the lambda below.
                        ingredients.removeIf((ingredient) -> {
                            if(ingredient.test(inventory.extractItem(slotNumber, 1, true))){
                                return !inventory.extractItem(slotNumber, 1, false).isEmpty();
                            }
                            return false;
                        });
                    }
                });
            });
        }
        return ingredients;
    }

    @Override
    public List<LazyOptional<IEnergyStorage>> getEnergyProviders() {
        return getProviders(ProviderType.ENERGY).stream().map((lazyProvider) -> {
            return lazyProvider.map((provider) -> {
                return provider.getEnergy();
            }).orElse(LazyOptional.empty());
        }).toList();
    }

    // //sum up the maximum extractable energy of all energy providers
    // @Override
    // public int getEnergyInput() {
    //     return getProviders(ProviderType.ENERGY).stream().mapToInt((lazyProvider) -> {
    //         return lazyProvider.map((provider) -> {
    //             if(provider.getAcceptor().map((acceptor) -> { //prevent multiple acceptors from using the same input
    //                 return acceptor.equals(this);
    //             }).orElse(false)){
    //                 return provider.getEnergy().map((energy) -> {
    //                     return energy.extractEnergy(Integer.MAX_VALUE, true);
    //                 }).orElse(0);
    //             }
    //             return 0;
    //         }).orElse(0);
    //     }).sum();
    // }

    // //returns the energy not consumed
    // @Override
    // public int consumeEnergy(int energyToConsume) {
    //     for(var lazyProvider : getProviders(ProviderType.ENERGY)){
    //         int toConsume = energyToConsume; //to satisfy the non-changing constraint of the lambda below.
    //         energyToConsume =- lazyProvider.map((provider) -> {
    //             return provider.getEnergy().map((energy) -> {
    //                 return energy.extractEnergy(toConsume, false);
    //             }).orElse(0);
    //         }).orElse(0);
    //     }
    //     return energyToConsume;
    // }


    @Override
    public List<FluidStack> getFluidInputs() {
        List<FluidStack> fluids = new ArrayList<>();
        getProviders(ProviderType.FLUID).stream().forEach((lazyProvider) -> {
            lazyProvider.ifPresent((provider) -> {
                if(provider.getAcceptor().map((acceptor) -> { //prevent multiple acceptors from using the same input
                    return acceptor.equals(this);
                }).orElse(false)){
                    provider.getFluid().ifPresent((fluid) -> {
                        for(int i = 0; i < fluid.getTanks(); i++ ){
                            fluids.add(fluid.getFluidInTank(i));
                        }
                    });
                }
            });
        });
        return fluids;
    }

    @Override
    public List<FluidStack> consumeFluidInputs(List<FluidStack> fluids) {
        return null;
    }

    public void updateNeighbors(Level level, BlockPos pos){
        for(Direction dir : Direction.values()){
            BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
            if(neighbor != null){
                LazyOptional<IInputProvider> neighborHandler = neighbor.getCapability(DSCapabilities.INPUT_PROVIDER, dir.getOpposite());
                if(neighborHandler.isPresent()){
                    neighborList[dir.ordinal()] = neighborHandler;
                }
            }
        }
    }

    public void tick(){
        if(this.shouldRefreshProvider()){
            this.refreshProvider();
        }
        // if(!updateSet.isEmpty()){
        //     updateSubUplink(ImmutableSet.copyOf(updateSet));
        // }
        // while (!updateSet.isEmpty()) {
        //     updateSubUplink(ImmutableSet.copyOf(updateSet));
        // }
        // if(!inputProviderPosSet.isEmpty()){ //used to load providers and sync them to the client.
        
        if(unpackPosList){
            inputProviders.clear(); //will never be called when no provider is present, keeping the old ones in the client memory.
            for(BlockPos pos : inputProviderPosSet){
                Level level = tile.getLevel();
                if(level != null){
                    BlockEntity providerTile = level.getBlockEntity(pos);
                    if(providerTile != null){
                        addInputProvider(providerTile.getCapability(DSCapabilities.INPUT_PROVIDER));
                    }
                }
            }
            inputProviderPosSet.clear();
            unpackPosList = false;
        }
    }

    @Override
    public void addInputProvider(LazyOptional<IInputProvider> provider) {
        if(provider.isPresent()) {
            if(inputProviders.add(provider)){
                provider.ifPresent((input) -> {
                    input.updateUplink();
                });
                onChange();
            }
        }
    }

    // @Override
    // public void removeInputProvider(LazyOptional<IInputProvider> provider) {
    //     inputProviders.remove(provider);
    //     // provider.ifPresent((input) -> {
    //     //     input.updateUplink();
    //     // });
    // }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        List<Long> packedPosList = new ArrayList<>();

        for(LazyOptional<IInputProvider> lazyProvider : inputProviders){
            lazyProvider.ifPresent((provider) -> {
                packedPosList.add(provider.getTile().getBlockPos().asLong());
            });
        }
        tag.putLongArray("providers", packedPosList);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if(nbt.contains("providers")){
            long[] packedPosList = nbt.getLongArray("providers");
            // inputProviders.clear(); //will never be called when no provider is present, keeping the old ones in the client memory.
            // Level level = tile.getLevel();
            for(long packedPos : packedPosList){
                inputProviderPosSet.add(BlockPos.of(packedPos));
                // if(level != null){
                //     BlockEntity providerTile = level.getBlockEntity(BlockPos.of(packedPos));
                //     if(providerTile != null){
                //         addInputProvider(providerTile.getCapability(DSCapabilities.INPUT_PROVIDER));
                //     }
                // }
            
            }
            unpackPosList = true;

        }
    }

    public void onRemove(){
        shouldRefreshProvider = false;
        for(LazyOptional<IInputProvider> lazyProvider : inputProviders){
            lazyProvider.ifPresent((provider) -> {
                provider.resetUplink();
            });
        }
    }

    @Override
    public void markForRefresh() {
        shouldRefreshProvider = true;
        completedUpdateSet.clear();
    }

    public boolean shouldRefreshProvider(){
        return shouldRefreshProvider;
    }

    public void refreshProvider() {
        shouldRefreshProvider = false;
        inputProviders.forEach((lazyProvider) -> {
            lazyProvider.ifPresent((provider) -> {
                provider.resetUplink();
            });
        });
        inputProviders.clear();
        updateSubUplink(Arrays.asList(neighborList));
        onChange();
    }

    protected void updateSubUplink(Collection<LazyOptional<IInputProvider>> lazyProviders){
        // updateSet.clear();
        for(LazyOptional<IInputProvider> lazyProvider : lazyProviders){
            if(completedUpdateSet.contains(lazyProvider)) continue;
            lazyProvider.ifPresent((provider) -> {
                addInputProvider(lazyProvider);
                provider.updateUplink();
                

                // updateSet.addAll(provider.getSubProviders(updateSet));
                completedUpdateSet.add(lazyProvider);
                updateSubUplink(provider.getSubProviders());
            });
        }
        // completedUpdateSet.addAll(lazyProviders); //prevent looping back to already completed providers
    }

    @Override
    public BlockEntity getTile() {
        return tile;
    }

    protected void onChange(){
        //should be overridden in the using class when an explicit change call is needed.
    }

    
    
}
