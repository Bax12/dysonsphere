package de.bax.dysonsphere.items.grapplingHook;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookEngine;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookFrame;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookHook;
import de.bax.dysonsphere.capabilities.grapplingHook.IGrapplingHookRope;
import de.bax.dysonsphere.capabilities.items.ItemItemStackHandler;
import de.bax.dysonsphere.color.ModColors.ITintableItem;
import de.bax.dysonsphere.containers.GrapplingHookHarnessInventoryContainer;
import de.bax.dysonsphere.util.AssetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;

public class GrapplingHookHarnessItem extends Item implements ITintableItem, Equipable {

    public static final int SLOTS = 3;
    public static final int SLOT_ENGINE = 0;
    public static final int SLOT_ROPE = 1;
    public static final int SLOT_HOOK = 2;

    public GrapplingHookHarnessItem() {
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
                IItemHandler itemHandler = new ItemItemStackHandler(stack, SLOTS){
                    public int getSlotLimit(int slot) {
                        return 1;
                    };

                    public boolean isItemValid(int slot, ItemStack stack) {
                        switch(slot){
                            case SLOT_ENGINE: return stack.getCapability(DSCapabilities.GRAPPLING_HOOK_ENGINE).isPresent();
                            case SLOT_ROPE: return stack.getCapability(DSCapabilities.GRAPPLING_HOOK_ROPE).isPresent();
                            case SLOT_HOOK: return stack.getCapability(DSCapabilities.GRAPPLING_HOOK_HOOK).isPresent();
                        }
                        return false;
                    };
                };
                


                if(cap.equals(DSCapabilities.GRAPPLING_HOOK_FRAME)){
                    return LazyOptional.of(() -> new IGrapplingHookFrame() {

                        @Override
                        public LazyOptional<IGrapplingHookEngine> getEngine() {
                            return itemHandler.getStackInSlot(SLOT_ENGINE).getCapability(DSCapabilities.GRAPPLING_HOOK_ENGINE);
                        }

                        @Override
                        public LazyOptional<IGrapplingHookRope> getRope() {
                            return itemHandler.getStackInSlot(SLOT_ROPE).getCapability(DSCapabilities.GRAPPLING_HOOK_ROPE);
                        }

                        @Override
                        public LazyOptional<IGrapplingHookHook> getHook() {
                            return itemHandler.getStackInSlot(SLOT_HOOK).getCapability(DSCapabilities.GRAPPLING_HOOK_HOOK);
                        }
                        
                    }).cast();
                }
                if(cap.equals(ForgeCapabilities.ITEM_HANDLER)){
                    return LazyOptional.of(() -> itemHandler).cast();
                }

                if(!cap.equals(DSCapabilities.GRAPPLING_HOOK_ENGINE) && !cap.equals(DSCapabilities.GRAPPLING_HOOK_ROPE) && !cap.equals(DSCapabilities.GRAPPLING_HOOK_HOOK)){
                    if(cap.equals(ForgeCapabilities.FLUID_HANDLER_ITEM)){
                        //Fluids have a nice getContainer() method, that replaces our harness with the engine...
                        //So we proxy the call and replace the container with ourself. Could cause a lot of issues but seems to work so far...
                        return itemHandler.getStackInSlot(SLOT_ENGINE).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).lazyMap((internalHandler) -> {
                            return new IFluidHandlerItem(){
                                @NotNull
                                @Override
                                public ItemStack getContainer()
                                {
                                    //reinsert the item to correctly update the harness as we get a new ItemStack
                                    itemHandler.extractItem(SLOT_ENGINE, 64, false);
                                    itemHandler.insertItem(SLOT_ENGINE, internalHandler.getContainer(), false);
                                    return stack;
                                }

                                @Override
                                public int getTanks() {
                                    return internalHandler.getTanks();
                                }

                                @Override
                                public @NotNull FluidStack getFluidInTank(int tank) {
                                    return internalHandler.getFluidInTank(tank);
                                }

                                @Override
                                public int getTankCapacity(int tank) {
                                    return internalHandler.getTankCapacity(tank);
                                }

                                @Override
                                public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
                                    return internalHandler.isFluidValid(tank, stack);
                                }

                                @Override
                                public int fill(FluidStack resource, FluidAction action) {
                                    return internalHandler.fill(resource, action);
                                }

                                @Override
                                public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
                                    return internalHandler.drain(resource, action);
                                }

                                @Override
                                public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
                                    return internalHandler.drain(maxDrain, action);
                                }
                            };
                        }).cast();
                        
                    }
                    //this is how we handle everything not fluid in the engine
                    return itemHandler.getStackInSlot(SLOT_ENGINE).getCapability(cap); //Allow transparent access to engine capabilities. E.g. EnergyStorage for electric engine to allow charging.
                }

                return LazyOptional.empty();
            }
        };
    }

    @Override
    public void appendHoverText(ItemStack pStack, @javax.annotation.Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((itemHandler) -> {
            //TODO: Add Component list
            ItemStack hook = itemHandler.getStackInSlot(SLOT_HOOK);
            ItemStack rope = itemHandler.getStackInSlot(SLOT_ROPE);
            ItemStack engine = itemHandler.getStackInSlot(SLOT_ENGINE);
            Player player = Minecraft.getInstance().player;

            //Add Engine Tooltips, E.g. Energy or Fluid Storage of the engine should be added in their respective items.
            engine.getItem().appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

            if(Screen.hasShiftDown()){
                pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.grappling_hook_hook", hook.getDisplayName()));
                pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.grappling_hook_rope", rope.getDisplayName()));
                pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.grappling_hook_engine", engine.getDisplayName()));
            } else {
                pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.spoiler_components").withStyle(ChatFormatting.AQUA));
            }
            
            if(Screen.hasControlDown()){
                
                float[] ropeMultiplier = rope.getCapability(DSCapabilities.GRAPPLING_HOOK_ROPE).map((gRope) -> {
                    pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.grappling_hook_max_distance", gRope.getMaxDistance(pLevel, player)));
                    float gravity = gRope.getHookGravityMultiplier(pLevel, player);
                    float deploy = gRope.getLaunchForceMultiplier(pLevel, player);
                    float winch = gRope.getWinchForceMultiplier(pLevel, player);

                    return new float[]{gravity, deploy, winch};
                }).orElse(new float[]{1f,1f,1f});
                float gravityMult = ropeMultiplier[0];
                float deployMult = ropeMultiplier[1];
                float winchMult = ropeMultiplier[2];
                
                hook.getCapability(DSCapabilities.GRAPPLING_HOOK_HOOK).ifPresent((gHook) -> {
                    pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.grappling_hook_max_hooks", gHook.getMaxHookCount(pLevel, player)));
                    pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.grappling_hook_gravity", AssetUtil.FLOAT_FORMAT.format(gHook.getGravity(pLevel, player) * gravityMult)));
                });

                engine.getCapability(DSCapabilities.GRAPPLING_HOOK_ENGINE).ifPresent((gEngine) -> {
                    pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.grappling_hook_launch_force", gEngine.getLaunchForce(pLevel, player) * deployMult));
                    pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.grappling_hook_winch_force", gEngine.getWinchForce(pLevel, player) * winchMult));
                });
            } else {
                pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.spoiler_stats").withStyle(ChatFormatting.AQUA));
            }
        });
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if(!level.isClientSide){
            NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider((containerId, playerInventory, playerProvided) -> 
                    new GrapplingHookHarnessInventoryContainer(containerId, playerInventory, itemStack), Component.translatable("container.dysonsphere.grappling_hook_harness_inventory")),
                    data -> data.writeItem(itemStack));
        }
        return InteractionResultHolder.success(itemStack);
    }


    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if(pEntity instanceof Player player){
            pStack.getCapability(DSCapabilities.GRAPPLING_HOOK_FRAME).ifPresent((hook) -> {
                hook.tick(player);
            });
        }
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return pStack.getCapability(ForgeCapabilities.ITEM_HANDLER).map((itemHandler) -> {
            return itemHandler.getStackInSlot(SLOT_ENGINE).isBarVisible();
        }).orElse(false);
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return pStack.getCapability(ForgeCapabilities.ITEM_HANDLER).map((itemHandler) -> {
            return itemHandler.getStackInSlot(SLOT_ENGINE).getBarColor();
        }).orElse(0);
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        return pStack.getCapability(ForgeCapabilities.ITEM_HANDLER).map((itemHandler) -> {
            return itemHandler.getStackInSlot(SLOT_ENGINE).getBarWidth();
        }).orElse(0);
    }

    @Override
    public int getTintColor(ItemStack stack, int tintIndex) {
        switch(tintIndex){
            //drum / engine colors
            case 1:
            case 2:
                return stack.getCapability(DSCapabilities.GRAPPLING_HOOK_FRAME).map((frame) -> {
                    return frame.getEngine().map((engine) -> {
                        return engine.getColor();
                    }).orElse(0xFFFFFF);
                }).orElse(0xFFFFFF);
            //hook color
            case 3:
            return stack.getCapability(DSCapabilities.GRAPPLING_HOOK_FRAME).map((frame) -> {
                return frame.getHook().map((hook) -> {
                    return hook.getColor();
                }).orElse(0xFFFFFF);
            }).orElse(0xFFFFFF);
        }
        return 0xFF00FF;
    }

    public static ClampedItemPropertyFunction getItemPropertiesAllParts() {
        return new ClampedItemPropertyFunction() {
                    @Override
                    public float unclampedCall(ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
                        return pStack.getCapability(DSCapabilities.GRAPPLING_HOOK_FRAME).map((hookFrame) -> {
                            return (hookFrame.getHook().isPresent() && hookFrame.getEngine().isPresent()) ? 1f : 0f;
                        }).orElse(0f);
                    }
                };
    }

    public static ClampedItemPropertyFunction getItemPropertiesHook() {
        return new ClampedItemPropertyFunction() {
                    @Override
                    public float unclampedCall(ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
                        return pStack.getCapability(DSCapabilities.GRAPPLING_HOOK_FRAME).map((hookFrame) -> {
                            return (hookFrame.getHook().isPresent()) ? 1f : 0f;
                        }).orElse(0f);
                    }
                };
    }

    public static ClampedItemPropertyFunction getItemPropertiesEngine() {
        return new ClampedItemPropertyFunction() {
                    @Override
                    public float unclampedCall(ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
                        return pStack.getCapability(DSCapabilities.GRAPPLING_HOOK_FRAME).map((hookFrame) -> {
                            return (hookFrame.getEngine().isPresent()) ? 1f : 0f;
                        }).orElse(0f);
                    }
                };
    }
    
    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.LEGS;
    }

}
