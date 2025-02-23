package cn.ycraft.limbo.command;

import com.loohp.limbo.commands.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public abstract class SubCommand<C extends CommandHandler> implements NamedExecutor {

    private final @NotNull C parent;

    public SubCommand(@NotNull C parent) {
        this.parent = parent;
    }

    public @NotNull C getParent() {
        return parent;
    }

    public abstract Void execute(CommandSender sender, String[] args) throws Exception;

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
