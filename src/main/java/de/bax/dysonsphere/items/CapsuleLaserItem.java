package de.bax.dysonsphere.items;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.dsPart.IDSPart;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class CapsuleLaserItem extends Item {

    public static int energyProvided = -50;
    public static float completionProgress = 0.00001f;

    public CapsuleLaserItem() {
        super(new Item.Properties());
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return cap.equals(DSCapabilities.DS_PART) ? LazyOptional.of(() -> new IDSPart() {
                    @Override
                    public int getEnergyProvided() {
                        return energyProvided;
                    }
                    
                    @Override
                    public float getCompletionProgress() {
                        return completionProgress;
                    }
                }).cast() : LazyOptional.empty();
            }
        };
    }

}
