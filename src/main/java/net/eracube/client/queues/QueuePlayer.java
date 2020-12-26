package net.eracube.client.queues;

import java.util.Random;
import java.util.UUID;

public class QueuePlayer implements QueueObject {
    private final String playerName, queueId;
    private final int power;

    public QueuePlayer(String playerName, int power) {
        this.queueId = UUID.randomUUID().toString() + "-" + new Random().nextInt(9999);
        this.playerName = playerName;
        this.power = power;
    }

    @Override
    public String getQueueOwner() {
        return this.playerName;
    }

    @Override
    public String getQueueId() {
        return this.queueId;
    }

    @Override
    public int getPower() {
        return this.power;
    }

    @Override
    public int getQueueSize() {
        return 1;
    }
}
