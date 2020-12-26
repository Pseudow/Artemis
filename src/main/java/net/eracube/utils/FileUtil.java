package net.eracube.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class FileUtil {
    public static void copyFolder(File source, File destination) {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }

            String[] files = source.list();

            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);

                copyFolder(srcFile, destFile);
            }
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(destination);
                WritableByteChannel targetChannel = fos.getChannel();

                FileInputStream fis = new FileInputStream(source);
                FileChannel inputChannel = fis.getChannel();

                inputChannel.transferTo(0, inputChannel.size(), targetChannel);

                inputChannel.close();
                targetChannel.close();
                fis.close();
                fos.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
