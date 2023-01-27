package org.bukkit.plugin.java;

import org.bukkit.Bukkit;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A ClassLoader for plugins, to allow shared classes across multiple plugins
 */
public class PluginClassLoader extends URLClassLoader {
    private final JavaPluginLoader loader;
    private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
    private final ClassLoader classLoader;
    private final ClassLoader libraryLoader;

    public PluginClassLoader(final JavaPluginLoader loader, final URL[] urls, final ClassLoader parent, final ClassLoader libraryLoader) {
        super(urls, parent);

        this.loader = loader;
        this.classLoader = parent;
        this.libraryLoader = libraryLoader;
    }

    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        try
        {
            Thread.currentThread().setContextClassLoader(this);
        } catch (Exception ignored) {}

        try
        {
            Class<?> result = super.loadClass(name, resolve);
            if (result != null) return result;
        } catch (ClassNotFoundException ignored) {}

        try
        {
            Class<?> result = classLoader.loadClass(name);
            if (result != null) return result;
        } catch (ClassNotFoundException ignored) {}

        try
        {
            Class<?> result = ClassLoader.getSystemClassLoader().loadClass(name);
            if (result != null) return result;
        } catch (ClassNotFoundException ignored) {}

        try
        {
            Class<?> result = getClass().getClassLoader().loadClass(name);
            if (result != null) return result;
        } catch (ClassNotFoundException ignored) {}

        try
        {
            Class<?> result = Bukkit.class.getClassLoader().loadClass(name);
            if (result != null) return result;
        } catch (ClassNotFoundException ignored) {}

        if (libraryLoader != null)
        {
            try
            {
                return libraryLoader.loadClass(name);
            } catch (ClassNotFoundException ignored) {}
        }

        Class<?> result = loader.getClassByName(name);
        if (result != null) return result;

        throw new ClassNotFoundException(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    protected Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name);
            }

            if (result == null) {
                result = super.findClass(name);

                if (result != null) {
                    loader.setClass(name, result);
                }
            }

            classes.put(name, result);
        }

        return result;
    }

    public Set<String> getClasses() {
        return classes.keySet();
    }
}
