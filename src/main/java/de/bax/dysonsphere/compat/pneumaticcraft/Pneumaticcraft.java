package de.bax.dysonsphere.compat.pneumaticcraft;

import de.bax.dysonsphere.DysonSphere;
import de.bax.dysonsphere.capabilities.heat.IHeatTile;
import de.bax.dysonsphere.compat.IModCompat;
import me.desht.pneumaticcraft.api.heat.IHeatExchangerLogic;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Pneumaticcraft implements IModCompat {
    
    public static final Capability<IHeatExchangerLogic> HEAT_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IAirHandlerItem> PRESSURE_ITEM = CapabilityManager.get(new CapabilityToken<>() {});

    @Override
    public void init() {
        DysonSphere.LOGGER.info("Pneumaticcraft Compat Loading");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void attachTileCaps(AttachCapabilitiesEvent<BlockEntity> event){
        /*if(event.getObject() instanceof IHeatExchangingTE heat){
            event.addCapability(new ResourceLocation(DysonSphere.MODID, "pnc2ds_heat_handler"), new PNC2DSHeatHandler(heat));
            event.addListener(() -> {
                event.getObject().getCapability(HEAT_HANDLER).invalidate();
            });
        } else */if(event.getObject() instanceof IHeatTile heat){
            event.addCapability(new ResourceLocation(DysonSphere.MODID, "ds2pnc_heat_handler"), new DS2PNCHeatHandler(heat));
            event.addListener(() -> {
                event.getObject().getCapability(HEAT_HANDLER).invalidate();
            });
        }
    }

}
