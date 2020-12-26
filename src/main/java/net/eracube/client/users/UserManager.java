package net.eracube.client.users;

import net.eracube.commons.games.GameState;
import net.eracube.client.ArtemisClient;
import net.eracube.commons.minecraft.ArtemisObject;
import net.eracube.commons.minecraft.ServerContainer;
import net.eracube.commons.protocol.clients.PacketClientConnectionResponse;
import net.eracube.commons.protocol.users.PacketUserConnect;
import net.eracube.commons.protocol.users.PacketUserDisconnect;
import net.eracube.commons.protocol.users.PacketUserHeartBeat;
import net.eracube.commons.users.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class UserManager implements ServerContainer {
    private final HashMap<String, AtomicInteger> timeouts;
    private final ArtemisClient artemisClient;
    private final HashSet<User> users;

    public UserManager(ArtemisClient artemisClient) {
        this.users = new HashSet<>();
        this.artemisClient = artemisClient;
        this.timeouts = new HashMap<>();
        artemisClient.getPacketManager().addReceiver("artemis-client@" +
                artemisClient.getMinecraftManager().getTemplate().getTemplateId(), (packet) -> {
            if(packet instanceof PacketUserConnect) {
                this.registerUser((PacketUserConnect) packet);
            } else if(packet instanceof PacketUserHeartBeat) {
                this.updateUser((PacketUserHeartBeat) packet);
            } else if(packet instanceof PacketUserDisconnect) {
                this.unregisterUser((PacketUserDisconnect) packet);
            }
        });

        artemisClient.getScheduledExecutorService().scheduleAtFixedRate(() -> timeouts.keySet().forEach(clientId -> {
            AtomicInteger lastBeat = timeouts.get(clientId);
            if (lastBeat.addAndGet(1) == 5)
                this.unregisterUser(new PacketUserDisconnect(clientId));
        }), 0, 1, TimeUnit.SECONDS);
    }

    public void registerUser(PacketUserConnect packet) {
        User user = new User(
                packet.getUserId(),
                packet.getServerName(),
                packet.getIp(),
                packet.getGameType(),
                GameState.LOADING,
                packet.isHost(),
                packet.getPort(),
                packet.getMaxPlayers(),
                packet.isAvailable(),
                0
        );
        this.artemisClient.getMinecraftManager().getMinecraftServersLoading().remove(packet.getServerName());

        boolean response = getResponse(user);
        this.artemisClient.getConnectionManager().sendPacket("artemis-" + user.getClientId() + "@-response",
                new PacketClientConnectionResponse(response));
        if (!response) return;

        this.users.add(user);
        this.timeouts.put(packet.getUserId(), new AtomicInteger(0));
        this.artemisClient.getQueueManager().createQueue(user);

        System.out.println("New user connected! (id: " + user.getClientId() + " - gameType: " + user.getGameType() + " - serverName: " + user.getServerName() + " - ip: " + user.getIp() + " - port: " +  user.getPort());
    }

    public void unregisterUser(PacketUserDisconnect packet) {
        this.timeouts.remove(packet.getUserId());
        this.users.stream().filter(client -> client.getClientId().equals(packet.getUserId())).forEach(user -> {
            this.artemisClient.getQueueManager().removeQueue(user);
            this.users.remove(user);
        });
        System.out.println("User removed!");
    }

    public void updateUser(PacketUserHeartBeat packet) {
        this.users.stream().filter(user -> user.getClientId().equals(packet.getUserId())).forEach(user -> {
            user.setAvailable(packet.isAvailable());
            user.setPlayerCount(packet.getPlayerCount());
            user.setGameState(GameState.getGameStateById(packet.getGameState()));
        });
        this.timeouts.get(packet.getUserId()).set(0);
    }

    public boolean getResponse(User user) {
        return true;
    }

    @Override
    public HashSet<ArtemisObject> getArtemisObjects() {
        return new HashSet<>(this.users);
    }

    @Override
    public ArtemisObject getArtemisObjectByServerName(String name) {
        for(User user : users)
            if(user.getServerName().equals(name))
                return user;
        return null;
    }
}