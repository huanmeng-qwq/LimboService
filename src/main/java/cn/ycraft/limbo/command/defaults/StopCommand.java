package cn.ycraft.limbo.command.defaults;

import cn.ycraft.limbo.command.SubCommand;
import com.loohp.limbo.Limbo;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.commands.DefaultCommands;
import org.jetbrains.annotations.NotNull;

public class StopCommand extends SubCommand<DefaultCommands> {

    public StopCommand(@NotNull DefaultCommands parent) {
        super(parent);
    }

    @Override
    public Void execute(CommandSender sender, String[] args) throws Exception {
        Limbo.getInstance().stopServer();
        return null;
    }

    @Override
    public boolean hasPermission(@NotNull CommandSender sender) {
        return sender.hasPermission("limbo.command.stop");
    }

}
