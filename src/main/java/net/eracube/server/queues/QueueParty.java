package net.eracube.server.queues;

import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class QueueParty implements QueueObject {
    private final String partyLeader, queueId;
    private final Set<String> members;
    private final int ownerPower;

    public QueueParty(String partyLeader, int ownerPower, Set<String> members) {
        this.queueId = UUID.randomUUID().toString() + "-" + new Random().nextInt(9999);
        this.partyLeader = partyLeader;
        this.ownerPower = ownerPower;
        this.members = members;
    }

    @Override
    public String getQueueOwner() {
        return this.partyLeader;
    }

    @Override
    public String getQueueId() {
        return this.queueId;
    }

    @Override
    public int getPower() {
        return this.ownerPower;
    }

    @Override
    public int getQueueSize() {
        return 0;
    }

    public Set<String> getMembers() {
        return this.members;
    }
}