package net.eracube.server.clients;

import net.eracube.commons.minecraft.ArtemisObject;
import net.eracube.commons.users.User;

import java.util.Set;

public class Client implements ArtemisObject {
    private final String clientId, ip, gameType;
    private Set<User> servers;
    private int playerCount;

    public Client(String clientId, String ip, String gameType, Set<User> servers, int playerCount) {
        this.clientId = clientId;
        this.ip = ip;
        this.gameType = gameType;
        this.servers = servers;
        this.playerCount = playerCount;
    }

    @Override
    public String getClientId() {
        return this.clientId;
    }

    @Override
    public int getPort() {
        return -1;
    }

    public String getIp() {
        return this.ip;
    }

    public String getGameType() {
        return this.gameType;
    }

    public Set<User> getServers() {
        return this.servers;
    }

    public int getPlayerCount() {
        return this.playerCount;
    }

    public void setServers(Set<User> servers) {
        this.servers = servers;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }
}
