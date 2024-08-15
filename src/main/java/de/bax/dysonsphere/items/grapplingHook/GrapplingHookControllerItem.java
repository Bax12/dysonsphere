package de.bax.dysonsphere.items.grapplingHook;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GrapplingHookControllerItem extends Item {
    
    public GrapplingHookControllerItem(){
        super(new Item.Properties());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);

        if(pPlayer.isShiftKeyDown()){
            pPlayer.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
                hookContainer.recallAll();
            });
        } else {
            // spawnHookEntity(itemStack, level, player, 2.5f);
            pPlayer.getCapability(DSCapabilities.GRAPPLING_HOOK_CONTAINER).ifPresent((hookContainer) -> {
                hookContainer.deployHook();
            });
        }

        return InteractionResultHolder.success(itemStack);
    }

}
