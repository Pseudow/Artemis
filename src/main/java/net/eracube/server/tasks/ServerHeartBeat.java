package net.eracube.server.tasks;

import net.eracube.commons.protocol.servers.PacketServerHeartBeat;
import net.eracube.server.ArtemisServer;

public class ServerHeartBeat implements Runnable {
    private final ArtemisServer artemisServer;

    public ServerHeartBeat(ArtemisServer artemisServer) {
        this.artemisServer = artemisServer;
    }

    @Override
    public void run() {
        this.artemisServer.getClientManager().getClients().forEach(client ->
                this.artemisServer.getConnectionManager().sendPacket("artemis-" + client.getClientId() + "@heartbeat", new PacketServerHeartBeat()
                ));
        this.artemisServer.getServerContainer().getArtemisObjects().forEach(hub ->
                this.artemisServer.getConnectionManager().sendPacket("artemis-" + hub.getClientId() + "@heartbeat", new PacketServerHeartBeat()
                ));
    }
}
