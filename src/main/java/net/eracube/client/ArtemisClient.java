package net.eracube.client;

import com.google.gson.JsonParser;
import net.eracube.client.games.GameManager;
import net.eracube.client.queues.QueueManager;
import net.eracube.client.tasks.ClientHeartBeat;
import net.eracube.client.tasks.ServerHeartBeat;
import net.eracube.client.users.UserManager;
import net.eracube.Artemis;
import net.eracube.Configuration;
import net.eracube.commons.exceptions.ServerConnectionRefused;
import net.eracube.commons.exceptions.ServerTimeoutException;
import net.eracube.commons.minecraft.MinecraftManager;
import net.eracube.commons.minecraft.MinecraftTemplate;
import net.eracube.commons.minecraft.ServerContainer;
import net.eracube.commons.protocol.CommandType;
import net.eracube.commons.protocol.clients.PacketClientConnect;
import net.eracube.commons.protocol.clients.PacketClientDisconnect;
import net.eracube.commons.protocol.servers.PacketServerCommandClient;
import net.eracube.commons.protocol.servers.PacketServerConnectionResponse;
import net.eracube.commons.resources.ResourcesManager;
import net.eracube.utils.ResponseWaiter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class ArtemisClient extends Artemis {
    private final MinecraftManager minecraftManager;
    private final ResourcesManager resourcesManager;
    private final QueueManager queueManager;
    private final UserManager userManager;
    private final GameManager gameManager;

    public ArtemisClient(Configuration configuration) throws ServerConnectionRefused, FileNotFoundException {
        super(configuration);

        MinecraftTemplate minecraftTemplate = new MinecraftTemplate(new JsonParser().parse(new InputStreamReader(new FileInputStream("template.json"))));

        //ASK CONNECTION TO THE ARTEMIS SERVER
        try {
            this.getConnectionManager().sendPacket("artemis-server@client", new PacketClientConnect(
                    this.getUUID(), InetAddress.getLocalHost().getHostAddress(),
                    minecraftTemplate.getTemplateId()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            disable();
        }

        //GET THE SERVER'S RESPONSE
        PacketServerConnectionResponse response = (PacketServerConnectionResponse) ResponseWaiter.waitResponse(this, "artemis-" + this.getUUID() + "@-response",
                PacketServerConnectionResponse.class, 100, TimeUnit.MILLISECONDS,
                500, new ServerTimeoutException("No connection response received!"));

        //STOP IF WE ARE NOT ALLOWED
        if (!response.isAllowed()) {
            throw new ServerConnectionRefused();
        }

        //Finally we can run!

        //BASIC TASKS
        getScheduledExecutorService().execute(new ClientHeartBeat(this));
        getScheduledExecutorService().execute(new ServerHeartBeat(this));

        //MANAGERS
        this.resourcesManager = new ResourcesManager(new File("template"));
        this.minecraftManager = new MinecraftManager(this, minecraftTemplate, new File(minecraftTemplate.getTemplateId() + "s"));
        this.queueManager = new QueueManager(this);
        this.userManager = new UserManager(this);
        this.gameManager = new GameManager(this, minecraftTemplate);


        //RECEIVERS
        this.getPacketManager().addReceiver("artemis-" + this.getUUID() + "@command", (packet) -> {
            if (packet instanceof PacketServerCommandClient) {
                PacketServerCommandClient packetServerCommandClient = (PacketServerCommandClient) packet;
                if (packetServerCommandClient.getCommandType().equals(CommandType.STOP_SERVER))
                    disable();
            }
        });
    }

    @Override
    public void disable() {
        this.running = false;
        this.getConnectionManager().sendPacket("artemis-server@client", new PacketClientDisconnect(this.getUUID()));
        this.getConnectionManager().close();
        getScheduledExecutorService().shutdown();
        System.exit(-1);
    }

    public QueueManager getQueueManager() {
        return this.queueManager;
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }

    @Override
    public ServerContainer getServerContainer() {
        return this.userManager;
    }

    @Override
    public ResourcesManager getResourcesManager() {
        return this.resourcesManager;
    }

    @Override
    public MinecraftManager getMinecraftManager() {
        return this.minecraftManager;
    }
}
