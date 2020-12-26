package net.eracube.commons.minecraft;

import net.eracube.Artemis;
import net.eracube.commons.protocol.CommandType;
import net.eracube.commons.protocol.servers.PacketServerCommandClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MinecraftManager {
    private final HashMap<String, AtomicInteger> minecraftServersLoading;
    private final MinecraftTemplate template;
    private final Artemis artemis;
    private final File serverFolder;

    private final int serverLoadingTimeout = 60 * 3;

    public MinecraftManager(Artemis artemis, MinecraftTemplate template, File serverFolder) {
        this.serverFolder = serverFolder;
        this.minecraftServersLoading = new HashMap<>();
        this.template = template;
        this.artemis = artemis;

        this.artemis.createVolatile("start-server");

        this.artemis.getScheduledExecutorService().scheduleAtFixedRate(() -> {
            List<String> toRemove = new ArrayList<>();
            this.minecraftServersLoading.forEach((key, value) -> {
                if (value.addAndGet(1) >= serverLoadingTimeout) {
                    toRemove.add(key);
                }
            });
            toRemove.forEach(minecraftServersLoading::remove);
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stopMinecraftServer(String clientUUID) {
        this.artemis.getConnectionManager().sendPacket("artemis-" + clientUUID + "@command",
                new PacketServerCommandClient(CommandType.STOP_SERVER));
    }

    public void startMinecraftServer(String gameType, boolean host) throws IOException {
        synchronized (this.artemis.getVolatile("start-server")) {
            //GET THE FOLDER CONTAINING ALL SERVERS
            File gameFolder = new File(serverFolder.getPath() + "\\" + gameType + "s");

            if (!host) {

                //THIS NUMBER MAY HELP US TO FIND MORE QUICKLY A SERVER NUMBER
                AtomicInteger number = new AtomicInteger(0);

                //CHECK IF A AVAILABLE SERVER IS ALREADY INSIDE THE FOLDER
                for (File file : Objects.requireNonNull(gameFolder.listFiles())) {
                    number.addAndGet(1);
                    if (this.artemis.getServerContainer().getArtemisObjectByServerName(file.getName()) == null) {
                        this.artemis.getResourcesManager().downloadMap(file, gameType);
                        //THIS SERVER FOLDER ISN'T USED, WE CAN USE IT SO
                        File runFile = new File(file.getPath() + "\\run.bat");
                        if (runFile.exists()) {

                            //CHANGE PORT IN SERVER.PROPERTIES
                            File serverProperties = new File(file.getPath() + "\\server.properties");
                            Properties properties = new Properties();
                            properties.load(new FileInputStream(serverProperties));
                            properties.replace("server-port", getUnusedPort());

                            //EXECUTE THE RUN.BAT
                            Runtime.getRuntime().exec("cmd /c run.bat", null, new File(file.getPath()));
                            this.minecraftServersLoading.put(file.getName(), new AtomicInteger(0));
                            return;
                        }
                    }
                }

                System.out.print("CREATING A SERVER FROM A TEMPLATE...");
                getAServerNumberAvailable(gameFolder, number);
                System.out.print("FOUND A NUMBER FOR OUR SERVER: " + number.get());

                File newGameFolder = new File(this.serverFolder.getPath() + "\\" + gameType + number.get());

                if (!newGameFolder.mkdir())
                    throw new IOException("Error, the file you want to convert to a new server is already busy!");

                this.artemis.getResourcesManager().downloadMap(newGameFolder, gameType);
                this.artemis.getResourcesManager().downloadBasicFiles(newGameFolder);

                try {
                    File serverProperties = this.artemis.getResourcesManager().downloadServerProperties(newGameFolder);
                    Properties properties = new Properties();
                    properties.load(new FileInputStream(serverProperties));
                    properties.replace("server-port", getUnusedPort());
                    System.out.println("WE HAVE SET THE NEW PORT IN THE SERVER.PROPERTIES: " + properties.get("server-port"));
                } catch (FileNotFoundException exception) {
                    throw new FileNotFoundException("ERROR THE SERVER.PROPERTIES OF THE SERVER CURRENTLY LOADING ISN'T PRESENT!");
                }

                File runFile = new File(serverFolder.getPath() + "\\run.bat");
                if (runFile.exists()) {
                    System.out.println("RUNNING THE SERVER...");
                    Runtime.getRuntime().exec("cmd /c run.bat", null, new File(serverFolder.getPath()));
                    this.minecraftServersLoading.put(serverFolder.getName(), new AtomicInteger(0));
                }
            }
        }
    }

    private int getUnusedPort() {
        boolean isUsed;
        int i;

        do {
            isUsed = false;
            i = ThreadLocalRandom.current().nextInt(20000, 40000);
            if (i % 2 != 0)
                i++;

            for (ArtemisObject object : this.artemis.getServerContainer().getArtemisObjects())
                if (object.getPort() == i)
                    isUsed = true;
        } while (isUsed);

        return i;
    }

    private void getAServerNumberAvailable(File gameFolder, AtomicInteger number) {
        for (File file : Objects.requireNonNull(gameFolder.listFiles()))
            if (file.getName().contains(Integer.toString(number.get()))) {
                number.addAndGet(1);
                getAServerNumberAvailable(gameFolder, number);
            }
    }

    public Set<String> getMinecraftServersLoading() {
        return this.minecraftServersLoading.keySet();
    }

    public MinecraftTemplate getTemplate() {
        return this.template;
    }
}