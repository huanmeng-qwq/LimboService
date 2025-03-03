package cn.ycraft.limbo.command.defaults;

import cn.ycraft.limbo.command.DefaultCommands;
import cn.ycraft.limbo.config.ServerMessages;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.player.Player;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Sender;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;

@Command(name = "gamemode", aliases = "gm")
@Permission("limbo.command.gamemode")
public class GameModeCommand implements DefaultCommands {

    @Execute
    public void gameModeSelf(@Sender Player player, @Arg("gameMode") GameMode gameMode) throws Exception {

        player.setGamemode(gameMode);
        ServerMessages.GAMEMODE.SET_SELF.sendTo(player, gameMode.name());
    }

    @Execute
    @Permission("limbo.command.gamemode.others")
    public void gameModeOthers(@Sender CommandSender sender, @Arg("gameMode") GameMode gameMode, @Arg("target") Player target) throws Exception {
        target.setGamemode(gameMode);
        ServerMessages.GAMEMODE.SET_PLAYER.sendTo(sender, target.getName(), gameMode.name());
    }
}
