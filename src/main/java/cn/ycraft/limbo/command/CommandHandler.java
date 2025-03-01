package cn.ycraft.limbo.command;

import com.loohp.limbo.commands.CommandExecutor;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.commands.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedReturnValue")
public abstract class CommandHandler implements TabCompleter, NamedExecutor, CommandExecutor {
    protected final @NotNull Map<String, SubCommand<?>> registeredCommands = new HashMap<>();
    protected final @NotNull Map<String, CommandHandler> registeredHandlers = new HashMap<>();

    protected final @NotNull Map<String, String> aliasesMap = new HashMap<>();

    public abstract Void noArgs(CommandSender sender);

    public abstract Void noPermission(CommandSender sender);

    public Void onException(CommandSender sender, String cmd, Exception ex) {
        sender.sendMessage("Error occurred when executing " + cmd + ": " + ex.getLocalizedMessage());
        ex.printStackTrace();
        return null;
    }

    public void registerSubCommand(@NotNull SubCommand<?> command, @NotNull String name, @NotNull String... aliases) {
        this.registeredCommands.put(name.toLowerCase(), command);
        Arrays.stream(aliases).forEach(alias -> this.aliasesMap.put(alias.toLowerCase(), name.toLowerCase()));
    }

    public void registerHandler(@NotNull CommandHandler handler, @NotNull String name, @NotNull String... aliases) {
        this.registeredHandlers.put(name.toLowerCase(), handler);
        Arrays.stream(aliases).forEach(alias -> this.aliasesMap.put(alias.toLowerCase(), name.toLowerCase()));
    }

    public void unregister(@NotNull String command) {
        this.registeredCommands.remove(command);
        this.registeredHandlers.remove(command);
        this.aliasesMap.entrySet().removeIf(entry -> entry.getValue().equalsIgnoreCase(command));
    }

    @Override
    public void execute(@NotNull CommandSender sender, String[] args) {
        if (!this.hasPermission(sender)) {
            noPermission(sender);
            return;
        }

        if (args.length == 0) {
            this.noArgs(sender);
            return;
        }

        String input = args[0].toLowerCase();

        CommandHandler handler = getHandler(input);
        if (handler != null) {
            if (!handler.hasPermission(sender)) {
                this.noPermission(sender);
            } else {
                handler.execute(sender, shortenArgs(args));
            }
            return;
        }

        SubCommand<?> sub = getSubCommand(input);
        if (sub != null) {
            if (!sub.hasPermission(sender)) {
                this.noPermission(sender);
            } else {
                try {
                    sub.execute(sender, shortenArgs(args));
                } catch (Exception ex) {
                    this.onException(sender, input, ex);
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 0) return getExecutors().entrySet().stream()
                .filter(e -> e.getValue().hasPermission(sender))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        String input = args[0].toLowerCase();
        if (args.length == 1) {
            return getExecutors().entrySet().stream()
                    .filter(e -> e.getValue().hasPermission(sender))
                    .map(Map.Entry::getKey)
                    .filter(s -> startsWithIgnoreCase(s, input))
                    .collect(Collectors.toList());
        } else {

            CommandHandler handler = getHandler(input);
            if (handler != null && handler.hasPermission(sender)) {
                return handler.tabComplete(sender, shortenArgs(args));
            }

            SubCommand<?> sub = getSubCommand(input);
            if (sub != null && sub.hasPermission(sender)) {
                return sub.tabComplete(sender, shortenArgs(args));
            }

            return Collections.emptyList();
        }
    }

    public Map<String, NamedExecutor> getExecutors() {
        Map<String, NamedExecutor> executors = new HashMap<>();
        executors.putAll(this.registeredCommands);
        executors.putAll(this.registeredHandlers);
        return executors.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    protected @Nullable CommandHandler getHandler(@NotNull String name) {
        CommandHandler fromName = this.registeredHandlers.get(name);
        if (fromName != null) return fromName;

        String nameFromAlias = this.aliasesMap.get(name);
        if (nameFromAlias == null) return null;
        else return this.registeredHandlers.get(nameFromAlias);
    }

    protected @Nullable SubCommand<?> getSubCommand(@NotNull String name) {
        SubCommand<?> fromName = this.registeredCommands.get(name);
        if (fromName != null) return fromName;

        String nameFromAlias = this.aliasesMap.get(name);
        if (nameFromAlias == null) return null;
        else return this.registeredCommands.get(nameFromAlias);
    }

    protected static String[] shortenArgs(String[] args) {
        if (args.length == 0) {
            return args;
        } else {
            List<String> argList = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
            return argList.toArray(new String[0]);
        }
    }

    protected static boolean startsWithIgnoreCase(@NotNull final String string, @NotNull final String prefix) throws IllegalArgumentException, NullPointerException {
        return string.length() >= prefix.length() && string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

}
