package cn.ycraft.limbo.command.defaults;

import cn.ycraft.limbo.command.SubCommand;
import cn.ycraft.limbo.config.ServerMessages;
import com.loohp.limbo.Limbo;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.commands.DefaultCommands;
import org.jetbrains.annotations.NotNull;

public class VersionCommand extends SubCommand<DefaultCommands> {

    public VersionCommand(@NotNull DefaultCommands parent) {
        super(parent);
    }

    @Override
    public Void execute(CommandSender sender, String[] args) throws Exception {
        return sendMessage(sender, ServerMessages.VERSION, Limbo.LIMBO_IMPLEMENTATION_VERSION, Limbo.SERVER_IMPLEMENTATION_VERSION);
    }

    @Override
    public boolean hasPermission(@NotNull CommandSender sender) {
        return sender.hasPermission("limbo.command.version");
    }
}
