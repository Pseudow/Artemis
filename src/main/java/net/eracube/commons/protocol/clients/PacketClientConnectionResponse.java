package net.eracube.commons.protocol.clients;

import net.eracube.commons.packets.Packet;

public class PacketClientConnectionResponse implements Packet {
    private final boolean response;

    public PacketClientConnectionResponse() {
        this(false);
    }

    public PacketClientConnectionResponse(boolean response) {
        this.response = response;
    }

    public boolean isAllowed() {
        return this.response;
    }
}
