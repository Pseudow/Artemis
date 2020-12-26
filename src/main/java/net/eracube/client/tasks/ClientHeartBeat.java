package net.eracube.client.tasks;

import net.eracube.client.ArtemisClient;
import net.eracube.commons.protocol.clients.PacketClientHeartBeat;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class ClientHeartBeat implements Runnable {
    private final ArtemisClient artemisClient;

    public ClientHeartBeat(ArtemisClient artemisClient) {
        this.artemisClient = artemisClient;
    }

    @Override
    public void run() {
        while (artemisClient.isRunning()) {

            this.artemisClient.getConnectionManager().sendPacket("artemis-server@client", new PacketClientHeartBeat(
                    this.artemisClient.getUUID(),
                    new HashSet<>(this.artemisClient.getGameManager().getCurrentServers()),
                    this.artemisClient.getGameManager().getPlayerCountByTemplate()
            ));

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
