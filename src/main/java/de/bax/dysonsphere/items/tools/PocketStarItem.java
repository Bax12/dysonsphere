package de.bax.dysonsphere.items.tools;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import de.bax.dysonsphere.capabilities.energy.ItemEnergyHandler;
import de.bax.dysonsphere.capabilities.fluid.ItemFluidHandlerMulti;
import de.bax.dysonsphere.compat.ModCompat;
import de.bax.dysonsphere.compat.curio.Curios;
import de.bax.dysonsphere.recipes.AnnihilationRecipe;
import de.bax.dysonsphere.recipes.ModRecipes;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class PocketStarItem extends Item {
    
    public static int inverseEfficiency = 1000;
    public static int minDelay = 500;
    public static long energyCapacity = 20_000_000_000_000l; //2 * 10^13
    public static int fluidCapacity = 500;

    public PocketStarItem(){
        super(new Item.Properties());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        
        
        LazyOptional<IFluidHandlerItem> lazyFluid = LazyOptional.of(() -> 
            new ItemFluidHandlerMulti(stack, 2)
                    .setTankParams(0, fluidCapacity, AnnihilationRecipe.getMatterInputFluids())
                    .setTankParams(1, fluidCapacity, AnnihilationRecipe.getAntiInputFluids())
        );      
        LazyOptional<IEnergyStorage> lazyEnergy = LazyOptional.of(() -> 
            new ItemEnergyHandler(stack, 2_000_000_000){ //2*10^9 ~ 2^31
                @Override
                public int extractEnergy(int maxExtract, boolean simulate) {
                    return (int) PocketStarItem.extractEnergy(stack, maxExtract, simulate);
                }

                public int receiveEnergy(int maxReceive, boolean simulate) {
                    return 0;
                };
            }
        );
        
        
        return new ICapabilityProvider() {
            

            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if(cap.equals(ForgeCapabilities.FLUID_HANDLER_ITEM)){
                    return lazyFluid.cast();
                } else if(cap.equals(ForgeCapabilities.ENERGY)){
                    return lazyEnergy.cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Entity entity, int pSlotId, boolean pIsSelected) {
        AnnihilationRecipe.generateInputLists(level); //Maybe this works good enough?
        if(!level.isClientSide()){
            stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent((fluidHandler) -> {

                CompoundTag tag = stack.getOrCreateTag();
                long lastGenTick = tag.getLong("lastGen");
                if(level.getGameTime() - lastGenTick  > minDelay){
                    Optional<AnnihilationRecipe> recipe = level.getRecipeManager().getAllRecipesFor(ModRecipes.ANNIHILATION_TYPE.get()).stream()
                        .filter((rec) -> rec.matches(fluidHandler.getFluidInTank(0), fluidHandler.getFluidInTank(1))).findFirst();
                    
                    recipe.ifPresent((rec) -> {
                        if(getEnergyStored(stack) == 0 || receiveEnergy(stack, rec.energy() / inverseEfficiency, true) == rec.energy() / inverseEfficiency){
                            ((ItemFluidHandlerMulti) fluidHandler).drain(0, rec.fluidMatter().getAmount(), FluidAction.EXECUTE);
                            ((ItemFluidHandlerMulti) fluidHandler).drain(1, rec.fluidAnti().getAmount(), FluidAction.EXECUTE);
                            
                            receiveEnergy(stack, rec.energy() / inverseEfficiency, false);

                            tag.putLong("lastGen", level.getGameTime());
                            stack.setTag(tag);
                        }
                    });
                }
            });
            chargeInventory(stack, entity);
        }
    }

    protected void chargeInventory(ItemStack stack, Entity entity){
        if(getEnergyStored(stack) > 0 && entity instanceof Player player) {
            for (ItemStack targetStack : player.getInventory().offhand) {
                transferEnergy(stack, targetStack);
            };
            for (ItemStack targetStack : player.getInventory().armor) {
                transferEnergy(stack, targetStack);
            };
            for (ItemStack targetStack : player.getInventory().items) {
                transferEnergy(stack, targetStack);
            };
            if(ModCompat.isLoaded(ModCompat.MODID.CURIOS)){
                Curios.getMatchingCurios(player, (itemStack) -> {return itemStack.getCapability(ForgeCapabilities.ENERGY).isPresent();}).forEach((targetStack) -> {
                    transferEnergy(stack, targetStack);
                });
            }
        }
    }

    protected void transferEnergy(ItemStack stack, ItemStack targetStack){
        targetStack.getCapability(ForgeCapabilities.ENERGY).ifPresent((targetEnergy) -> {
            int transfer = targetEnergy.receiveEnergy((int) extractEnergy(stack, Integer.MAX_VALUE, true), true);
            if(transfer > 0){
                targetEnergy.receiveEnergy((int) extractEnergy(stack, transfer, false), false);
            }
        });
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {

        tooltip.add(Component.translatable("tooltip.dysonsphere.energy_display", AssetUtil.FLOAT_FORMAT.format(getEnergyStored(stack)), AssetUtil.FLOAT_FORMAT.format(getMaxEnergyStored())));
        stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluid -> {
            tooltip.add(Component.translatable("tooltip.dysonsphere.fluid_display", fluid.getFluidInTank(0).getDisplayName(), AssetUtil.FLOAT_FORMAT.format(Math.round(fluid.getFluidInTank(0).getAmount())), AssetUtil.FLOAT_FORMAT.format(Math.round(fluid.getTankCapacity(0)))));
            tooltip.add(Component.translatable("tooltip.dysonsphere.fluid_display", fluid.getFluidInTank(1).getDisplayName(), AssetUtil.FLOAT_FORMAT.format(Math.round(fluid.getFluidInTank(1).getAmount())), AssetUtil.FLOAT_FORMAT.format(Math.round(fluid.getTankCapacity(1)))));
        });
    }

    @Override
    public boolean isBarVisible(@Nonnull ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarColor(@Nonnull ItemStack pStack) {
        return 0xDD2222;
    }

    @Override
    public int getBarWidth(@Nonnull ItemStack pStack) {
        return pStack.getCapability(ForgeCapabilities.ENERGY).map((energy) -> {
            return (int) Math.min(13, 13 * getEnergyStored(pStack) / energyCapacity);
        }).orElse(0);
    }

    protected static long getEnergyStored(ItemStack stack){
        return stack.getOrCreateTag().getLong("energy");
    }

    protected static void setEnergyStored(ItemStack stack, long energy){
        stack.getOrCreateTag().putLong("energy", energy);
    }

    
    protected static long getMaxEnergyStored() {
        return energyCapacity;
    }

    public static long receiveEnergy(ItemStack stack, long maxReceive, boolean simulate) {
        long energy = getEnergyStored(stack);
        long energyReceived = Math.min(energyCapacity - energy, maxReceive);
        if (!simulate){
            setEnergyStored(stack, energy + energyReceived);
        }
            
        return energyReceived;
    }

    
    public static long extractEnergy(ItemStack stack, long maxExtract, boolean simulate) {
        long energy = getEnergyStored(stack);
        long energyExtracted = Math.min(energy, maxExtract);
        if (!simulate){
            setEnergyStored(stack, energy - energyExtracted);
        }
        return energyExtracted;
    }

    

}
