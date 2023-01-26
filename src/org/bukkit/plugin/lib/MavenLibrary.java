package org.bukkit.plugin.lib;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

public class MavenLibrary
{
    public String group;
    public String name;
    public String version;

    public MavenLibrary() {}

    public MavenLibrary(String artifact)
    {
        String[] parts = artifact.split(":");
        if (parts.length != 3) throw new IllegalArgumentException("Invalid artifact: " + artifact);

        this.group = parts[0];
        this.name = parts[1];
        this.version = parts[2];
    }

    public MavenLibrary(String group, String name, String version)
    {
        this.group = group;
        this.name = name;
        this.version = version;
    }

    public void download()
    {
        try
        {
            URL url = new URL(getRemote());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (getFile().exists()) return; //TODO: hash check?

            try (InputStream inputStream = connection.getInputStream())
            {
                Files.copy(inputStream, getFile().toPath());
                System.out.println("Downloaded maven library: " + getFile().getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName()
    {
        return name + "-" + version + ".jar";
    }

    public File getFile()
    {
        File file = new File("libraries/" + group.replace(".", "/") + "/" + name + "/" + version + "/" + getFileName());
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        return file;
    }

    public String getRemote()
    {
        return String.format("https://repo1.maven.org/maven2/%s/%s/%s/%s-%s.jar", group.replace(".", "/"), name, version, name, version);
    }
}
