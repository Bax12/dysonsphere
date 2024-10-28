package de.bax.dysonsphere.items;

import de.bax.dysonsphere.color.ModColors.ITintableBlock;
import de.bax.dysonsphere.color.ModColors.ITintableItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class ColorBlockItem extends BlockItem implements ITintableItem {

    protected final Block block;

    public ColorBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
        this.block = pBlock;
    }

    @Override
    public int getTintColor(ItemStack stack, int tintIndex) {
        if(this.block instanceof ITintableBlock tintableBlock){
            return tintableBlock.getTintColor(block.defaultBlockState(), null, null, tintIndex);
        }
        
        return 0xFFFFFFFF;
    }
    
}
