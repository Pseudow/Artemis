package net.eracube.client.queues;

import net.eracube.Artemis;
import net.eracube.commons.protocol.others.PacketMessagePlayer;
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

        artemis.getScheduledExecutorService().scheduleAtFixedRate(() -> {
            // SEND QUEUE UPDATE TO ALL PLAYERS
            this.current.forEach(queueObject -> {
                artemis.getConnectionManager().sendPacket("player-message", new PacketMessagePlayer(queueObject.getQueueOwner(),
                        "§6[§eArtemisQueue§6] Vous êtes actuellement en §e" + this.current.indexOf(queueObject) + " §6postion sur §e" + this.current.size() + "§6!"));

                if(queueObject instanceof QueueParty) {
                    ((QueueParty) queueObject).getMembers().forEach(member ->
                            artemis.getConnectionManager().sendPacket("player-message", new PacketMessagePlayer(member,
                                    "§6[§eArtemisQueue§6] Vous êtes actuellement en §e" + this.current.indexOf(queueObject) + " §6postion sur §e" + this.current.size() + "§6!")));
                }
            });
        }, 0, 10, TimeUnit.SECONDS);
    }

    private void teleport(QueueObject queueObject) {
        if(queueObject instanceof QueuePlayer) {
            QueuePlayer queuePlayer = (QueuePlayer) queueObject;
            this.artemis.getConnectionManager().sendPacket("artemis-server@bungeecord",
                    new PacketTeleportPlayer(queuePlayer.getQueueOwner(), this.serverName));
        } else if(queueObject instanceof QueueParty) {
            QueueParty queueParty = (QueueParty) queueObject;
            this.artemis.getConnectionManager().sendPacket("artemis-server@bungeecord",
                    new PacketTeleportPlayer(queueParty.getQueueOwner(), this.serverName));
            queueParty.getMembers().forEach(member ->
                    this.artemis.getConnectionManager().sendPacket("artemis-server@bungeecord",
                            new PacketTeleportPlayer(member, this.serverName)));
        } else {
            throw new IllegalStateException("ERROR WE HAVE A QUEUE OBJECT WHICH IS IMPLEMENTED BY A UNKNOWN VALUE!");
        }
        this.current.remove(queueObject);
    }

    public void addPlayerInQueue(String playerName, int power) {
        QueuePlayer queuePlayer = new QueuePlayer(playerName, power);
        this.current.add(queuePlayer);
        this.artemis.getConnectionManager().sendPacket("player-message", new PacketMessagePlayer(playerName,
                "§6[§eArtemisQueue§6] Vous avez rejoint la queue pour le serveur §e" + this.serverName + "§6§!\n " +
                        "§6Vous êtes actuellement en position §e" + this.current.indexOf(queuePlayer) + " §6sur §e" + this.current.size() + " §6!"));
    }

    public void addPartyInQueue(String leaderName, int power, Set<String> members) {
        QueueParty queueParty = new QueueParty(leaderName, power, members);
        this.current.add(queueParty);
        this.artemis.getConnectionManager().sendPacket("player-message", new PacketMessagePlayer(leaderName,
                "§6[§eArtemisQueue§6] Vous avez rejoint la queue pour le serveur §e" + this.serverName + "§6§!\n " +
                        "§6Vous êtes actuellement en position §e" + this.current.indexOf(queueParty) + " §6sur §e" + this.current.size() + " §6!"));
        queueParty.getMembers().forEach(member ->
                this.artemis.getConnectionManager().sendPacket("player-message", new PacketMessagePlayer(member,
                        "§6[§eArtemisQueue§6] Vous avez rejoint la queue pour le serveur §e" + this.serverName + "§6§!\n " +
                                "§6Vous êtes actuellement en position §e" + this.current.indexOf(queueParty) + " §6sur §e" + this.current.size() + " §6!")));
    }

    public void removeObjectInQueue(String playerName) {
        this.current.removeIf(queueObject -> queueObject.getQueueOwner().equals(playerName));
    }

    public String getServerName() {
        return this.serverName;
    }
}
