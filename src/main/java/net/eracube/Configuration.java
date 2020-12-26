package net.eracube;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Configuration {
    private final JsonObject jsonObject;
    private final String user, password, host;
    private final int port;

    public Configuration(String filePath) throws Exception {
        File file = new File(filePath);
        jsonObject = new JsonParser().parse(new InputStreamReader(new FileInputStream(file))).getAsJsonObject();

        if(!(jsonObject.has("user") || jsonObject.has("password")
                || jsonObject.has("host") || jsonObject.has("port")
        )) throw new Exception("Error! The file doesn't contain what it should!");

        this.user = jsonObject.get("user").getAsString();
        this.password = jsonObject.get("password").getAsString();
        this.host = jsonObject.get("host").getAsString();
        this.port = jsonObject.get("port").getAsInt();
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public JsonObject getJsonObject() {
        return this.jsonObject;
    }
}
