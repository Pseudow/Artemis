package net.eracube.commons.protocol.others;

import net.eracube.commons.packets.Packet;

public class PacketMessagePlayer implements Packet {
    private final String playerName, message;

    public PacketMessagePlayer() {
        this("", "");
    }

    public PacketMessagePlayer(String playerName, String message) {
        this.playerName = playerName;
        this.message = message;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getMessage() {
        return this.message;
    }
}
