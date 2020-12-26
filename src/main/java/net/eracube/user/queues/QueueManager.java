package net.eracube.user.queues;

import net.eracube.commons.protocol.queues.PacketQueueAddPlayer;
import net.eracube.commons.protocol.queues.PacketQueuePartyAdd;
import net.eracube.commons.protocol.queues.PacketQueuePartyRemove;
import net.eracube.commons.protocol.queues.PacketQueueRemovePlayer;
import net.eracube.user.ArtemisUser;

import java.util.Set;

public class QueueManager {
    private final ArtemisUser artemisUser;

    public QueueManager(ArtemisUser artemisUser) {
        this.artemisUser = artemisUser;
    }

    public void addPlayerToQueue(String playerName, int playerPower, String serverName) {
        this.artemisUser.getConnectionManager().sendPacket("artemis-client@" + serverName.replaceAll("[0-9]", ""),
                new PacketQueueAddPlayer(playerName, serverName, playerPower));
    }

    public void addPartyToQueue(String leaderName, int leaderPower, Set<String> members, String serverName) {
        this.artemisUser.getConnectionManager().sendPacket("artemis-client@" + serverName.replaceAll("[0-9]", ""),
                new PacketQueuePartyAdd(leaderName, serverName, members, leaderPower));
    }

    public void removePLayerFromQueue(String playerName, String serverName) {
        this.artemisUser.getConnectionManager().sendPacket("artemis-client@" + serverName.replaceAll("[0-9]", ""),
                new PacketQueueRemovePlayer(playerName, serverName));
    }

    public void removePartyFromQueue(String leaderName, String serverName) {
        this.artemisUser.getConnectionManager().sendPacket("artemis-client@" + serverName.replaceAll("[0-9]", ""),
                new PacketQueuePartyRemove(leaderName, serverName));
    }
}
