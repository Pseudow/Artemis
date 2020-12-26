package net.eracube.commons.protocol.queues;

import net.eracube.commons.packets.Packet;

public class PacketQueuePartyRemove implements Packet {
    private final String leaderName, serverName;

    public PacketQueuePartyRemove() {
        this("", "");
    }

    public PacketQueuePartyRemove(String leaderName, String serverName) {
        this.leaderName = leaderName;
        this.serverName = serverName;
    }

    public String getLeaderName() {
        return this.leaderName;
    }

    public String getServerName() {
        return this.serverName;
    }
}
