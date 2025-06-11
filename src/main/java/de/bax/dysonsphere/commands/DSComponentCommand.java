package de.bax.dysonsphere.commands;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import de.bax.dysonsphere.capabilities.DSCapabilities;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DSComponentCommand {

    //command: dysonsphere
    //subcommand: add, remove, set, list
    //parameter: Item, int
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context){
        //Probably the worst spaghetti i've wrote yet. But vanilla does it just like that.
        dispatcher.register(Commands.literal("dysonsphere").requires((req) -> {
            return req.hasPermission(2);//Same as weather, no idea what it means
        })
        .then(Commands.literal("add").then(Commands.argument("DSPart", ItemArgument.item(context)).executes((command) -> {
            return add(command.getSource(), ItemArgument.getItem(command, "DSPart").getItem(), 1);
        })
        .then(Commands.argument("count", IntegerArgumentType.integer(1)).executes((command) -> {
            return add(command.getSource(), ItemArgument.getItem(command, "DSPart").getItem(), IntegerArgumentType.getInteger(command, "count"));
        }))))
        .then(Commands.literal("remove").then(Commands.argument("DSPart", ItemArgument.item(context)).executes((command) -> {
            return remove(command.getSource(), ItemArgument.getItem(command, "DSPart").getItem(), Integer.MAX_VALUE);
        })
        .then(Commands.argument("count", IntegerArgumentType.integer(1)).executes((command) -> {
            return remove(command.getSource(), ItemArgument.getItem(command, "DSPart").getItem(), IntegerArgumentType.getInteger(command, "count"));
        }))))
        .then(Commands.literal("set").then(Commands.argument("DSPart", ItemArgument.item(context)).then(Commands.argument("count", IntegerArgumentType.integer(0)).executes((command) -> {
            return set(command.getSource(), ItemArgument.getItem(command, "DSPart").getItem(), IntegerArgumentType.getInteger(command, "count"));
        }))))
        .then(Commands.literal("list").executes((command) -> {
            return list(command.getSource(), null);
        }).then(Commands.argument("DSPart", ItemArgument.item(context)).executes((command) -> {
            return list(command.getSource(), ItemArgument.getItem(command, "DSPart").getItem());
        })))
        );
    }

    private static int list(CommandSourceStack source, Item item){
        Map<Item, Integer> parts = source.getLevel().getCapability(DSCapabilities.DYSON_SPHERE).map((dysonSphere) -> {
            return dysonSphere.getDysonSphereParts();
        }).orElse(ImmutableMap.of());
        if(parts.isEmpty()){
            source.sendSuccess(() -> {
                return Component.translatable("commands.dysonsphere.list_empty");
            }, true);
        }
        if(item != null){
            source.sendSuccess(() -> {
                return Component.translatable("commands.dysonsphere.list").append(Component.literal("\n"))
                .append(item.getDefaultInstance().getDisplayName()).append(Component.literal(" : " + parts.getOrDefault(item, 0)));
            }, true);
        } else {
            MutableComponent msg = Component.translatable("commands.dysonsphere.list");
            parts.forEach((part, amount) -> {
                msg.append(Component.literal("\n").append(part.getDefaultInstance().getDisplayName()).append(Component.literal(" : " + amount)));
            });
            source.sendSuccess(() -> {
                return msg;
            }, true);
        }
        

        return parts.size();
    } 

    private static int add(CommandSourceStack source, Item item, int count){
        ItemStack defaultStack = item.getDefaultInstance();
        int added = source.getLevel().getCapability(DSCapabilities.DYSON_SPHERE).map((dysonsphere) -> {
            int i = count;
            while (dysonsphere.addDysonSpherePart(defaultStack, false) && --i > 0); //prevents overfilling and allows counts bigger then maxStackSize
            return count - i;
        }).orElse(0);
        if(added != 0){
            source.sendSuccess(() -> {
                return Component.translatable("commands.dysonsphere.add_success", added, defaultStack.getDisplayName());
            }, true);
        } else {
            source.sendFailure(Component.translatable("commands.dysonsphere.add_failure", defaultStack.getDisplayName()));
        }
        return count;
    }

    private static int remove(CommandSourceStack source, Item item, int count){
        ItemStack defaultStack = item.getDefaultInstance();
        int removed = source.getLevel().getCapability(DSCapabilities.DYSON_SPHERE).map((dysonsphere) -> {
            int i = count;
            while (dysonsphere.removeDysonSpherePart(defaultStack, false) && --i > 0);
            return count - i;
        }).orElse(0);
        if(removed != 0){
            source.sendSuccess(() -> {
                return Component.translatable("commands.dysonsphere.remove_success", removed, defaultStack.getDisplayName());
            }, true);
        } else {
            source.sendFailure(Component.translatable("commands.dysonsphere.remove_failure", defaultStack.getDisplayName()));
        }
        return count;
    }

    private static int set(CommandSourceStack source, Item item, int count){
        ItemStack defaultStack = item.getDefaultInstance();

        //add or remove as needed
        if(source.getLevel().getCapability(DSCapabilities.DYSON_SPHERE).map((dysonsphere) -> {
            int diff = count - dysonsphere.getDysonSpherePartCount(item);
            while(diff > 0 && dysonsphere.addDysonSpherePart(defaultStack, false)){//diff > 0 if we have not enough parts, so add until then
                diff--;
            }
            while(diff < 0 && dysonsphere.removeDysonSpherePart(defaultStack, false)){//diff < 0 if to many parts, remove until at count
                diff++;
            }
            return true;
        }).orElse(false)){
            source.sendSuccess(() -> {
                return Component.translatable("commands.dysonsphere.set_success", defaultStack.getDisplayName(), count);
            }, true);
        } else {
            source.sendFailure(Component.translatable("commands.dysonsphere.set_failure", defaultStack.getDisplayName()));
        }

        return count;
    }
}
