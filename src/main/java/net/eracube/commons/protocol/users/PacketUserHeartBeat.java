package net.eracube.commons.protocol.users;

import net.eracube.commons.packets.Packet;

public class PacketUserHeartBeat implements Packet {
    private final String userId, gameState;
    private final boolean available;
    private final int playerCount;

    public PacketUserHeartBeat() {
        this("", "", false, -1);
    }

    public PacketUserHeartBeat(String userId, String gameState, boolean available, int playerCount) {
        this.userId = userId;
        this.gameState = gameState;
        this.available = available;
        this.playerCount = playerCount;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getGameState() {
        return this.gameState;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public int getPlayerCount() {
        return this.playerCount;
    }
}
