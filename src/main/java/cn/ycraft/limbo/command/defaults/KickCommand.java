package cn.ycraft.limbo.command.defaults;

import cn.ycraft.limbo.command.SubCommand;
import cn.ycraft.limbo.config.ServerMessages;
import com.loohp.limbo.Limbo;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.commands.DefaultCommands;
import com.loohp.limbo.player.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class KickCommand extends SubCommand<DefaultCommands> {

    public KickCommand(@NotNull DefaultCommands parent) {
        super(parent);
    }

    @Override
    public Void execute(CommandSender sender, String[] args) throws Exception {
        if (args.length < 1) return sendMessage(sender, "Usage: /kick <player> [reason]");

        Player player = Limbo.getInstance().getPlayer(args[0]);
        if (player == null) return sendMessage(sender, ServerMessages.PLAYER_NOT_FOUND);

        String reasonRaw = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Component reason = Component.translatable("multiplayer.disconnect.kicked");
        boolean customReason = false;

        if (!reasonRaw.trim().isEmpty()) {
            reason = LegacyComponentSerializer.legacySection().deserialize(reasonRaw);
            customReason = true;
        }

        player.disconnect(reason);
        if (customReason) {
            ServerMessages.KICK.REASON.sendTo(sender, player.getName(), reasonRaw);
        } else {
            ServerMessages.KICK.PLAYER.sendTo(sender, player.getName());
        }

        return null;
    }

    @Override
    public boolean hasPermission(@NotNull CommandSender sender) {
        return sender.hasPermission("limbo.command.kick");
    }
}
