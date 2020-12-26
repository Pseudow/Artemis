package net.eracube.user.clients;

import net.eracube.server.clients.Client;
import net.eracube.commons.protocol.clients.PacketClientConnect;
import net.eracube.commons.protocol.clients.PacketClientDisconnect;
import net.eracube.commons.protocol.clients.PacketClientHeartBeat;
import net.eracube.user.ArtemisUser;

import java.util.HashSet;

public class ClientManager {
    private final HashSet<Client> clients;

    public ClientManager(ArtemisUser artemisUser) {
        this.clients = new HashSet<>();

        artemisUser.getPacketManager().addReceiver("artemis-server@client", (packet) -> {
            if (packet instanceof PacketClientConnect) {
                this.registerClient((PacketClientConnect) packet);
            } else if (packet instanceof PacketClientHeartBeat) {
                this.updateClient((PacketClientHeartBeat) packet);
            } else if (packet instanceof PacketClientDisconnect) {
                this.unregisterClient((PacketClientDisconnect) packet);
            }
        });
    }

    public void registerClient(PacketClientConnect packet) {
        Client client = new Client(
                packet.getClientId(),
                packet.getIp(),
                packet.getGameType(),
                new HashSet<>(),
                0
        );

        this.clients.add(client);
        }

    public void unregisterClient(PacketClientDisconnect packet) {
        this.clients.stream().filter(client -> client.getClientId().equals(packet.getClientId())).forEach(this.clients::remove);
    }

    public void updateClient(PacketClientHeartBeat packet) {
        this.clients.stream().filter(client -> client.getClientId().equals(packet.getClientId())).forEach(client -> {
            client.setPlayerCount(packet.getPlayerCount());
            client.setServers(packet.getServers());
        });
    }

    public Client getClientByGameId(String gameId) {
        for (Client client : this.clients)
            if(client.getGameType().equals(gameId))
                return client;
        return null;
    }

    public HashSet<Client> getClients() {
        return this.clients;
    }
}
