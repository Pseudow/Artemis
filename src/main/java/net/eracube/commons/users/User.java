package net.eracube.commons.users;

import net.eracube.commons.games.GameState;
import net.eracube.commons.minecraft.ArtemisObject;

public class User implements ArtemisObject {
    private final String clientId, serverName, ip, gameType;
    private GameState gameState;
    private final boolean host;
    private final int port, maxPlayers;
    private boolean available;
    private int playerCount;

    public User(String clientId, String serverName, String ip, String gameType, GameState gameState,
                boolean host, int port, int maxPlayers, boolean available, int playerCount) {
        this.clientId = clientId;
        this.serverName = serverName;
        this.ip = ip;
        this.gameType = gameType;
        this.gameState = gameState;
        this.host = host;
        this.port = port;
        this.maxPlayers = maxPlayers;
        this.available = available;
        this.playerCount = playerCount;
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

    public GameState getGameState() {
        return this.gameState;
    }

    public boolean isHost() {
        return this.host;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public int getPlayerCount() {
        return this.playerCount;
    }

    @Override
    public String getClientId() {
        return this.clientId;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }
}
