package net.eracube.user.hubs;

import java.util.HashMap;

public class Hub {
    private HashMap<String, Integer> players;
    private final int hubNumber;
    private int playerCount;
    private boolean accessible;

    public Hub(int hubNumber) {
        this.hubNumber = hubNumber;
        this.players = new HashMap<>();
        this.playerCount = 0;
        this.accessible = false;
    }

    public HashMap<String, Integer> getPlayers() {
        return this.players;
    }

    public int getHubNumber() {
        return this.hubNumber;
    }

    public int getPlayerCount() {
        return this.playerCount;
    }

    public boolean isAccessible() {
        return this.accessible;
    }

    public void setPlayers(HashMap<String, Integer> players) {
        this.players = players;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }
}
