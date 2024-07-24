package de.bax.dysonsphere.color;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

/*
 * heavily inspired by me.desht.pneumaticcraft.client.ColorHandlers
 * https://github.com/TeamPneumatic/pnc-repressurized/blob/1.20.1/src/main/java/me/desht/pneumaticcraft/client/ColorHandlers.java
 */

@Mod.EventBusSubscriber(modid = DysonSphere.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModColors {

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        for(RegistryObject<Block> block : ModBlocks.BLOCKS.getEntries()){
            if(block.get() instanceof ITintableBlock tintable){
                event.register(tintable::getTintColor, block.get());
            }
        }
    }

    public static interface ITintableBlock {
        int getTintColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex);
    }

    public static interface ITintableTileBlock extends ITintableBlock {
        @Override
        default int getTintColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
            if(level != null && level.getBlockEntity(pos) instanceof ITintableTile tile){
                return tile.getTintColor(tintIndex);
            }
            return 0xFFFFFFFF;
        }
    }

    public static interface ITintableTile {
        int getTintColor(int tintIndex);
    }
}
