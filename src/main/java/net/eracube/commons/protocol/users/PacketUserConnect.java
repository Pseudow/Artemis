package net.eracube.commons.protocol.users;

import net.eracube.commons.packets.Packet;

public class PacketUserConnect implements Packet {
    private final String userId, serverName, ip, gameType;
    private final boolean host, available;
    private final int port, maxPlayers;

    public PacketUserConnect() {
        this("", "", "", "", false, false, -1, -1);
    }

    public PacketUserConnect(String userId, String serverName, String ip, String gameType,
                             boolean host, boolean available, int port, int maxPlayers) {
        this.userId = userId;
        this.serverName = serverName;
        this.ip = ip;
        this.gameType = gameType;
        this.host = host;
        this.available = available;
        this.port = port;
        this.maxPlayers = maxPlayers;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getIp() {
        return this.ip;
    }

    public String getGameType() {
        return this.gameType;
    }

    public boolean isHost() {
        return this.host;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public int getPort() {
        return this.port;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }
}
