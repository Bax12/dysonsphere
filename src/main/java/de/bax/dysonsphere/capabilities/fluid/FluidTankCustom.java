package de.bax.dysonsphere.capabilities.fluid;

import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

//fluidTank with internal fill and drain Methods. Like forgetank in 1.12.
public class FluidTankCustom extends FluidTank {

    public FluidTankCustom(int capacity) {
        super(capacity);
        
    }

    public FluidTankCustom(int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
    }

    public boolean canDrain(){
        return true;
    }

    public boolean canFill(){
        return true;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        return canDrain() ? drainInternal(maxDrain, action) : FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        return canDrain() ? drainInternal(resource, action) : FluidStack.EMPTY;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return canFill() ? fillInternal(resource, action) : 0;
    }
    
    public @NotNull FluidStack drainInternal(int maxDrain, FluidAction action) {
        return super.drain(maxDrain, action);
    }

    public @NotNull FluidStack drainInternal(FluidStack resource, FluidAction action) {
        return super.drain(resource, action);
    }
    
    public int fillInternal(FluidStack resource, FluidAction action) {
        return super.fill(resource, action);
    }


}
