package net.eracube.commons.protocol.hubs;

import net.eracube.commons.packets.Packet;

public class PacketHubConnect implements Packet {
    private final int hubNumber;

    public PacketHubConnect() {
        this(-1);
    }

    public PacketHubConnect(int hubNumber) {
        this.hubNumber = hubNumber;
    }

    public int getHubNumber() {
        return this.hubNumber;
    }
}
