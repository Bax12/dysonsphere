package de.bax.dysonsphere.items.grapplingHook;

import java.util.function.Supplier;

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

    public enum TYPE{
        SMART_ALLOY(3, 0.02f, 0x383738);

        public int count;
        public float gravity;
        public int color;

        TYPE(int count, float gravity, int color){
            this.count = count;
            this.gravity = gravity;
            this.color = color;
        }
    }

    protected final TYPE type;

    public GrapplingHookHookItem(int type){
        super(new Item.Properties());

        this.type = TYPE.values()[type];
        
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if(cap.equals(DSCapabilities.GRAPPLING_HOOK_HOOK)){
                    return LazyOptional.of(() -> GrapplingHookHooks.makeSimpleHook(stack, type.count, type.gravity, type.color)).cast();
                }
                return LazyOptional.empty();
            }
        };
    }
}
