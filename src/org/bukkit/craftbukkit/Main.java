
package org.bukkit.craftbukkit;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static boolean useJline = true;

    // Immutable and pre-defined argument lists to avoid redundant memory allocation.
    private static final List<String> HELP_ARGS = Collections.unmodifiableList(Arrays.asList("?", "help"));
    private static final List<String> CONFIG_ARGS = Collections.unmodifiableList(Arrays.asList("c", "config"));
    private static final File DEFAULT_CONFIG = new File("server.properties");
    private static final File DEFAULT_PLUGIN_DIR = new File("plugins");

    public static void main(String[] args) {
        OptionParser parser = createOptionParser();

        OptionSet options;
        try {
            options = parser.parse(args);
            if (options.has("?")) {
                parser.printHelpOn(System.out);
                return;
            }
        } catch (joptsimple.OptionException | IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.WARNING, ex.getLocalizedMessage());
            return;  // Exit on error to avoid unnecessary operations.
        }

        boolean showVersion = options.has("v");
        if (showVersion) {
            System.out.println(CraftServer.class.getPackage().getImplementationVersion());
            return;
        }

        try {
            useJline = !"jline.UnsupportedTerminal".equals(System.getProperty("jline.terminal"));

            boolean noJLine = options.has("nojline");
            if (noJLine) {
                Properties props = new Properties();
                props.setProperty("jline.terminal", "jline.UnsupportedTerminal");
                props.setProperty("user.language", "en");
                setSystemProperties(props);
                useJline = false;
            }

            // Synchronize around the server start to ensure thread safety.
            synchronized (MinecraftServer.class) {
                MinecraftServer.main(options);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();  // Catch RuntimeExceptions instead of Throwable for safer handling.
        }
    }

    private static OptionParser createOptionParser() {
        return new OptionParser() {{
            acceptsAll(HELP_ARGS, "Show the help");
            acceptsAll(CONFIG_ARGS, "Properties file to use")
                    .withRequiredArg()
                    .ofType(File.class)
                    .defaultsTo(DEFAULT_CONFIG)
                    .describedAs("Properties file");

            acceptsAll(asList("P", "plugins"), "Plugin directory to use")
                    .withRequiredArg()
                    .ofType(File.class)
                    .defaultsTo(DEFAULT_PLUGIN_DIR)
                    .describedAs("Plugin directory");

            acceptsAll(asList("h", "host", "server-ip"), "Host to listen on")
                    .withRequiredArg()
                    .ofType(String.class)
                    .describedAs("Hostname or IP");

            acceptsAll(asList("w", "world", "level-name"), "World directory")
                    .withRequiredArg()
                    .ofType(String.class)
                    .describedAs("World dir");

            acceptsAll(asList("p", "port", "server-port"), "Port to listen on")
                    .withRequiredArg()
                    .ofType(Integer.class)
                    .describedAs("Port");

            acceptsAll(asList("o", "online-mode"), "Whether to use online authentication")
                    .withRequiredArg()
                    .ofType(Boolean.class)
                    .describedAs("Authentication");

            acceptsAll(asList("s", "size", "max-players"), "Maximum amount of players")
                    .withRequiredArg()
                    .ofType(Integer.class)
                    .describedAs("Server size");

            acceptsAll(asList("d", "date-format"), "Format of the date to display in the console (for log entries)")
                    .withRequiredArg()
                    .ofType(SimpleDateFormat.class)
                    .describedAs("Log date format");

            acceptsAll(asList("log-pattern"), "Specifies the log filename pattern")
                    .withRequiredArg()
                    .ofType(String.class)
                    .defaultsTo("server.log")
                    .describedAs("Log filename");

            acceptsAll(asList("log-limit"), "Limits the maximum size of the log file (0 = unlimited)")
                    .withRequiredArg()
                    .ofType(Integer.class)
                    .defaultsTo(0)
                    .describedAs("Max log size");

            acceptsAll(asList("log-count"), "Specifies how many log files to cycle through")
                    .withRequiredArg()
                    .ofType(Integer.class)
                    .defaultsTo(1)
                    .describedAs("Log count");

            acceptsAll(asList("log-append"), "Whether to append to the log file")
                    .withRequiredArg()
                    .ofType(Boolean.class)
                    .defaultsTo(true)
                    .describedAs("Log append");

            acceptsAll(asList("b", "bukkit-settings"), "File for bukkit settings")
                    .withRequiredArg()
                    .ofType(File.class)
                    .defaultsTo(new File("bukkit.yml"))
                    .describedAs("Yml file");

            acceptsAll(asList("debug-config"), "Don't load Poseidon.yml, but generate a new one with all the default values");

            acceptsAll(asList("nojline"), "Disables jline and emulates the vanilla console");

            acceptsAll(asList("nogui"), "Some modern panels like to pass this thru. Just ignore it");

            acceptsAll(asList("v", "version"), "Show the CraftBukkit Version");
        }};
    }

    private static synchronized void setSystemProperties(Properties properties) {
        System.getProperties().putAll(properties);
    }

    private static List<String> asList(String... params) {
        return Arrays.asList(params);
    }
}
