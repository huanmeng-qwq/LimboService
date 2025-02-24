package cn.ycraft.limbo.network;

import cn.ycraft.limbo.network.server.ForwardData;
import com.loohp.limbo.player.Player;
import org.geysermc.mcprotocollib.network.Flag;


public interface NetworkConstants {

    Flag<Player> PLAYER_FLAG = new Flag<>("limbo:player", Player.class);

    Flag<ClientConnection> CLIENT_CONNECTION_FLAG = new Flag<>("limbo:client_connection", ClientConnection.class);

    Flag<ClientSessionPacketHandler> CLIENT_SESSION_PACKET_HANDLER_FLAG = new Flag<>("limbo:packet_handler", ClientSessionPacketHandler.class);

    Flag<ForwardData> FORWARD_FLAG = new Flag<>("limbo:forward_data", ForwardData.class);

}
