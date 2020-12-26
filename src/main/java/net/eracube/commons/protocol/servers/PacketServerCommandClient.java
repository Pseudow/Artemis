package net.eracube.commons.protocol.servers;

import net.eracube.commons.packets.Packet;
import net.eracube.commons.protocol.CommandType;

public class PacketServerCommandClient implements Packet {
    private final CommandType commandType;

    public PacketServerCommandClient() {
        this(null);
    }

    public PacketServerCommandClient(CommandType commandType) {
        this.commandType = commandType;
    }

    public CommandType getCommandType() {
        return this.commandType;
    }
}
