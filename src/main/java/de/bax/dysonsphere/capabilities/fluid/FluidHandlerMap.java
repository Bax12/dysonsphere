package de.bax.dysonsphere.capabilities.fluid;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

//Map implementation to handle multiple FluidTanks for different fluids
public class FluidHandlerMap implements IFluidHandler {

    protected final Map<Fluid, IFluidHandler> handlerMap;

    public FluidHandlerMap() {
        // LinkedHashMap to ensure iteration order is consistent.
        this(new LinkedHashMap<Fluid, IFluidHandler>());
    }

    public FluidHandlerMap(Map<Fluid, IFluidHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public void addFluidHandler(Fluid fluid, FluidTank handler) {
        handlerMap.put(fluid, handler);
    }

    @Override
    public int getTanks() {
        return handlerMap.size();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return ((IFluidHandler) handlerMap.values().toArray()[tank]).getFluidInTank(0);
    }

    @Override
    public int getTankCapacity(int tank) {
        return ((IFluidHandler) handlerMap.values().toArray()[tank]).getTankCapacity(0);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return handlerMap.containsKey(stack.getFluid());
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource == FluidStack.EMPTY)
            return 0;
        IFluidHandler handler = handlerMap.get(resource.getFluid());
        if (handler == FluidStack.EMPTY)
            return 0;
        return handler.fill(resource, action);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource == FluidStack.EMPTY)
            return FluidStack.EMPTY;
        IFluidHandler handler = handlerMap.get(resource.getFluid());
        if (handler == null)
            return FluidStack.EMPTY;
        return handler.drain(resource, action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        for (IFluidHandler handler : handlerMap.values()) {
            FluidStack drain = handler.drain(maxDrain, action);
            if (drain != null)
                return drain;
        }
        return FluidStack.EMPTY;
    }

}
