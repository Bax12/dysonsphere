package de.bax.dysonsphere.commands;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import de.bax.dysonsphere.constructs.Construct;
import de.bax.dysonsphere.constructs.ModConstructs;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class DSConstructCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context){
        dispatcher.register(Commands.literal("dysonsphere").requires((req) -> {
            return req.hasPermission(2);//Same as weather, no idea what it means
        })
        .then(Commands.literal("construct")
        .then(Commands.literal("list").executes((command) -> {
            return list(command.getSource());
        }))
        .then(Commands.literal("show").executes((command) -> {
            return showAvailable(command.getSource());
        }))
        .then(Commands.literal("add").then(Commands.argument("Construct", ResourceLocationArgument.id()).suggests(DSConstructCommand::suggestConstructs).executes((command) -> {
            return addConstruct(command.getSource(), getConstruct(ResourceLocationArgument.getId(command, "Construct")));
        })))
        .then(Commands.literal("remove").then(Commands.argument("Construct", ResourceLocationArgument.id()).suggests(DSConstructCommand::suggestConstructs).executes((command) -> {
            return removeConstruct(command.getSource(), getConstruct(ResourceLocationArgument.getId(command, "Construct")));
        })))
        .then(Commands.literal("enable").then(Commands.argument("Construct", ResourceLocationArgument.id()).suggests(DSConstructCommand::suggestConstructs).executes((command) -> {
            return enableConstruct(command.getSource(), getConstruct(ResourceLocationArgument.getId(command, "Construct")));
        })))
        .then(Commands.literal("disable").then(Commands.argument("Construct", ResourceLocationArgument.id()).suggests(DSConstructCommand::suggestConstructs).executes((command) -> {
            return disableConstruct(command.getSource(), getConstruct(ResourceLocationArgument.getId(command, "Construct")));
        })))
        
        ));

    }

    private static Construct getConstruct(ResourceLocation key){
        return ModConstructs.registry().getValue(key);
    }

    private static CompletableFuture<Suggestions> suggestConstructs(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder){
        Collection<String> constructs = ModConstructs.registry().getKeys().stream().map(ResourceLocation::toString).toList();
        return SharedSuggestionProvider.suggest(constructs, builder);
    }

    private static int list(CommandSourceStack source){
        List<Construct> constructsActive = source.getLevel().getCapability(DSCapabilities.DYSON_SPHERE).map((ds) -> {
            return ds.getEnabledConstructs();
        }).orElse(ImmutableList.of());
        List<Construct> constructsInactive = source.getLevel().getCapability(DSCapabilities.DYSON_SPHERE).map((ds) -> {
            return ds.getDisabledConstructs();
        }).orElse(ImmutableList.of());
        if(constructsActive.isEmpty() && constructsInactive.isEmpty()){
            source.sendSuccess(() -> {
                return Component.translatable("commands.dysonsphere.constructs.list_empty");
            }, true);
        } else {
            MutableComponent msg = Component.translatable("commands.dysonsphere.constructs.list");
            constructsActive.forEach((construct) -> {
                msg.append(Component.literal("\n").append(construct.getDisplayName()).append(Component.translatable("commands.dysonsphere.constructs.enabled")));
            });
            constructsInactive.forEach((construct) -> {
                msg.append(Component.literal("\n").append(construct.getDisplayName()).append(Component.translatable("commands.dysonsphere.constructs.disabled")));
            });
            source.sendSuccess(() -> {
                return msg;
            }, true);
        }
        
        
        return constructsActive.size() + constructsInactive.size();
    }

    private static int showAvailable(CommandSourceStack source){
        source.sendSuccess(() -> {
            MutableComponent msg = Component.translatable("commands.dysonsphere.constructs.list");
            ModConstructs.registry().getValues().forEach((con) -> {
                msg.append(Component.literal("\n")).append(con.getDisplayName());
            });
            return msg;
        }, false);
        return 0;
    }

    private static int addConstruct(CommandSourceStack source, Construct construct){
        boolean added = source.getLevel().getCapability(DSCapabilities.DYSON_SPHERE).map((ds) -> {
            return ds.addConstruct(construct);
        }).orElse(false) ;
        if(added){
            source.sendSuccess(() -> {
                return Component.translatable("commands.dysonsphere.constructs.add_success", construct.getDisplayName());
            }, true);
        } else {
            source.sendFailure(Component.translatable("commands.dysonsphere.constructs.add_failure", construct.getDisplayName()));
        }
        return added ? 1 : 0;
    }

    private static int removeConstruct(CommandSourceStack source, Construct construct){
        boolean removed = source.getLevel().getCapability(DSCapabilities.DYSON_SPHERE).map((ds) -> {
            return ds.removeConstruct(construct);
        }).orElse(false) ;
        if(removed){
            source.sendSuccess(() -> {
                return Component.translatable("commands.dysonsphere.constructs.remove_success", construct.getDisplayName());
            }, true);
        } else {
            source.sendFailure(Component.translatable("commands.dysonsphere.constructs.remove_failure", construct.getDisplayName()));
        }
        return removed ? 1 : 0;
    }

    private static int enableConstruct(CommandSourceStack source, Construct construct){
        boolean enabled = source.getLevel().getCapability(DSCapabilities.DYSON_SPHERE).map((ds) -> {
            return ds.enableConstruct(construct);
        }).orElse(false);
        if(enabled){
            source.sendSuccess(() -> {
                return Component.translatable("commands.dysonsphere.constructs.enable_success", construct.getDisplayName());
            }, true);
        } else {
            source.sendFailure(Component.translatable("commands.dysonsphere.constructs.enable_failure", construct.getDisplayName()));
        }
        return enabled ? 1 : 0;
    }

    private static int disableConstruct(CommandSourceStack source, Construct construct){
        boolean disabled = source.getLevel().getCapability(DSCapabilities.DYSON_SPHERE).map((ds) -> {
            return ds.disableConstruct(construct);
        }).orElse(false);
        if(disabled){
            source.sendSuccess(() -> {
                return Component.translatable("commands.dysonsphere.constructs.disable_success", construct.getDisplayName());
            }, true);
        } else {
            source.sendFailure(Component.translatable("commands.dysonsphere.constructs.disable_failure", construct.getDisplayName()));
        }
        return disabled ? 1 : 0;
    }
}
