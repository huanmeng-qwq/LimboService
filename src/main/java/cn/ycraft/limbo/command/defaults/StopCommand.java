package cn.ycraft.limbo.command.defaults;

import cn.ycraft.limbo.command.DefaultCommands;
import com.loohp.limbo.Limbo;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;

@Command(name = "stop")
@Permission("limbo.command.stop")
public class StopCommand implements DefaultCommands {
    @Execute
    public void execute() throws Exception {
        Limbo.getInstance().stopServer();
    }
}
