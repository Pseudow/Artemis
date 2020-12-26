package net.eracube.commons.protocol.hubs;

import net.eracube.commons.packets.Packet;

import java.util.HashMap;

public class PacketHubHeartBeat implements Packet {
    private final HashMap<String, Integer> players;
    private final int playerCount, hubNumber;
    private final boolean accessible;

    public PacketHubHeartBeat() {
        this(-1, null, -1, false);
    }

    public PacketHubHeartBeat(int hubNumber, HashMap<String, Integer> players, int playerCount, boolean accessible) {
        this.hubNumber = hubNumber;
        this.players = players;
        this.playerCount = playerCount;
        this.accessible = accessible;
    }

    public int getHubNumber() {
        return this.hubNumber;
    }

    public HashMap<String, Integer> getPlayers() {
        return this.players;
    }

    public int getPlayerCount() {
        return this.playerCount;
    }

    public boolean isAccessible() {
        return this.accessible;
    }
}
