package cn.ycraft.limbo.command.defaults;

import cn.ycraft.limbo.command.SimpleCompleter;
import cn.ycraft.limbo.command.SubCommand;
import cn.ycraft.limbo.config.ServerMessages;
import com.loohp.limbo.Limbo;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.commands.DefaultCommands;
import com.loohp.limbo.player.Player;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class GameModeCommand extends SubCommand<DefaultCommands> {

    public GameModeCommand(@NotNull DefaultCommands parent) {
        super(parent);
    }

    @Override
    public Void execute(CommandSender sender, String[] args) throws Exception {
        if (args.length < 1) return sendMessage(sender, "Usage: /gamemode <mode> [player]");

        GameMode gameMode = parseGameMode(args[0]);
        if (gameMode == null) return sendMessage(sender, ServerMessages.GAMEMODE.AVAILABLE);

        Player target = null;
        if (args.length > 1 && sender.hasPermission("limbo.command.gamemode.others")) {
            target = Limbo.getInstance().getPlayer(args[1]);
            if (target == null) return sendMessage(sender, ServerMessages.PLAYER_NOT_FOUND);
        } else if (sender instanceof Player) {
            target = (Player) sender;
        }

        if (target == null) return sendMessage(sender, ServerMessages.PLAYER_NOT_SPECIFY);

        target.setGamemode(gameMode);
        if (target != sender) {
            ServerMessages.GAMEMODE.SET_PLAYER.sendTo(sender, target.getName(), gameMode.name());
        } else {
            ServerMessages.GAMEMODE.SET_SELF.sendTo(target, gameMode.name());
        }

        return null;
    }

    @Override
    public boolean hasPermission(@NotNull CommandSender sender) {
        return sender.hasPermission("limbo.command.gamemode");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return SimpleCompleter.objects(args[0], Arrays.stream(GameMode.values()).map(GameMode::name).map(String::toLowerCase));
        } else if (args.length == 2 && sender.hasPermission("limbo.command.gamemode.others")) {
            return SimpleCompleter.players(args[1]);
        } else return SimpleCompleter.none();
    }

    public static @Nullable GameMode parseGameMode(@NotNull String mode) {
        if (mode.equalsIgnoreCase("survival") || mode.equalsIgnoreCase("s") || mode.equalsIgnoreCase("0")) {
            return GameMode.SURVIVAL;
        } else if (mode.equalsIgnoreCase("creative") || mode.equalsIgnoreCase("c") || mode.equalsIgnoreCase("1")) {
            return GameMode.CREATIVE;
        } else if (mode.equalsIgnoreCase("adventure") || mode.equalsIgnoreCase("a") || mode.equalsIgnoreCase("2")) {
            return GameMode.ADVENTURE;
        } else if (mode.equalsIgnoreCase("spectator") || mode.equalsIgnoreCase("sp") | mode.equalsIgnoreCase("3")) {
            return GameMode.SPECTATOR;
        }
        return null;
    }

}
