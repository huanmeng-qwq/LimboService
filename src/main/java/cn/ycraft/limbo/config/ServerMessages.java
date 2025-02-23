package cn.ycraft.limbo.config;

import cc.carm.lib.configuration.Configuration;
import cc.carm.lib.configuration.annotation.ConfigPath;
import cn.ycraft.limbo.config.value.ConfiguredMessage;

@ConfigPath(root = true)
public interface ServerMessages extends Configuration {

    static ConfiguredMessage.Builder create() {
        return ConfiguredMessage.create();
    }

    ConfiguredMessage NOT_ALLOWED = create().defaults(
            "&cYou are not allowed to join this limbo!"
    ).build();

    ConfiguredMessage NO_CHAT_PERMISSION = create().defaults(
            "&cYou do not have permission to chat!"
    ).build();


}
