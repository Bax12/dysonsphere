package de.bax.dysonsphere.commands;

import de.bax.dysonsphere.DysonSphere;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = DysonSphere.MODID, bus = Bus.FORGE)
public class ModCommands {
    
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event){
        DSComponentCommand.register(event.getDispatcher(), event.getBuildContext());
    }
}
