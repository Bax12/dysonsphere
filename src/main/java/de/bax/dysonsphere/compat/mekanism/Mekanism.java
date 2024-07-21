package de.bax.dysonsphere.compat.mekanism;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.capabilities.orbitalLaser.ILaserReceiver;
import de.bax.dysonsphere.compat.IModCompat;
import mekanism.api.lasers.ILaserReceptor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Mekanism implements IModCompat {


    public static final Capability<ILaserReceptor> LASER_RECEPTOR = CapabilityManager.get(new CapabilityToken<>(){});
    
    @Override
    public void init(){
        DysonSphere.LOGGER.info("Mekanism Compat Loading");
        MinecraftForge.EVENT_BUS.register(this);
        
    }

    @SubscribeEvent
    public void attachTileCaps(AttachCapabilitiesEvent<BlockEntity> event){
        if(event.getObject() instanceof ILaserReceptor laser){
            DysonSphere.LOGGER.info("foo");
            event.addCapability(new ResourceLocation(DysonSphere.MODID, "mek2ds_laser_receiver"), new Mek2DSLaserReceiver(laser));
            event.addListener(() -> {
                event.getObject().getCapability(DSCapabilities.LASER_RECEIVER).invalidate();
            });
        } else if (event.getObject() instanceof ILaserReceiver laser){
            DysonSphere.LOGGER.info("bar");
            event.addCapability(new ResourceLocation(DysonSphere.MODID, "ds2mek_laser_receiver"), new DS2MekLaserReceiver(laser));
            event.addListener(() -> {
                event.getObject().getCapability(LASER_RECEPTOR).invalidate();
            });
        }
    }
    
}
