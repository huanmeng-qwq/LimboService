package cn.ycraft.limbo.command.defaults;

import cc.carm.lib.easyplugin.utils.ColorParser;
import cn.ycraft.limbo.command.DefaultCommands;
import cn.ycraft.limbo.config.ServerMessages;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.player.Player;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Sender;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import dev.rollczi.litecommands.annotations.permission.Permission;
import dev.rollczi.litecommands.annotations.quoted.Quoted;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@Command(name = "kick")
@Permission("limbo.command.kick")
public class KickCommand implements DefaultCommands {

    @Execute
    public void execute(@Sender CommandSender sender, @Arg("player") Player player, @Quoted @OptionalArg("reason") String reasonRaw) throws Exception {

        Component reason = Component.translatable("multiplayer.disconnect.kicked");
        boolean customReason = false;

        if (reasonRaw != null && !reasonRaw.trim().isEmpty()) {
            reason = LegacyComponentSerializer.legacySection().deserialize(ColorParser.parse(reasonRaw));
            customReason = true;
        }

        player.disconnect(reason);
        if (customReason) {
            ServerMessages.KICK.REASON.sendTo(sender, player.getName(), ColorParser.parse(reasonRaw));
        } else {
            ServerMessages.KICK.PLAYER.sendTo(sender, player.getName());
        }
    }
}
