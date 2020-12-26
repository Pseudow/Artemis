package net.eracube.commons.protocol.queues;

import net.eracube.commons.packets.Packet;

public class PacketQueueRemovePlayer implements Packet {
    private final String playerName, serverName;

    public PacketQueueRemovePlayer() {
        this("", "");
    }

    public PacketQueueRemovePlayer(String playerName, String serverName) {
        this.playerName = playerName;
        this.serverName = serverName;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getServerName() {
        return this.serverName;
    }
}
