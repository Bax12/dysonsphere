package de.bax.dysonsphere.items.laser;

import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.energy.ItemEnergyHandler;
import de.bax.dysonsphere.capabilities.items.ItemItemStackHandler;
import de.bax.dysonsphere.containers.LaserControllerInventoryContainer;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkHooks;

public class LaserControllerItem extends Item {
    
    public static int capacity = 50000;
    public static int maxInput = 500;
    public static int usage = 100;
    public static final int slots = 6;



    public LaserControllerItem() {
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
                if(cap.equals(ForgeCapabilities.ENERGY)){
                    return LazyOptional.of(() -> new ItemEnergyHandler(stack, capacity, maxInput, Integer.MAX_VALUE)).cast();
                }
                if(cap.equals(ForgeCapabilities.ITEM_HANDLER)){
                    return LazyOptional.of(() -> new ItemItemStackHandler(stack, slots){
                        @Override
                        public int getSlotLimit(int slot) {
                            return 1;
                        }

                        @Override
                        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                            return stack.getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER).map((container) -> {
                                return !container.isEmpty();
                            }).orElse(false);
                        }
                    }).cast();
                }
                return  LazyOptional.empty();
            }
        };
        

    }


    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @javax.annotation.Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            tooltip.add(Component.translatable("tooltip.dysonsphere.energy_display", AssetUtil.FLOAT_FORMAT.format(energy.getEnergyStored()), AssetUtil.FLOAT_FORMAT.format(energy.getMaxEnergyStored())));
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!level.isClientSide){
            NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                new LaserControllerInventoryContainer(containerId, playerInventory, stack), Component.translatable("container.dysonsphere.laser_controller_inventory")),
                data -> data.writeItem(stack));
        }

        return InteractionResultHolder.success(stack);
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
            return 13 * energy.getEnergyStored() / energy.getMaxEnergyStored();
        }).orElse(0);
    }


}
