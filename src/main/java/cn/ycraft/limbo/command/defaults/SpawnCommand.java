package cn.ycraft.limbo.command.defaults;

import cn.ycraft.limbo.command.DefaultCommands;
import cn.ycraft.limbo.config.ServerConfig;
import cn.ycraft.limbo.config.ServerMessages;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.player.Player;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Sender;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;

@Command(name = "spawn")
@Permission("limbo.command.spawn")
public class SpawnCommand implements DefaultCommands {

    @Execute
    @Permission("limbo.command.spawn.others")
    public void spawn(@Sender CommandSender sender, @Arg("palyer") Player player) throws Exception {
        player.teleport(ServerConfig.WORLD.SPAWNPOINT.resolve());
        ServerMessages.TELEPORT_TO_SPAWN.sendTo(player);
    }

    @Execute
    public void spawn(@Sender Player player) throws Exception {
        player.teleport(ServerConfig.WORLD.SPAWNPOINT.resolve());
        ServerMessages.TELEPORT_TO_SPAWN.sendTo(player);
    }
}
