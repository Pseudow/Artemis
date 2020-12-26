package net.eracube.commons.packets;

import net.eracube.Artemis;
import net.eracube.commons.protocol.clients.*;
import net.eracube.commons.protocol.hubs.PacketHubConnect;
import net.eracube.commons.protocol.hubs.PacketHubDisconnect;
import net.eracube.commons.protocol.hubs.PacketHubHeartBeat;
import net.eracube.commons.protocol.others.PacketMessagePlayer;
import net.eracube.commons.protocol.others.PacketTeleportPlayer;
import net.eracube.commons.protocol.queues.PacketQueueAddPlayer;
import net.eracube.commons.protocol.queues.PacketQueuePartyAdd;
import net.eracube.commons.protocol.queues.PacketQueuePartyRemove;
import net.eracube.commons.protocol.queues.PacketQueueRemovePlayer;
import net.eracube.commons.protocol.servers.PacketServerCommandClient;
import net.eracube.commons.protocol.servers.PacketServerConnectionResponse;
import net.eracube.commons.protocol.servers.PacketServerHeartBeat;
import net.eracube.commons.protocol.users.PacketUserConnect;
import net.eracube.commons.protocol.users.PacketUserDisconnect;
import net.eracube.commons.protocol.users.PacketUserHeartBeat;

import java.util.HashMap;
import java.util.HashSet;

public class PacketManager {
        private final HashMap<String, HashSet<PacketReceiver>> receivers;

        private final Artemis artemis;
        private final Packet[] packets;

    /**
     *  This class is inspired by a class create by SamaGames in Hydroangeas project,
     *  a french mini-game closed minecraft server. You can find all classes of
     *  this projet called Hydroangeas is on github:
     *  https://github.com/SamaGames/Hydroangeas
     */
    public PacketManager(Artemis artemis) {
            this.artemis = artemis;
            this.receivers = new HashMap<>();
            this.packets = new Packet[20];

            //USER PACKETS
            this.packets[0] = new PacketUserConnect();
            this.packets[1] = new PacketUserHeartBeat();
            this.packets[2] = new PacketUserDisconnect();

            //CLIENT PACKETS
            this.packets[3] = new PacketClientConnect();
            this.packets[4] = new PacketClientHeartBeat();
            this.packets[5] = new PacketClientDisconnect();
            this.packets[6] = new PacketClientCommandUser();
            this.packets[7] = new PacketClientConnectionResponse();

            //SERVER PACKETS
            this.packets[8] = new PacketServerConnectionResponse();
            this.packets[9] = new PacketServerHeartBeat();
            this.packets[10] = new PacketServerCommandClient();

            //HUB PACKETS
            this.packets[11] = new PacketHubConnect();
            this.packets[12] = new PacketHubHeartBeat();
            this.packets[13] = new PacketHubDisconnect();

            //QUEUE PACKETS
            this.packets[14] = new PacketQueueAddPlayer();
            this.packets[15] = new PacketQueueRemovePlayer();
            this.packets[16] = new PacketQueuePartyAdd();
            this.packets[17] = new PacketQueuePartyRemove();

            //OTHERS PACKETS
            this.packets[18] = new PacketTeleportPlayer();
            this.packets[13] = new PacketMessagePlayer();
        }

        public int getPacketId(Packet packet) {
            for(int i = 0; i < packets.length; i++)
                if(packets[i] != null && packets[i].getClass().equals(packet.getClass()))
                    return i;
            return -1;
        }

        public void receivePacket(String jsonPacket) {
            String[] split = jsonPacket.split(";");
            String tag = split[0];

            if(this.receivers.get(tag) == null || this.receivers.get(tag).size() <= 0) return;

            String id = split[1];

            String information = jsonPacket.substring(tag.length() + id.length() + 2);

            int packetId = Integer.parseInt(id);

            Class<?> packetClass = packets[packetId].getClass();
            Object packet = this.artemis.getGson().fromJson(information, packetClass);

            for(PacketReceiver packetReceiver : this.receivers.get(tag))
                packetReceiver.receive(packet);
        }

        public void addReceiver(String tag, PacketReceiver receiver) {
            HashSet<PacketReceiver> receivers = this.receivers.get(tag);

            if (receivers == null)
                receivers = new HashSet<>();

            receivers.add(receiver);
            this.receivers.put(tag, receivers);
        }

        public void removeReceiver(String tag) {
            this.receivers.remove(tag);
        }

        public void removeReceiver(PacketReceiver receiver) {
            this.receivers.values().forEach(hashSet -> hashSet.forEach(r -> {
                if(r == receiver)
                    hashSet.remove(r);
            }));
        }
}
