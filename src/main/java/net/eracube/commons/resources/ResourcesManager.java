package net.eracube.commons.resources;

import net.eracube.utils.FileUtil;

import java.io.File;
import java.nio.file.FileSystemException;

public class ResourcesManager {
    private final String[] basicFiles = {
            "spigot.yml",
            "bukkit.yml",
            "plugins",
            "run.bat",
            "spigot.jar",
            "eula.txt"
    };
    private final File templateFolder;

    public ResourcesManager(File templateFolder) {
        this.templateFolder = templateFolder;
    }

    public void downloadMap(File minecraftServerFolder, String mapName) {
        try {
            FileUtil.copyFolder(new File(templateFolder.getPath() + "\\" + mapName), minecraftServerFolder);
        } catch(Exception e) {
            try {
                throw new FileSystemException("An error happened when we tried to copy " + mapName + "map!");
            } catch (FileSystemException fileSystemException) {
                fileSystemException.printStackTrace();
            }
        }
    }

    public File downloadServerProperties(File minecraftServerFolder) {
        try {
            FileUtil.copyFolder(new File(templateFolder.getPath() + "\\server.properties"), minecraftServerFolder);
            return new File(minecraftServerFolder.getPath() + "\\server.properties");
        } catch(Exception e) {
            try {
                throw new FileSystemException("An error happened when we tried to copy server.properties!");
            } catch (FileSystemException fileSystemException) {
                fileSystemException.printStackTrace();
            }
        }
        return null;
    }

    public void downloadBasicFiles(File minecraftServerFolder) {
        for (String basicFile : this.basicFiles) {
            try {
                FileUtil.copyFolder(new File(templateFolder.getPath() + "\\" + basicFile), minecraftServerFolder);
            } catch(Exception e) {
                try {
                    throw new FileSystemException("An error happened when we tried to copy server.properties!");
                } catch (FileSystemException fileSystemException) {
                    fileSystemException.printStackTrace();
                }
            }
        }
    }
}
