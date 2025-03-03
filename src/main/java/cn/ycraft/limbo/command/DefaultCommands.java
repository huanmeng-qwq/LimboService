package cn.ycraft.limbo.command;

import cc.carm.lib.easyplugin.utils.ColorParser;
import cn.ycraft.limbo.config.value.ConfiguredMessage;
import com.loohp.limbo.commands.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.UnaryOperator;

public interface DefaultCommands {
    default Void sendMessage(@NotNull CommandSender sender, @NotNull String... messages) {
        return sendMessage(sender, ColorParser::parse, messages);
    }

    default Void sendMessage(@NotNull CommandSender sender, @Nullable UnaryOperator<String> parser,
                             @NotNull String... messages) {
        if (messages == null || messages.length == 0) return null;
        UnaryOperator<String> finalParser = Optional.ofNullable(parser).orElse(UnaryOperator.identity());
        Arrays.stream(messages).map(finalParser).forEach(sender::sendMessage);
        return null;
    }

    default Void sendMessage(@NotNull CommandSender sender, @NotNull ConfiguredMessage text, Object... values) {
        text.sendTo(sender, values);
        return null;
    }
}
