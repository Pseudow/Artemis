package net.eracube.user;

import net.eracube.commons.games.GameState;
import net.eracube.user.clients.ClientManager;
import net.eracube.user.hubs.HubManager;
import net.eracube.utils.ResponseWaiter;
import net.eracube.Artemis;
import net.eracube.Configuration;
import net.eracube.commons.exceptions.ServerConnectionRefused;
import net.eracube.commons.exceptions.ServerTimeoutException;
import net.eracube.commons.minecraft.MinecraftManager;
import net.eracube.commons.minecraft.ServerContainer;
import net.eracube.commons.protocol.clients.PacketClientConnectionResponse;
import net.eracube.commons.protocol.users.PacketUserConnect;
import net.eracube.commons.protocol.users.PacketUserDisconnect;
import net.eracube.commons.protocol.users.PacketUserHeartBeat;
import net.eracube.commons.resources.ResourcesManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public abstract class ArtemisUser extends Artemis {
    private final ClientManager clientManager;
    private final HubManager hubManager;
    private final String serverType;

    public ArtemisUser(Configuration configuration, String serverType, String serverName, boolean host, int port, int maxPlayers) {
        super(configuration);
        this.serverType = serverType;

        try {
            this.getConnectionManager().sendPacket("artemis-client@" + serverType, new PacketUserConnect(
                    this.getUUID(), serverName, InetAddress.getLocalHost().getHostAddress(), serverType, host, false, port, maxPlayers));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        PacketClientConnectionResponse response = (PacketClientConnectionResponse) ResponseWaiter.waitResponse(this, "artemis-" + this.getUUID() + "@-response",
                PacketClientConnectionResponse.class, 100, TimeUnit.MILLISECONDS,
                500, new ServerTimeoutException("No connection response received!"));

        if (!response.isAllowed()) {
            throw new ServerConnectionRefused();
        }

        //MANAGERS
        this.clientManager = new ClientManager(this);
        this.hubManager = new HubManager(this);

        //BASIC TASK
        this.getScheduledExecutorService().scheduleAtFixedRate(() ->
                        this.getConnectionManager().sendPacket("artemis-client@" + serverType,
                                new PacketUserHeartBeat(this.getUUID(), this.getGameState().getId(), this.isAvailable(), this.getPlayerCount())),
                0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void disable() {
        this.running = false;
        this.getConnectionManager().sendPacket("artemis-client@" + serverType, new PacketUserDisconnect(this.getUUID()));
        this.getConnectionManager().close();
        getScheduledExecutorService().shutdown();
    }

    public HubManager getHubManager() {
        return this.hubManager;
    }

    public ClientManager getClientManager() {
        return this.clientManager;
    }

    /**
     * @return NULL DONT USE THIS METHOD
     */
    @Override @Deprecated
    public MinecraftManager getMinecraftManager() {
        return null;
    }

    /**
     * @return NULL DONT USE THIS METHOD
     */
    @Override @Deprecated
    public ServerContainer getServerContainer() {
        return null;
    }
    /**
     * @return NULL DONT USE THIS METHOD
     */
    @Override @Deprecated
    public ResourcesManager getResourcesManager() {
        return null;
    }

    public abstract boolean isAvailable();
    public abstract int getPlayerCount();
    public abstract GameState getGameState();
}
