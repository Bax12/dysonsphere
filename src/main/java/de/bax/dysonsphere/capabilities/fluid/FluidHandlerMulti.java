package de.bax.dysonsphere.capabilities.fluid;

import org.jetbrains.annotations.NotNull;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidHandlerMulti implements IFluidHandler {

    protected final FluidTankCustom fluidHandlers[];

    protected int count = 0;

    public FluidHandlerMulti(int capacity) {
        this.fluidHandlers = new FluidTankCustom[capacity];
    }

    public FluidHandlerMulti(FluidTankCustom[] tanks){
        this.fluidHandlers = tanks;
    }


    public void addFluidHandler(FluidTankCustom handler) {
        fluidHandlers[count] = handler;
        count++;
    }

    

    @Override
    public int getTanks() {
        return count;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return fluidHandlers[tank].getFluidInTank(0);
    }

    @Override
    public int getTankCapacity(int tank) {
        return fluidHandlers[tank].getTankCapacity(0);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return fluidHandlers[tank].isFluidValid(0, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource == FluidStack.EMPTY)
            return 0;
        IFluidHandler handler = null;
        for(int i = 0; i < count; i++){
            if(fluidHandlers[i].canFill() && fluidHandlers[i].isFluidValid(0, resource)){
                handler = fluidHandlers[i];
                break;
            }
        }
        if (handler == null)
            return 0;
        return handler.fill(resource, action);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource == FluidStack.EMPTY)
            return FluidStack.EMPTY;
        IFluidHandler handler = null;
        for(int i = 0; i < count; i++){
            if(fluidHandlers[i].canDrain() && fluidHandlers[i].isFluidValid(0, resource)){
                handler = fluidHandlers[i];
                break;
            }
        }
        if (handler == null)
            return FluidStack.EMPTY;
        return handler.drain(resource, action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        for (IFluidHandler handler : fluidHandlers) {
            FluidStack drain = handler.drain(maxDrain, action);
            if (drain != null && !drain.isEmpty())
                return drain;
        }
        return FluidStack.EMPTY;
    }

}
