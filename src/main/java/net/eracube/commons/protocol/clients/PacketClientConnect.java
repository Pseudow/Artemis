package net.eracube.commons.protocol.clients;

import net.eracube.commons.packets.Packet;

public class PacketClientConnect implements Packet {
    private final String clientId, ip, gameType;

    public PacketClientConnect() {
        this("", "", "");
    }

    public PacketClientConnect(String clientId, String ip, String gameType) {
        this.clientId = clientId;
        this.ip = ip;
        this.gameType = gameType;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getIp() {
        return this.ip;
    }

    public String getGameType() {
        return this.gameType;
    }
}
