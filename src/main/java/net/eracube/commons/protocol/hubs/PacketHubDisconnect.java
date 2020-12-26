package net.eracube.commons.protocol.hubs;

import net.eracube.commons.packets.Packet;

public class PacketHubDisconnect implements Packet {
    private final int hubNumber;

    public PacketHubDisconnect() {
        this(-1);
    }

    public PacketHubDisconnect(int hubNumber) {
        this.hubNumber = hubNumber;
    }

    public int getHubNumber() {
        return this.hubNumber;
    }
}
