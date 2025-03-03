package cn.ycraft.limbo.command.defaults;

import cn.ycraft.limbo.command.DefaultCommands;
import cn.ycraft.limbo.config.ServerMessages;
import com.loohp.limbo.Limbo;
import com.loohp.limbo.commands.CommandSender;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Sender;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;

@Permission("limbo.command.version")
@Command(name = "version", aliases = "ver")
public class VersionCommand implements DefaultCommands {
    @Execute
    public void execute(@Sender CommandSender sender) {
        sendMessage(sender, ServerMessages.VERSION, Limbo.LIMBO_IMPLEMENTATION_VERSION, Limbo.SERVER_IMPLEMENTATION_VERSION);
    }
}
