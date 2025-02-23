package cn.ycraft.limbo.command.defaults;

import cn.ycraft.limbo.command.CommandHandler;
import cn.ycraft.limbo.command.SubCommand;
import cn.ycraft.limbo.config.AllowlistConfig;
import cn.ycraft.limbo.config.ServerMessages;
import com.loohp.limbo.Limbo;
import com.loohp.limbo.commands.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.BiConsumer;

public class AllowlistCommands extends CommandHandler {

    public AllowlistCommands() {
        registerSubCommand(new OperateCommand(this, (sender, name) -> {
            AllowlistConfig.BY_NAME.add(name);
            ServerMessages.ALLOWLIST.ADD.sendTo(sender, name);
        }, (sender, uuid) -> {
            AllowlistConfig.BY_UUID.add(uuid);
            ServerMessages.ALLOWLIST.ADD.sendTo(sender, uuid.toString());
        }), "add");
        registerSubCommand(new OperateCommand(this, (sender, name) -> {
            AllowlistConfig.BY_NAME.removeIf(v -> v.equalsIgnoreCase(name));
            ServerMessages.ALLOWLIST.REMOVE.sendTo(sender, name);
        }, (sender, uuid) -> {
            AllowlistConfig.BY_UUID.remove(uuid);
            ServerMessages.ALLOWLIST.REMOVE.sendTo(sender, uuid.toString());
        }), "remove");
        registerSubCommand(new ToggleCommand(this), "toggle");
    }

    @Override
    public Void noArgs(CommandSender sender) {
        sender.sendMessage("Usages:");
        sender.sendMessage("- /allowlist toggle");
        sender.sendMessage("- /allowlist reload");
        sender.sendMessage("- /allowlist add <name/uuid>");
        sender.sendMessage("- /allowlist remove <name/uuid>");
        return null;
    }

    @Override
    public Void noPermission(CommandSender sender) {
        return sendMessage(sender, ServerMessages.NO_PERMISSION);
    }

    @Override
    public boolean hasPermission(@NotNull CommandSender sender) {
        return sender.hasPermission("limbo.command.whitelist");
    }

    static class OperateCommand extends SubCommand<AllowlistCommands> {

        protected final BiConsumer<CommandSender, String> nameConsumer;
        protected final BiConsumer<CommandSender, UUID> uuidConsumer;

        public OperateCommand(AllowlistCommands parent, BiConsumer<CommandSender, String> nameConsumer, BiConsumer<CommandSender, UUID> uuidConsumer) {
            super(parent);
            this.nameConsumer = nameConsumer;
            this.uuidConsumer = uuidConsumer;
        }

        @Override
        public Void execute(CommandSender sender, String[] args) {
            if (args.length != 1) return getParent().noArgs(sender);

            String input = args[0];
            if (input.length() <= 16 && input.length() >= 3) {
                nameConsumer.accept(sender, input);
                return null;
            }

            try {
                UUID uuid = UUID.fromString(input);
                uuidConsumer.accept(sender, uuid);
                return null;
            } catch (IllegalArgumentException e) {
                return sendMessage(sender, "Username should be 3-16 characters long, and UUID should be 36 characters long with '-'.");
            }

        }
    }

    static class ToggleCommand extends SubCommand<AllowlistCommands> {

        public ToggleCommand(AllowlistCommands parent) {
            super(parent);
        }

        @Override
        public Void execute(CommandSender sender, String[] args) {
            boolean current = AllowlistConfig.REVERSED.resolve();
            AllowlistConfig.REVERSED.set(!current);
            if (AllowlistConfig.REVERSED.resolve()) {
                ServerMessages.ALLOWLIST.DENYING.sendTo(sender);
            } else {
                ServerMessages.ALLOWLIST.ALLOWING.sendTo(sender);
            }
            return null;
        }
    }

    static class ReloadCommand extends SubCommand<AllowlistCommands> {

        public ReloadCommand(AllowlistCommands parent) {
            super(parent);
        }

        @Override
        public Void execute(CommandSender sender, String[] args) {
            try {
                Limbo.getInstance().getAllowlistHolder().reload();
            } catch (Exception e) {
            }
            return sendMessage(sender, ServerMessages.ALLOWLIST.RELOADED, AllowlistConfig.size());
        }

    }

}
