package net.eracube.commons.protocol.clients;

import net.eracube.commons.packets.Packet;
import net.eracube.commons.protocol.CommandType;

public class PacketClientCommandUser implements Packet {
    private final CommandType commandType;

    public PacketClientCommandUser() {
        this(null);
    }

    public PacketClientCommandUser(CommandType commandType) {
        this.commandType = commandType;
    }

    public CommandType getCommandType() {
        return this.commandType;
    }
}
