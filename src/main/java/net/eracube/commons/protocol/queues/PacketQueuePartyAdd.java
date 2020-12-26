package net.eracube.commons.protocol.queues;

import net.eracube.commons.packets.Packet;

import java.util.HashSet;
import java.util.Set;

public class PacketQueuePartyAdd implements Packet {
    private final String leaderName, serverName;
    private final Set<String> members;
    private final int playerPower;

    public PacketQueuePartyAdd() {
        this("", "", new HashSet<>(), -1);
    }

    public PacketQueuePartyAdd(String leaderName, String serverName, Set<String> members, int playerPower) {
        this.leaderName = leaderName;
        this.serverName = serverName;
        this.members = members;
        this.playerPower = playerPower;
    }

    public String getLeaderName() {
        return this.leaderName;
    }

    public String getServerName() {
        return this.serverName;
    }

    public Set<String> getMembers() {
        return this.members;
    }

    public int getPlayerPower() {
        return this.playerPower;
    }
}
