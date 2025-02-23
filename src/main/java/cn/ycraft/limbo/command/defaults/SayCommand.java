package cn.ycraft.limbo.command.defaults;

import cn.ycraft.limbo.command.SimpleCompleter;
import cn.ycraft.limbo.command.SubCommand;
import com.loohp.limbo.Console;
import com.loohp.limbo.Limbo;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.commands.DefaultCommands;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class SayCommand extends SubCommand<DefaultCommands> {

    public SayCommand(@NotNull DefaultCommands parent) {
        super(parent);
    }

    @Override
    public Void execute(CommandSender sender, String[] args) throws Exception {
        if (args.length < 1) return sendMessage(sender, "Usage: /say <messages>");

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if (sender instanceof Console) {
            builder.append("Server");
        } else {
            builder.append(sender.getName());
        }
        builder.append("] ");
        builder.append(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));

        String message = builder.toString();
        Limbo.getInstance().getConsole().sendMessage(message);
        Limbo.getInstance().getPlayers().forEach(each -> each.sendMessage(message));
        return null;
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return SimpleCompleter.players(args[0]);
        } else return SimpleCompleter.none();
    }

    @Override
    public boolean hasPermission(@NotNull CommandSender sender) {
        return sender.hasPermission("limbo.command.say");
    }

}
