package de.bax.dysonsphere.compat.curio;

import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class GrapplingHookHarnessCurio implements ICurio {

    protected ItemStack stack;

    public GrapplingHookHarnessCurio(ItemStack stack){
        this.stack = stack;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }
    
}
