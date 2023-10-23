package org.bukkit.plugin.lib;

import org.bukkit.plugin.PluginDescriptionFile;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LibraryLoader
{
    public ClassLoader createLoader(PluginDescriptionFile description) throws MalformedURLException
    {
        if (description.getDependencies().isEmpty()) return null;

        List<URL> urls = new ArrayList<>();
        for (String dependency : description.getDependencies())
        {
            MavenLibrary library = new MavenLibrary(dependency);
            library.download();
            urls.add(library.getFile().toURI().toURL());
        }

        System.out.println("Loaded " + urls.size() + " libraries for plugin: " + description.getName());

        return new URLClassLoader(urls.toArray(new URL[0]), getClass().getClassLoader());
    }
}
