package de.bax.dysonsphere.items.schematics;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.items.ItemItemStackHandler;
import de.bax.dysonsphere.items.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class BluePrintBookItem extends Item{

    public BluePrintBookItem() {
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
                if(cap.equals(ForgeCapabilities.ITEM_HANDLER)){
                    return LazyOptional.of(() -> new ItemItemStackHandler(stack, 80){
                        public boolean isItemValid(int slot, ItemStack stack) {
                            return stack.getCapability(DSCapabilities.SCHEMATIC_CONTAINER).isPresent() || stack.is(ModItems.BLUEPRINT_BOOK.get());
                        };
                        public int getSlotLimit(int slot) {
                            return 1;
                        };
                    }).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack pStack, @Nullable Level pLevel, @Nonnull List<Component> pTooltipComponents, @Nonnull TooltipFlag pIsAdvanced) {
        pStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            for(int i = 0; i < handler.getSlots(); i++){
                ItemStack stack = handler.getStackInSlot(i);
                stack.getCapability(DSCapabilities.SCHEMATIC_CONTAINER).ifPresent(container -> {
                    pTooltipComponents.add(Component.literal(container.getSchematic().getName()));
                });
                stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(container -> {
                    //should be a blueprint book, so...
                    List<Component> tooltips = new ArrayList<>();
                    stack.getItem().appendHoverText(pStack, pLevel, tooltips, pIsAdvanced);
                    pTooltipComponents.add(stack.getDisplayName());
                    tooltips.forEach((tip) -> {
                        pTooltipComponents.add(Component.literal("-").append(tip));
                    });
                });
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level pLevel, @Nonnull Player pPlayer, @Nonnull InteractionHand pUsedHand) {
        //todo open gui
        return super.use(pLevel, pPlayer, pUsedHand);
    }



}
