package de.bax.dysonsphere.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.seed.ISeededItem;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class SeededItem extends Item {

    public SeededItem() {
        super(new Item.Properties());
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
                
                return cap.equals(DSCapabilities.SEEDED) ? LazyOptional.of(() -> new ISeededItem() {
                    @Override
                    public ItemStack getSeed() {
                        return SeededItem.getSeed(stack);
                    }

                    @Override
                    public void setSeed(ItemStack seed) {
                        SeededItem.setSeed(stack, seed);
                    }
                }).cast() : LazyOptional.empty();
            }
        };
    }

    public static void setSeed(ItemStack stack, ItemStack seed){
        stack.getOrCreateTag().put("seed", seed.serializeNBT());
    }
    
    public static ItemStack getSeed(ItemStack stack){
        return ItemStack.of(stack.getOrCreateTag().getCompound("seed"));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack pStack, @Nullable Level pLevel, @Nonnull List<Component> pTooltipComponents, @Nonnull TooltipFlag pIsAdvanced) {
        ItemStack seed = getSeed(pStack);
        if(!seed.isEmpty()){
            pTooltipComponents.add(Component.translatable("tooltip.dysonsphere.seeded", seed));
        }
    }
    
}
