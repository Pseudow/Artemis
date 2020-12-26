package net.eracube.commons.protocol.users;

import net.eracube.commons.packets.Packet;

public class PacketUserDisconnect implements Packet {
    private final String userId;

    public PacketUserDisconnect() {
        this("");
    }

    public PacketUserDisconnect(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }
}
