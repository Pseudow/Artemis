package net.eracube.client.games;

import net.eracube.Artemis;
import net.eracube.commons.games.GameState;
import net.eracube.commons.minecraft.MinecraftTemplate;
import net.eracube.commons.users.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GameManager {
    private final int IS_CONSIDERED_AS_EMPTY = 5;
    private final MinecraftTemplate minecraftTemplate;
    private final Artemis artemis;

    public GameManager(Artemis artemis, MinecraftTemplate minecraftTemplate) {
        this.artemis = artemis;
        this.minecraftTemplate = minecraftTemplate;

        createGameRunnable();
    }

    public User getBestServerWithMaxPlayers(String playerName) {
        while (getPlayerCountByTemplate() == 0) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<User> servers = new ArrayList<>(getCurrentServers());
        List<User> toRemove = new ArrayList<>();
        servers.stream().filter(server -> !server.isAvailable()).forEach(toRemove::add);
        servers.stream().filter(server -> server.getMaxPlayers() == server.getPlayerCount()).forEach(toRemove::add);
        servers.stream().filter(server -> server.getGameState().equals(GameState.STOPPING) || server.getGameState().equals(GameState.LOADING)).forEach(toRemove::add);
        toRemove.forEach(servers::remove);

        servers.sort((client1, client2) -> {
            if (client1.getPlayerCount() > client2.getPlayerCount())
                return 1;
            return 0;
        });
        return servers.get(0);
    }

    private void createGameRunnable() {
        this.artemis.getScheduledExecutorService().scheduleAtFixedRate(() -> {
            if(getPlayerCountByTemplate() < needNumberOfGame()) {
                System.out.println("WE NEED A NEW SERVER: " + minecraftTemplate.getTemplateId());
                try {
                    System.out.println("STARTING IT: " + minecraftTemplate.getTemplateId());
                    this.artemis.getMinecraftManager().startMinecraftServer(minecraftTemplate.getTemplateId(), false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(getPlayerCountByTemplate() > needNumberOfGame()) {
                for (User server : getCurrentServers()) {
                    if ((getPlayerCountByTemplate() + getCurrentlyServerLoading())
                            == needNumberOfGame())
                        break;

                    if (server.getPlayerCount() <= IS_CONSIDERED_AS_EMPTY) {
                        this.artemis.getMinecraftManager().stopMinecraftServer(server.getClientId());
                    }
                }

            }
        }, 0, 300, TimeUnit.MILLISECONDS);
    }

    public int getPlayerCountByTemplate() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        getCurrentServers().forEach(user -> atomicInteger.addAndGet(user.getPlayerCount()));
        return atomicInteger.get();
    }

    public Set<User> getCurrentServers() {
        Set<User> clients = new HashSet<>();
        this.artemis.getServerContainer().getArtemisObjects().forEach(artemisObject -> clients.add((User) artemisObject));
        return clients;
    }

    private int getCurrentlyServerLoading() {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        this.artemis.getMinecraftManager().getMinecraftServersLoading().stream().filter(server ->
                server.contains(minecraftTemplate.getTemplateId())).forEach(server -> atomicInteger.addAndGet(1));
        return atomicInteger.get();
    }

    private int needNumberOfGame() {
        double v = (((double) getPlayerCountByTemplate()) * 1.1) / (double) minecraftTemplate.getMaxSlot();
        if(v <= 1)
            return 1;

        double r = v / 2;
        return (int) Math.ceil(r);
    }
}
