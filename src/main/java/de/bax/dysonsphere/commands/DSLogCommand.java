package de.bax.dysonsphere.commands;

import java.util.List;

import com.mojang.brigadier.CommandDispatcher;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class DSLogCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context){
        dispatcher.register(Commands.literal("dysonsphere").requires((req) -> {
            return req.hasPermission(2);
        })
        .then(Commands.literal("log").executes((command) -> {
            return printLog(command.getSource());
        })));
    }

    private static int printLog(CommandSourceStack source){
        List<Component> log = source.getLevel().getCapability(DSCapabilities.DYSON_SPHERE).map((ds) -> {
            return ds.getDSLog();
        }).orElse(List.of());
        MutableComponent msg = Component.translatable("commands.dysonsphere.log");
        log.forEach((comp) -> {
            msg.append(Component.literal("\n").append(comp));
        });
        source.sendSuccess(() -> {
            return msg;
        }, true);
        return 0;
    }

}
