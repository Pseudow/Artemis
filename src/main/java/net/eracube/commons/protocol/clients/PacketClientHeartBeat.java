package net.eracube.commons.protocol.clients;

import net.eracube.commons.packets.Packet;
import net.eracube.commons.users.User;

import java.util.HashSet;

public class PacketClientHeartBeat implements Packet {
    private final String clientId;
    private final HashSet<User> servers;
    private final int playerCount;

    public PacketClientHeartBeat() {
        this("", null, -1);
    }

    public PacketClientHeartBeat(String clientId, HashSet<User> servers, int playerCount) {
        this.clientId = clientId;
        this.servers = servers;
        this.playerCount = playerCount;
    }

    public String getClientId() {
        return this.clientId;
    }

    public HashSet<User> getServers() {
        return this.servers;
    }

    public int getPlayerCount() {
        return this.playerCount;
    }
}
