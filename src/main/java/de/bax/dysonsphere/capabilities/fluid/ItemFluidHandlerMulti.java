package de.bax.dysonsphere.capabilities.fluid;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import de.bax.dysonsphere.util.FluidIngredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class ItemFluidHandlerMulti  implements IFluidHandlerItem {

    protected ItemStack container;
    protected int capacity[];
    protected List<FluidIngredient> validFluids[];

    public ItemFluidHandlerMulti(ItemStack container, int tanks){
        this.container = container;
        this.capacity = new int[tanks];
        this.validFluids = new List[tanks];
        for (int i = 0; i < capacity.length; i++) {
            capacity[i] = 0;
            validFluids[i] = List.of();
        }
    }

    public ItemFluidHandlerMulti setTankParams(int tank, int capacity, List<FluidIngredient> validFluids){
        this.capacity[tank] = capacity;
        this.validFluids[tank] = validFluids;

        return this;
    }
    

    @Override
    public int getTanks() {
        return capacity.length;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return getFluid(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity[tank];
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return validFluids[tank].stream().anyMatch((fluid) -> fluid.test(stack));
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if(resource.isEmpty()) return 0;
        for (int i = 0; i < capacity.length; i++) {
            if(isFluidValid(i, resource)){
                FluidStack contained = getFluid(i);
                if (contained.isEmpty()) {
                    int fillAmount = Math.min(capacity[i], resource.getAmount());

                    if (action.execute()) {
                        FluidStack filled = resource.copy();
                        filled.setAmount(fillAmount);
                        setFluid(i, filled);
                    }

                    return fillAmount;
                } else {
                    if (contained.isFluidEqual(resource)) {
                        int fillAmount = Math.min(capacity[i] - contained.getAmount(), resource.getAmount());

                        if (action.execute() && fillAmount > 0) {
                            contained.grow(fillAmount);
                            setFluid(i, contained);
                        }
                        return fillAmount;
                    }
                }
            }    
        }
        return 0;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        for (int i = 0; i < capacity.length; i++) {
            FluidStack contained = getFluid(i);
            if(!contained.isEmpty() && contained.isFluidEqual(resource)){
                int drainAmount = Math.min(resource.getAmount(), contained.getAmount());
                FluidStack drained = contained.copy();
                drained.setAmount(drainAmount);

                if(action.execute()){
                    contained.shrink(drainAmount);
                    setFluid(i, contained);
                }

                return drained;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        for (int i = 0; i < capacity.length; i++) {
            FluidStack contained = getFluid(i);
            if(!contained.isEmpty()){
                int drainAmount = Math.min(maxDrain, contained.getAmount());
                FluidStack drained = contained.copy();
                drained.setAmount(drainAmount);

                if(action.execute()){
                    contained.shrink(drainAmount);
                    setFluid(i, contained);
                }

                return drained;
            }
        }
        return FluidStack.EMPTY;
    }

    public FluidStack drain(int tank, int maxDrain, FluidAction action){
        FluidStack contained = getFluid(tank);
        if(!contained.isEmpty()){
            int drainAmount = Math.min(maxDrain, contained.getAmount());
            FluidStack drained = contained.copy();
            drained.setAmount(drainAmount);

            if(action.execute()){
                contained.shrink(drainAmount);
                setFluid(tank, contained);
            }

            return drained;
        }
        
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return container;
    }

    protected FluidStack getFluid(int tank){
        CompoundTag tagCompound = container.getTag();
        if (tagCompound == null || !tagCompound.contains("fluid"+tank)) {
            return FluidStack.EMPTY;
        }
        return FluidStack.loadFluidStackFromNBT(tagCompound.getCompound("fluid"+tank));
    }

    protected void setFluid(int tank, FluidStack fluid){
        if (!container.hasTag()) {
            container.setTag(new CompoundTag());
        }

        CompoundTag fluidTag = new CompoundTag();
        fluid.writeToNBT(fluidTag);
        container.getTag().put("fluid" + tank, fluidTag);
    }
    
}
