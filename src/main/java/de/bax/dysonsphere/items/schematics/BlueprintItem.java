package de.bax.dysonsphere.items.schematics;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.schematic.ItemSchematicContainer;
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

public class BlueprintItem extends Item {

    public BlueprintItem() {
        super(new Item.Properties());
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if(cap.equals(DSCapabilities.SCHEMATIC_CONTAINER)){
                    return LazyOptional.of(() -> new ItemSchematicContainer(stack)).cast(); 
                }
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack pStack, @Nullable Level pLevel, @Nonnull List<Component> pTooltipComponents, @Nonnull TooltipFlag pIsAdvanced) {
        //todo print schematic stats / input component list?
    }

    @Override
    public @Nullable CompoundTag getShareTag(ItemStack stack) {
        // TODO only sync needed data to client, not nested elements.
        return super.getShareTag(stack);
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        // TODO only sync needed data to client, not nested elements.
        super.readShareTag(stack, nbt);
    }

    @Override
    public Component getName(@Nonnull ItemStack pStack) {
        return pStack.getCapability(DSCapabilities.SCHEMATIC_CONTAINER).map((schematic) -> {
            return (Component) Component.literal(schematic.getSchematic().getName());
        }).orElse(super.getName(pStack));
    }
    
}
