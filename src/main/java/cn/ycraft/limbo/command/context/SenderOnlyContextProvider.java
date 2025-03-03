package cn.ycraft.limbo.command.context;

import com.loohp.limbo.commands.CommandSender;
import dev.rollczi.litecommands.context.ContextProvider;
import dev.rollczi.litecommands.context.ContextResult;
import dev.rollczi.litecommands.invocation.Invocation;

import java.util.function.Supplier;


public class SenderOnlyContextProvider<SENDER> implements ContextProvider<CommandSender, SENDER> {
    private final Class<SENDER> senderClass;
    private final Supplier<Object> errorMessage;

    public SenderOnlyContextProvider(Class<SENDER> senderClass, Supplier<Object> errorMessage) {
        this.senderClass = senderClass;
        this.errorMessage = errorMessage;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ContextResult<SENDER> provide(Invocation<CommandSender> invocation) {
        if (senderClass.isInstance(invocation.sender())) {
            return ContextResult.ok(() -> (SENDER) invocation.sender());
        }

        return ContextResult.error(errorMessage.get());
    }

}
