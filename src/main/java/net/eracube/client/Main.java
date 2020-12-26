package net.eracube.client;

import net.eracube.Configuration;

public class Main {
    public static void main(String[] args) throws Exception {
        new ArtemisClient(new Configuration("artemis_config.json"));
    }
}
