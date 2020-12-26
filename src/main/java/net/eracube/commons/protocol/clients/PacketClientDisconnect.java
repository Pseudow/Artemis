package net.eracube.commons.protocol.clients;

import net.eracube.commons.packets.Packet;

public class PacketClientDisconnect implements Packet {
    private final String clientId;

    public PacketClientDisconnect() {
        this("");
    }

    public PacketClientDisconnect(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return this.clientId;
    }
}
