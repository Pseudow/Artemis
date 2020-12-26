package net.eracube;

import com.google.gson.Gson;
import net.eracube.commons.minecraft.MinecraftManager;
import net.eracube.commons.minecraft.ServerContainer;
import net.eracube.commons.packets.PacketManager;
import net.eracube.commons.pubsub.ConnectionManager;
import net.eracube.commons.resources.ResourcesManager;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class Artemis {
    private final ScheduledExecutorService scheduledExecutorService;

    private final ConnectionManager connectionManager;
    private final PacketManager packetManager;

    private final String uuid;
    private final Gson gson;
    protected boolean running;

    public Artemis(Configuration configuration) {
        this.running = false;
        this.packetManager = new PacketManager(this);
        this.connectionManager = new ConnectionManager(this, configuration.getUser(),
                configuration.getPassword(), configuration.getHost(), configuration.getPort());
        this.gson = new Gson();
        this.uuid = UUID.randomUUID().toString() + "-" + new Random().nextInt(9999);
        this.running = true;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(32);
    }

    public abstract void disable();

    public abstract MinecraftManager getMinecraftManager();

    public abstract ServerContainer getServerContainer();

    public abstract ResourcesManager getResourcesManager();

    public final ScheduledExecutorService getScheduledExecutorService() {
        return this.scheduledExecutorService;
    }

    public final ConnectionManager getConnectionManager() {
        return this.connectionManager;
    }

    public final PacketManager getPacketManager() {
        return this.packetManager;
    }

    public final Gson getGson() {
        return this.gson;
    }

    public String getUUID() {
        return this.uuid;
    }

    public boolean isRunning() {
        return this.running;
    }
}
