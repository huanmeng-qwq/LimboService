package cn.ycraft.limbo.command;

import com.loohp.limbo.commands.CommandSender;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class InternalCommandRegistry {
    private static final CommandDispatcher<CommandSender> DISPATCHER = new CommandDispatcher<>();

    public static CommandDispatcher<CommandSender> getDispatcher() {
        return DISPATCHER;
    }

    public static int execute(CommandSender sender, String command) throws CommandSyntaxException {
        return DISPATCHER.execute(command, sender);
    }
}
