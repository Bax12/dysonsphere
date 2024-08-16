package de.bax.dysonsphere.items.grapplingHook;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookEngine;
import de.bax.dysonsphere.entities.GrapplingHookEntity;
import de.bax.dysonsphere.fluids.ModFluids;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class GrapplingHookSteamEngineItem extends Item {

    public static int CAPACITY = 20000;
    public static int LAUNCH_USAGE = 100;
    public static int WINCH_USAGE = 10;

    public GrapplingHookSteamEngineItem() {
        super(new Item.Properties());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
                return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if(cap.equals(ForgeCapabilities.FLUID_HANDLER_ITEM)){
                    return LazyOptional.of(() -> new FluidHandlerItemStack(stack, CAPACITY){
                        @Override
                        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
                            return stack.isFluidEqual(new FluidStack(ModFluids.STEAM.get(), 5));
                        }
                        public boolean canFillFluidType(FluidStack fluid) {
                            return isFluidValid(0, fluid);
                        };
                    }).cast();
                }
                if(cap.equals(DSCapabilities.GRAPPLING_HOOK_ENGINE)){
                    return LazyOptional.of(() -> new GrapplingHookSteamEngineWrapper(stack, new FluidHandlerItemStack(stack, CAPACITY))).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public void appendHoverText(ItemStack pStack, @javax.annotation.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(fluid -> {
            pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.fluid_display", fluid.getFluidInTank(0).getDisplayName(), AssetUtil.FLOAT_FORMAT.format(Math.round(fluid.getFluidInTank(0).getAmount())), AssetUtil.FLOAT_FORMAT.format(Math.round(fluid.getTankCapacity(0)))));
        });
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return 0xBCBCBC;
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        return pStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map((fluid) -> {
            return (int) (13f * fluid.getFluidInTank(0).getAmount() / fluid.getTankCapacity(0));
        }).orElse(0);
    }
    
    public static class GrapplingHookSteamEngineWrapper implements IGrapplingHookEngine {

        protected ItemStack containingStack;
        protected IFluidHandlerItem fluidReference;

        public GrapplingHookSteamEngineWrapper(ItemStack stack, IFluidHandlerItem fluidRef){
            this.containingStack = stack;
            this.fluidReference = fluidRef;
        }

        @Override
        public float getDeployForce(Level level, Player player) {
            return 3.0f;
        }

        @Override
        public float getWinchForce(Level level, Player player) {
            return 2.2f;
        }

        @Override
        public void onHookLaunch(Level level, Player player, GrapplingHookEntity hook) {
            fluidReference.drain(LAUNCH_USAGE, FluidAction.EXECUTE);
        }

        @Override
        public void onActiveWinchTick(Level level, Player player) {
            fluidReference.drain(WINCH_USAGE, FluidAction.EXECUTE);
        }

        @Override
        public void onRappelTick(Level level, Player player) {
        }

        @Override
        public boolean canLaunch(Level level, Player player) {
            return fluidReference.getFluidInTank(0).getAmount() >= LAUNCH_USAGE;
        }

        @Override
        public boolean canWinch(Level level, Player player) {
            return fluidReference.getFluidInTank(0).getAmount() >= WINCH_USAGE;
        }

        @Override
        public boolean canRappel(Level level, Player player) {
            return true;
        }

        @Override
        public int getColor() {
            return 0x9b531f;
        }

    }
}
