package de.bax.dysonsphere.items.tools;

import javax.annotation.Nonnull;

import de.bax.dysonsphere.tags.DSTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WrenchItem extends Item{

    public WrenchItem() {
        super(new Item.Properties());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean canAttackBlock(@Nonnull BlockState pState, @Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull Player pPlayer) {
        return false;
    }

    public static boolean isWrench(ItemStack stack){
        return stack.is(DSTags.itemWrench) || stack.is(DSTags.itemTool) || stack.is(DSTags.itemToolWrench);
    }

    
}
