package net.eracube.user.hubs;

import net.eracube.commons.protocol.hubs.PacketHubConnect;
import net.eracube.commons.protocol.hubs.PacketHubDisconnect;
import net.eracube.commons.protocol.hubs.PacketHubHeartBeat;
import net.eracube.user.ArtemisUser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HubManager {
    private final HashMap<Integer, AtomicInteger> timeouts;
    private final HashSet<Hub> hubs;

    public HubManager(ArtemisUser artemisUser) {
        this.hubs = new HashSet<>();
        this.timeouts = new HashMap<>();

        artemisUser.getPacketManager().addReceiver("net.eracube-hub", (packet) -> {
            if(packet instanceof PacketHubConnect) {
                this.registerHub((PacketHubConnect) packet);
            } else if(packet instanceof PacketHubDisconnect) {
                this.unregisterHub((PacketHubDisconnect) packet);
            } else if(packet instanceof PacketHubHeartBeat) {
                this.updateHub((PacketHubHeartBeat) packet);
            }
        });

        artemisUser.getScheduledExecutorService().scheduleAtFixedRate(() -> {
            Set<Hub> toRemove = new HashSet<>();
            this.timeouts.forEach((hubNumber, timeout) -> {
                if(timeout.addAndGet(1) == 10) {
                    toRemove.add(new Hub(hubNumber));
                }
            });
            toRemove.forEach(hub -> unregisterHub(new PacketHubDisconnect(hub.getHubNumber())));
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void registerHub(PacketHubConnect packet) {
        Hub hub = new Hub(packet.getHubNumber());
        this.hubs.add(hub);
        this.timeouts.put(packet.getHubNumber(), new AtomicInteger(0));
    }

    private void unregisterHub(PacketHubDisconnect packet) {
        for (Hub hub : this.hubs) {
            if(hub.getHubNumber() == packet.getHubNumber()) {
                this.hubs.remove(hub);
                this.timeouts.remove(packet.getHubNumber());
            }
        }
    }

    public void updateHub(PacketHubHeartBeat packet) {
        this.hubs.forEach(hub -> {
            if(hub.getHubNumber() == packet.getHubNumber()) {
                hub.setPlayerCount(packet.getPlayerCount());
                hub.setAccessible(packet.isAccessible());
                hub.setPlayers(packet.getPlayers());
                this.timeouts.get(packet.getHubNumber()).set(0);
            }
        });
    }

    public HashSet<Hub> getHubs() {
        return this.hubs;
    }
}
