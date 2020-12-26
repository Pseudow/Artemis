package net.eracube.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class DirectoryUtil {
    public static File getJarFolder() {
        URL url;
        String extURL;

        try {
            url = DirectoryUtil.class.getProtectionDomain().getCodeSource().getLocation();
        } catch (SecurityException ex) {
            url = DirectoryUtil.class.getResource(DirectoryUtil.class.getSimpleName() + ".class");
        }

        extURL = url.toExternalForm();

        if (extURL.endsWith(".jar"))
            extURL = extURL.substring(0, extURL.lastIndexOf("/"));
        else {
            String suffix = "/" + (DirectoryUtil.class.getName()).replace(".", "/") + ".class";
            extURL = extURL.replace(suffix, "");

            if (extURL.startsWith("jar:") && extURL.endsWith(".jar!"))
                extURL = extURL.substring(4, extURL.lastIndexOf("/"));
        }

        try {
            url = new URL(extURL);
        } catch (MalformedURLException ignored) {
        }

        try {
            return new File(url.toURI());
        } catch (URISyntaxException ex) {
            return new File(url.getPath());
        }
    }
}
