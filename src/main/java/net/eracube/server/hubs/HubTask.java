package net.eracube.server.hubs;

import net.eracube.commons.users.User;
import net.eracube.server.ArtemisServer;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class HubTask implements Runnable {
    private final int IS_CONSIDERED_AS_EMPTY = 5;
    private final ArtemisServer artemisServer;
    private final HubManager hubManager;

    public HubTask(ArtemisServer artemisServer) {
        this.hubManager = artemisServer.getHubManager();
        this.artemisServer = artemisServer;
    }

    @Override
    public void run() {
        if ((hubManager.getCurrentHubs().size() + hubCurrentlyLoading())
                < needNumberOfHub()) {

            try {
                this.artemisServer.getMinecraftManager().startMinecraftServer("hub", false);
            } catch (IOException e) {
                System.out.println("Error, failed to create a new hub!");
            }

        } else if ((hubManager.getCurrentHubs().size() + hubCurrentlyLoading())
                > needNumberOfHub()) {
            for (User hub : hubManager.getCurrentHubs()) {
                if ((hubManager.getCurrentHubs().size() + hubCurrentlyLoading())
                        == needNumberOfHub())
                    break;

                if (hub.getPlayerCount() <= IS_CONSIDERED_AS_EMPTY) {
                    this.artemisServer.getMinecraftManager().stopMinecraftServer(hub.getClientId());
                }
            }
        }
    }

    private int hubCurrentlyLoading() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        this.artemisServer.getMinecraftManager().getMinecraftServersLoading().stream().filter(server ->
                server.contains("hub")).forEach(server -> atomicInteger.addAndGet(1));
        return atomicInteger.get();
    }

    /**
     * This method is a part of Hydroangeas created by SamaGames,
     * a french mini-game closed minecraft server.
     * To find this project called Hydroangeas you can find it on github:
     * https://github.com/SamaGames/Hydroangeas
     */
    private int needNumberOfHub() {
        double v = (((double) hubManager.getSlotUsed()) * 1.1) / (double) hubManager.getMaxSlotOfAHub();
        if (v <= 0.5)
            return 1;

        return (int) Math.ceil(v + 1);
    }
}
