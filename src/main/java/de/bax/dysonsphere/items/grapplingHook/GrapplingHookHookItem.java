package de.bax.dysonsphere.items.grapplingHook;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.grapplingHook.GrapplingHookHooks;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class GrapplingHookHookItem extends Item{

    protected final int count;
    protected final float gravity;
    protected final int color;

    public GrapplingHookHookItem(int count, float gravity, int color){
        super(new Item.Properties());
        this.count = count;
        this.gravity = gravity;
        this.color = color;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if(cap.equals(DSCapabilities.GRAPPLING_HOOK_HOOK)){
                    return LazyOptional.of(() -> GrapplingHookHooks.makeSimpleHook(stack, count, gravity, color)).cast();
                }
                return LazyOptional.empty();
            }
        };
    }
}
