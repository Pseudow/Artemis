package net.eracube.server;

import com.google.gson.JsonParser;
import net.eracube.server.clients.ClientManager;
import net.eracube.server.hubs.HubManager;
import net.eracube.server.hubs.HubTask;
import net.eracube.server.queues.QueueManager;
import net.eracube.server.tasks.ServerHeartBeat;
import net.eracube.server.users.UserManager;
import net.eracube.Artemis;
import net.eracube.Configuration;
import net.eracube.commons.minecraft.MinecraftManager;
import net.eracube.commons.minecraft.MinecraftTemplate;
import net.eracube.commons.minecraft.ServerContainer;
import net.eracube.commons.protocol.CommandType;
import net.eracube.commons.protocol.servers.PacketServerCommandClient;
import net.eracube.commons.resources.ResourcesManager;
import net.eracube.utils.DirectoryUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class ArtemisServer extends Artemis {
    private final MinecraftManager minecraftManager;
    private final ResourcesManager resourcesManager;
    private final ClientManager clientManager;
    private final QueueManager queueManager;
    private final UserManager userManager;
    private final HubManager hubManager;

    public ArtemisServer(Configuration configuration) throws FileNotFoundException {
        super(configuration);

        //ARTEMIS SERVER FILES
        File currentFile = DirectoryUtil.getJarFolder();
        LinkedList<String> linkedList = new LinkedList<>(Arrays.asList(currentFile.getPath().split("\\\\")));
        linkedList.remove(linkedList.size() - 1);
        linkedList.remove(linkedList.size() - 1);
        StringBuilder stringBuilder = new StringBuilder();
        linkedList.forEach(path -> stringBuilder.append(path).append("\\"));

        //MANAGERS
        this.resourcesManager = new ResourcesManager(new File(stringBuilder.toString() + "\\resources"));
        this.minecraftManager = new MinecraftManager(this,
                new MinecraftTemplate(new JsonParser().parse(new InputStreamReader(new FileInputStream(stringBuilder.toString() + "\\template.json")))),
                new File(stringBuilder.toString() + "\\hubs"));
        this.queueManager = new QueueManager(this);
        this.userManager = new UserManager(this);
        this.clientManager = new ClientManager(this);
        this.hubManager = new HubManager(this);

        //TASKS
        getScheduledExecutorService().scheduleAtFixedRate(new ServerHeartBeat(this), 0, 1, TimeUnit.SECONDS);
        getScheduledExecutorService().execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
                getScheduledExecutorService().scheduleAtFixedRate(new HubTask(this), 0, 5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } });
    }

    @Override
    public void disable() {
        this.running = false;
        this.clientManager.getClients().forEach(client ->
                this.getConnectionManager().sendPacket("artemis-" + client.getClientId() + "@command",
                        new PacketServerCommandClient(CommandType.STOP_SERVER)));
        this.getConnectionManager().close();
        getScheduledExecutorService().shutdown();
    }

    @Override
    public MinecraftManager getMinecraftManager() {
        return this.minecraftManager;
    }

    @Override
    public ServerContainer getServerContainer() {
        return this.userManager;
    }

    @Override
    public ResourcesManager getResourcesManager() {
        return this.resourcesManager;
    }

    public ClientManager getClientManager() {
        return this.clientManager;
    }

    public HubManager getHubManager() {
        return this.hubManager;
    }

    public QueueManager getQueueManager() {
        return this.queueManager;
    }
}
