package de.bax.dysonsphere.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TargetDesignatorItem extends Item {

    public TargetDesignatorItem() {
        super(new Item.Properties());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
    
    
}
