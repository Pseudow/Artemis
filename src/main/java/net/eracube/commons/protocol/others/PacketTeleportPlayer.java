package net.eracube.commons.protocol.others;

import net.eracube.commons.packets.Packet;

public class PacketTeleportPlayer implements Packet {
    private final String playerName, serverName;

    public PacketTeleportPlayer() {
        this("", "");
    }

    public PacketTeleportPlayer(String playerName, String serverName) {
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
