package net.eracube.client.queues;

import net.eracube.client.ArtemisClient;
import net.eracube.commons.protocol.queues.PacketQueueAddPlayer;
import net.eracube.commons.protocol.queues.PacketQueuePartyAdd;
import net.eracube.commons.protocol.queues.PacketQueuePartyRemove;
import net.eracube.commons.protocol.queues.PacketQueueRemovePlayer;
import net.eracube.commons.users.User;

import java.util.HashSet;

public class QueueManager {
    private final HashSet<Queue> currentQueues;
    private final ArtemisClient artemisClient;

    public QueueManager(ArtemisClient artemisClient) {
        this.currentQueues = new HashSet<>();
        this.artemisClient = artemisClient;

        this.artemisClient.getPacketManager().addReceiver("artemis-queue@" +
                artemisClient.getMinecraftManager().getTemplate().getTemplateId(), (packet) -> {
            if(packet instanceof PacketQueueAddPlayer) {
                this.addPlayerToQueue((PacketQueueAddPlayer) packet);
            } else if(packet instanceof PacketQueueRemovePlayer) {
                this.removePlayerFromQueue(((PacketQueueRemovePlayer) packet).getPlayerName(), ((PacketQueueRemovePlayer) packet).getServerName());
            } else if(packet instanceof PacketQueuePartyAdd) {
                this.addPartyToQueue((PacketQueuePartyAdd) packet);
            } else if(packet instanceof PacketQueuePartyRemove) {
                this.removePlayerFromQueue(((PacketQueuePartyRemove) packet).getLeaderName(), ((PacketQueuePartyRemove) packet).getServerName());
            }
        });
    }

    public void createQueue(User user) {
        this.currentQueues.add(new Queue(user.getServerName(), artemisClient));
    }

    public void removeQueue(User user) {
        this.currentQueues.removeIf(queue -> queue.getServerName().equals(user.getServerName()));
    }

    private void addPlayerToQueue(PacketQueueAddPlayer packet) {
        this.currentQueues.forEach(queue -> queue.removeObjectInQueue(packet.getServerName()));
        this.currentQueues.stream().filter(queue -> queue.getServerName()
                .equals(packet.getServerName())).forEach(queue -> queue.addPlayerInQueue(packet.getPlayerName(), packet.getPlayerPower()));
    }

    private void addPartyToQueue(PacketQueuePartyAdd packet) {
        this.currentQueues.forEach(queue -> queue.removeObjectInQueue(packet.getServerName()));
        this.currentQueues.stream().filter(queue -> queue.getServerName().equals(packet.getServerName()))
                .forEach(queue -> queue.addPartyInQueue(packet.getLeaderName(), packet.getPlayerPower(), packet.getMembers()));
    }

    private void removePlayerFromQueue(String playerName, String serverName) {
        this.currentQueues.stream().filter(queue -> queue.getServerName().equals(serverName))
                .forEach(queue -> queue.removeObjectInQueue(playerName));
    }
}
