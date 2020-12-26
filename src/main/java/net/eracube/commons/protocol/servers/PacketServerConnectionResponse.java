package net.eracube.commons.protocol.servers;

import net.eracube.commons.packets.Packet;

public class PacketServerConnectionResponse implements Packet {
    private final boolean response;

    public PacketServerConnectionResponse() {
        this(false);
    }

    public PacketServerConnectionResponse(boolean response) {
        this.response = response;
    }

    public boolean isAllowed() {
        return this.response;
    }
}
