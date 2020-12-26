package net.eracube.server.hubs;

import net.eracube.commons.games.GameState;
import net.eracube.commons.users.User;
import net.eracube.server.ArtemisServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HubManager {
    private final ArtemisServer artemisServer;

    public HubManager(ArtemisServer artemisServer) {
        this.artemisServer = artemisServer;
    }

    public int getSlotUsed() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        getCurrentHubs().forEach(hub -> atomicInteger.set(atomicInteger.get() + hub.getPlayerCount()));
        return atomicInteger.get();
    }

    public int getMaxSlotOfAHub() {
        return artemisServer.getMinecraftManager().getTemplate().getMaxSlot();
    }

    public User getHubWithMinPlayers() {
        synchronized (this.artemisServer.getVolatile("start-server")) {
            if (this.getCurrentHubs().size() == 0) {
                try {
                    this.artemisServer.getMinecraftManager().startMinecraftServer("hub", false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        while(this.getCurrentHubs().size() == 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<User> hubs = new ArrayList<>(getCurrentHubs());
        List<User> toRemove = new ArrayList<>();
        hubs.stream().filter(hub -> !hub.isAvailable()).forEach(toRemove::add);
        hubs.stream().filter(hub -> hub.getGameState().equals(GameState.STOPPING) || hub.getGameState().equals(GameState.LOADING)).forEach(toRemove::add);
        toRemove.forEach(hubs::remove);

        hubs.sort((client1, client2) -> {
            if (client1.getPlayerCount() > client2.getPlayerCount())
                return 1;
            return 0;
        });
        return hubs.get(0);
    }

    public HashSet<User> getCurrentHubs() {
        HashSet<User> users = new HashSet<>();
        artemisServer.getServerContainer().getArtemisObjects().forEach(user -> users.add((User) user));
        return users;
    }
}
