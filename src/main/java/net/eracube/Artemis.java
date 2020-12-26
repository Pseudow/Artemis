package net.eracube;

import com.google.gson.Gson;
import net.eracube.commons.minecraft.MinecraftManager;
import net.eracube.commons.minecraft.ServerContainer;
import net.eracube.commons.packets.PacketManager;
import net.eracube.commons.pubsub.ConnectionManager;
import net.eracube.commons.resources.ResourcesManager;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Artemis {
    private final HashMap<String, Volatile> volatiles;

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
        this.uuid = UUID.randomUUID().toString() + "-" + ThreadLocalRandom.current().nextInt(9999);
        this.running = true;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(32);
        this.volatiles = new HashMap<>();
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

    public void createVolatile(String keyId) {
        this.volatiles.put(keyId, new Volatile());
    }

    public Volatile getVolatile(String keyId) {
        return this.volatiles.get(keyId);
    }

    private class Volatile {
        private final Object volatileObject;

        public Volatile() {
            this.volatileObject = UUID.randomUUID().toString() + ";" + ThreadLocalRandom.current().nextInt(9999);
        }

        public Object getVolatileObject() {
            return this.volatileObject;
        }
    }
}
