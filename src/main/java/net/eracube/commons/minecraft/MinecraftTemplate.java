package net.eracube.commons.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MinecraftTemplate {
    private final JsonObject jsonObject;

    public MinecraftTemplate(JsonElement jsonElement) {
        this.jsonObject = jsonElement.getAsJsonObject();
        jsonObject.entrySet().forEach((entry) -> System.out.println(entry.getKey() + "  -  " + entry.getValue()));
    }

    public String getTemplateId() {
        return jsonObject.get("id").getAsString();
    }

    public int getMaxSlot() {
        return jsonObject.get("max-slot").getAsInt();
    }

    public boolean hasWhitelist() {
        return jsonObject.get("haswhitelist").getAsBoolean();
    }

    public List<String> getWhitelist() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(jsonObject.get("whitelist").getAsJsonArray().iterator(),
                0), false).map(JsonElement::getAsString).distinct().collect(Collectors.toList());
    }

    public List<String> getBlackList() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(jsonObject.get("blacklist").getAsJsonArray().iterator(),
                0), false).map(JsonElement::getAsString).distinct().collect(Collectors.toList());
    }
}
