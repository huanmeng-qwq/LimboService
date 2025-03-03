package cn.ycraft.limbo.command.context;

import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.player.Player;
import com.loohp.limbo.world.World;
import dev.rollczi.litecommands.context.ContextProvider;
import dev.rollczi.litecommands.context.ContextResult;
import dev.rollczi.litecommands.invocation.Invocation;

import java.util.function.Supplier;

public class WorldContext implements ContextProvider<CommandSender, World> {
    private final Supplier<Object> errorMessage;

    public WorldContext(Supplier<Object> errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public ContextResult<World> provide(Invocation<CommandSender> invocation) {
        CommandSender sender = invocation.sender();

        if (sender instanceof Player player) {
            return ContextResult.ok(player::getWorld);
        }

        return ContextResult.error(errorMessage.get());
    }

}
