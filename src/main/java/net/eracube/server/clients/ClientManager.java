package net.eracube.server.clients;

import net.eracube.commons.protocol.clients.PacketClientConnect;
import net.eracube.commons.protocol.clients.PacketClientDisconnect;
import net.eracube.commons.protocol.clients.PacketClientHeartBeat;
import net.eracube.commons.protocol.servers.PacketServerConnectionResponse;
import net.eracube.server.ArtemisServer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientManager {
    private final HashMap<String, AtomicInteger> timeoutClients;
    private final ArtemisServer artemisServer;
    private final HashSet<Client> clients;

    public ClientManager(ArtemisServer artemisServer) {
        this.clients = new HashSet<>();
        this.artemisServer = artemisServer;
        this.timeoutClients = new HashMap<>();

        artemisServer.getPacketManager().addReceiver("artemis-server@client", (packet) -> {
            if (packet instanceof PacketClientConnect) {
                this.registerClient((PacketClientConnect) packet);
            } else if (packet instanceof PacketClientHeartBeat) {
                this.updateClient((PacketClientHeartBeat) packet);
            } else if (packet instanceof PacketClientDisconnect) {
                this.unregisterClient((PacketClientDisconnect) packet);
            }
        });

        artemisServer.getScheduledExecutorService().scheduleAtFixedRate(() -> timeoutClients.keySet().forEach(clientId -> {
            AtomicInteger lastBeat = timeoutClients.get(clientId);

            if (lastBeat.addAndGet(1) == 5)
                this.unregisterClient(new PacketClientDisconnect(clientId));
        }), 0, 1, TimeUnit.SECONDS);
    }

    public void registerClient(PacketClientConnect packet) {
        Client client = new Client(
                packet.getClientId(),
                packet.getIp(),
                packet.getGameType(),
                new HashSet<>(),
                0
        );
        boolean response = getResponse(client);
        this.artemisServer.getConnectionManager().sendPacket("artemis-" + packet.getClientId() + "@response",
                new PacketServerConnectionResponse(getResponse(client)));
        if (!response) {
            this.artemisServer.getConnectionManager().sendPacket("artemis-server@client",
                    new PacketClientDisconnect(client.getClientId()));
            return;
        }

        this.clients.add(client);
        this.timeoutClients.put(packet.getClientId(), new AtomicInteger(0));

        System.out.println("New client connected! (id: " + client.getClientId() + " - gameType: " + client.getGameType() + " - serverName: " + client.getClientId() + " - ip: " + client.getIp() + " - port: " +  client.getPort());
    }

    public void unregisterClient(PacketClientDisconnect packet) {
        this.timeoutClients.remove(packet.getClientId());
        this.clients.stream().filter(client -> client.getClientId().equals(packet.getClientId())).forEach(this.clients::remove);
        System.out.println("Client removed!");
    }

    public void updateClient(PacketClientHeartBeat packet) {
        this.clients.stream().filter(client -> client.getClientId().equals(packet.getClientId())).forEach(client -> {
            client.setPlayerCount(packet.getPlayerCount());
            client.setServers(packet.getServers());
        });
        this.timeoutClients.get(packet.getClientId()).set(0);
    }

    public boolean getResponse(Client client) {
        return true;
    }

    public HashSet<Client> getClients() {
        return this.clients;
    }
}
