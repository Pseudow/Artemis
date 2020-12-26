package net.eracube.commons.protocol.queues;

import net.eracube.commons.packets.Packet;

public class PacketQueueAddPlayer implements Packet {
    private final String playerName, serverName;
    private final int playerPower;

    public PacketQueueAddPlayer() {
        this("", "", -1);
    }

    public PacketQueueAddPlayer(String playerName, String serverName, int playerPower) {
        this.playerName = playerName;
        this.serverName = serverName;
        this.playerPower = playerPower;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getServerName() {
        return this.serverName;
    }

    public int getPlayerPower() {
        return this.playerPower;
    }
}
