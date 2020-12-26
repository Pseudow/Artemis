package net.eracube.client.tasks;

import net.eracube.client.ArtemisClient;
import net.eracube.commons.exceptions.ServerTimeoutException;
import net.eracube.commons.protocol.servers.PacketServerHeartBeat;

import java.util.concurrent.TimeUnit;

public class ServerHeartBeat implements Runnable {
    private final ArtemisClient artemisClient;
    private int lastBeat;

    public ServerHeartBeat(ArtemisClient artemisClient) {
        this.artemisClient = artemisClient;
        this.lastBeat = 0;

        artemisClient.getPacketManager().addReceiver("artemis-" + artemisClient.getUUID() + "@heartbeat", (packet)-> {
            if(packet instanceof PacketServerHeartBeat)
                lastBeat = 0;
        });
    }

    @Override
    public void run() {
        while(artemisClient.isRunning()) {
            lastBeat += 1;

            if (lastBeat == 5) {
                try {
                    throw new ServerTimeoutException("Error, the server hasn't sent any heartbeat for 50 seconds!");
                } catch (ServerTimeoutException e) {
                    e.printStackTrace();
                }
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
