package cn.ycraft.limbo.command.defaults;

import cn.ycraft.limbo.command.SimpleCompleter;
import cn.ycraft.limbo.command.SubCommand;
import cn.ycraft.limbo.config.ServerConfig;
import cn.ycraft.limbo.config.ServerMessages;
import com.loohp.limbo.Limbo;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.commands.DefaultCommands;
import com.loohp.limbo.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpawnCommand extends SubCommand<DefaultCommands> {

    public SpawnCommand(@NotNull DefaultCommands parent) {
        super(parent);
    }

    @Override
    public Void execute(CommandSender sender, String[] args) throws Exception {
        Player player = null;

        if (args.length == 0 && sender instanceof Player) {
            player = (Player) sender;
        } else if (args.length == 1 && sender.hasPermission("limbo.command.spawn.others")) {
            player = Limbo.getInstance().getPlayer(args[0]);
        }

        if (player == null) {
            if (!sender.hasPermission("limbo.command.spawn.others")) {
                ServerMessages.NO_PERMISSION.sendTo(sender);
                return null;
            }

            ServerMessages.PLAYER_NOT_FOUND.sendTo(sender);
            return null;
        }

        player.teleport(ServerConfig.WORLD.SPAWNPOINT.resolve());
        ServerMessages.TELEPORT_TO_SPAWN.sendTo(player);
        return null;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender.hasPermission("limbo.command.spawn.others")) {
            return SimpleCompleter.players(args[0]);
        } else return SimpleCompleter.none();
    }

    @Override
    public boolean hasPermission(@NotNull CommandSender sender) {
        return sender.hasPermission("limbo.command.spawn");
    }
}
