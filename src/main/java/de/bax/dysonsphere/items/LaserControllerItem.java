package de.bax.dysonsphere.items;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.modify.LocalVariableDiscriminator.Context.Local;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.energy.ItemEnergyHandler;
import de.bax.dysonsphere.capabilities.items.ItemItemStackHandler;
import de.bax.dysonsphere.containers.LaserControllerInventoryContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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
import net.minecraftforge.items.ItemStackHandler;
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
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        var format = NumberFormat.getInstance(Locale.ENGLISH);
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            tooltip.add(Component.translatable("tooltip.dysonsphere.energy_display", format.format(energy.getEnergyStored()), format.format(energy.getMaxEnergyStored())));
        });
        //todo replace with hud gui
        stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((container) -> {
            container.getStackInSlot(0).getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER).ifPresent((pattern) -> {
                tooltip.add(Component.literal("Pattern0: " + pattern.getPattern().getCallInSequence()));    
            });
        });

        Minecraft.getInstance().player.getCapability(DSCapabilities.ORBITAL_LASER).ifPresent((laser) -> {
            tooltip.add(Component.literal("OnCooldown: " + laser.getLasersOnCooldown(Minecraft.getInstance().player.tickCount)));
        });

        tooltip.add(Component.literal("Stacks: " + stack.getOrCreateTagElement("inventory").getAsString()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(!level.isClientSide){
            if(player.isCrouching()){
                //feed itemstack into networkhooks.openscreen additionalData
                NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                    new LaserControllerInventoryContainer(containerId, playerInventory, stack), Component.translatable("container.dysonsphere.laser_controller_inventory")),
                    data -> data.writeItem(stack));
            } else {
                stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
                    stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((inventory) -> {
                        if(energy.getEnergyStored() >= usage){
                            energy.extractEnergy(usage, false);
                            ItemStack patternStack = inventory.getStackInSlot(0);
                            if(!patternStack.isEmpty()){
                                patternStack.getCapability(DSCapabilities.ORBITAL_LASER_PATTERN_CONTAINER).ifPresent((patternContainer) -> {
                                    ItemStack targetDesignator = new ItemStack(ModItems.TARGET_DESIGNATOR.get());
                                    if(hand.equals(InteractionHand.MAIN_HAND)) {
                                        TargetDesignatorItem.setContainedStack(targetDesignator, stack);
                                    } else {
                                        ItemStack mainHandStack = player.getMainHandItem();
                                        TargetDesignatorItem.setContainedStack(targetDesignator, mainHandStack);
                                    }
                                    TargetDesignatorItem.setOrbitalStrikePattern(targetDesignator, patternContainer.getPattern());
                                    player.setItemInHand(InteractionHand.MAIN_HAND, targetDesignator);
                                    
                                    
                                });
                            }
                        }
                    });
                });
            }
            player.getInventory().setChanged();
        }

        return InteractionResultHolder.success(stack);
    }


}
