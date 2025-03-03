package cn.ycraft.limbo.command.context;

import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.location.Location;
import com.loohp.limbo.player.Player;
import dev.rollczi.litecommands.context.ContextProvider;
import dev.rollczi.litecommands.context.ContextResult;
import dev.rollczi.litecommands.invocation.Invocation;

import java.util.function.Supplier;

public class LocationContext implements ContextProvider<CommandSender, Location> {
    private final Supplier<Object> errorMessage;

    public LocationContext(Supplier<Object> errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public ContextResult<Location> provide(Invocation<CommandSender> invocation) {
        CommandSender sender = invocation.sender();

        if (sender instanceof Player player) {
            return ContextResult.ok(player::getLocation);
        }

        return ContextResult.error(errorMessage.get());
    }

}
