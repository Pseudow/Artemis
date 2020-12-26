package net.eracube.commons.exceptions;

public class ServerConnectionRefused extends Error {
    public ServerConnectionRefused() {
        super("Error, the server doesn't accept our connection!");
    }
}
