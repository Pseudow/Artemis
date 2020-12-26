package net.eracube.commons.minecraft;

import java.util.HashSet;

public interface ServerContainer {
    HashSet<ArtemisObject> getArtemisObjects();
    ArtemisObject getArtemisObjectByServerName(String name);
}
