package de.bax.dysonsphere.compat.waila;

import de.bax.dysonsphere.blocks.DSEnergyReceiverBlock;
import de.bax.dysonsphere.blocks.HeatGeneratorBlock;
import de.bax.dysonsphere.blocks.LaserControllerBlock;
import de.bax.dysonsphere.tileentities.BaseTile;
import de.bax.dysonsphere.tileentities.DSEnergyReceiverTile;
import de.bax.dysonsphere.tileentities.HeatGeneratorTile;
import de.bax.dysonsphere.tileentities.LaserControllerTile;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class DSWailaPlugin implements IWailaPlugin {
    
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(HeatDataProvider.INSTANCE, BaseTile.class);
        registration.registerBlockDataProvider(DSEnergyReceiverDataProvider.INSTANCE, DSEnergyReceiverTile.class);
        registration.registerBlockDataProvider(HeatGeneratorDataProvider.INSTANCE, HeatGeneratorTile.class);
        registration.registerBlockDataProvider(LaserControllerDataProvider.INSTANCE, LaserControllerTile.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(HeatDataProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(DSEnergyReceiverDataProvider.INSTANCE, DSEnergyReceiverBlock.class);
        registration.registerBlockComponent(HeatGeneratorDataProvider.INSTANCE, HeatGeneratorBlock.class);
        registration.registerBlockComponent(LaserControllerDataProvider.INSTANCE, LaserControllerBlock.class);
    }

}
