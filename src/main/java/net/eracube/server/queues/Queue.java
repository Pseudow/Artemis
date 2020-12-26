package net.eracube.server.queues;

import net.eracube.Artemis;
import net.eracube.commons.protocol.others.PacketTeleportPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Queue {
    private final ArrayList<QueueObject> current;
    private final Artemis artemis;
    private final String serverName;

    public Queue(String serverName, Artemis artemis) {
        this.current = new ArrayList<>();
        this.serverName = serverName;
        this.artemis = artemis;

        // COMPARATOR WITH POWER
        Comparator<? super QueueObject> comparator = (Comparator<QueueObject>) (o1, o2) -> {
            if(o1.getPower() < o2.getPower())
                return 1;
            return 0;
        };

        // LET'S DO A TASK EVERY TWO SECONDS
        artemis.getScheduledExecutorService().scheduleAtFixedRate(() -> {
            if(this.current.size() > 0) {
                // LET'S SORT THE ARRAYLIST
                this.current.sort(comparator);

                // GET THE FIRST THING IN THE QUEUE
                this.teleport(this.current.get(0));
            }
        }, 0, 2, TimeUnit.SECONDS);

        // NO UPDATE THIS IS A HUB
    }

    private void teleport(QueueObject queueObject) {
        if(queueObject instanceof QueuePlayer) {
            QueuePlayer queuePlayer = (QueuePlayer) queueObject;
            this.artemis.getConnectionManager().sendPacket("player-server-sender",
                    new PacketTeleportPlayer(queuePlayer.getQueueOwner(), this.serverName));
        } else if(queueObject instanceof QueueParty) {
            QueueParty queueParty = (QueueParty) queueObject;
            this.artemis.getConnectionManager().sendPacket("player-server-sender",
                    new PacketTeleportPlayer(queueParty.getQueueOwner(), this.serverName));
            queueParty.getMembers().forEach(member ->
                    this.artemis.getConnectionManager().sendPacket("player-server-sender",
                            new PacketTeleportPlayer(member, this.serverName)));
        } else {
            throw new IllegalStateException("ERROR WE HAVE A QUEUE OBJECT WHICH IS IMPLEMENTED BY A UNKNOWN VALUE!");
        }
        this.current.remove(queueObject);
    }

    public void addPlayerInQueue(String playerName, int power) {
        QueuePlayer queuePlayer = new QueuePlayer(playerName, power);
        this.current.add(queuePlayer);
        }

    public void addPartyInQueue(String leaderName, int power, Set<String> members) {
        QueueParty queueParty = new QueueParty(leaderName, power, members);
        this.current.add(queueParty);
        }

    public void removeObjectInQueue(String playerName) {
        this.current.removeIf(queueObject -> queueObject.getQueueOwner().equals(playerName));
    }

    public String getServerName() {
        return this.serverName;
    }
}
