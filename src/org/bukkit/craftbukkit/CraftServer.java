package org.bukkit.craftbukkit;

import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import jline.ConsoleReader;
import net.minecraft.server.*;
import org.bukkit.*;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.inventory.CraftFurnaceRecipe;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftShapedRecipe;
import org.bukkit.craftbukkit.inventory.CraftShapelessRecipe;
import org.bukkit.craftbukkit.map.CraftMapView;
import org.bukkit.craftbukkit.scheduler.CraftScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitWorker;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.bukkit.util.permissions.DefaultPermissions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.MarkedYAMLException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CraftServer implements Server {
    private final String serverName = "Poseidon Plus Craftbukkit";
    //Poseidon Versions
    private final String serverEnvironment = "POSEIDON";
    private final String serverVersion = "1.2";
    private final String releaseType = "RELEASE";
    private final String protocolVersion = "1.7.3";
    private final String GameVersion = "b1.7.3";
    private final ServicesManager servicesManager = new SimpleServicesManager();
    private final BukkitScheduler scheduler = new CraftScheduler(this);
    private final SimpleCommandMap commandMap = new SimpleCommandMap(this);
    private final PluginManager pluginManager = new SimplePluginManager(this, commandMap);
    private final MinecraftServer console;
    private final ServerConfigurationManager server;
    private final Map<String, World> worlds = new LinkedHashMap<String, World>();
    private final Configuration configuration;
    private final Yaml yaml = new Yaml(new SafeConstructor());
    private boolean shuttingdown = false;

    public CraftServer(MinecraftServer console, ServerConfigurationManager server) {
        this.console = console;
        this.server = server;
        //this.serverVersion = CraftServer.class.getPackage().getImplementationVersion(); //Poseidon Replace

        Bukkit.setServer(this);

        configuration = new Configuration((File) console.options.valueOf("bukkit-settings"));
        loadConfig();
        loadPlugins();
        enablePlugins(PluginLoadOrder.STARTUP);

        ChunkCompressionThread.startThread();
    }

    private void loadConfig() {
        configuration.load();
        configuration.getString("database.url", "jdbc:sqlite:{DIR}{NAME}.db");
        configuration.getString("database.username", "bukkit");
        configuration.getString("database.password", "walrus");
        configuration.getString("database.driver", "org.sqlite.JDBC");
        configuration.getString("database.isolation", "SERIALIZABLE");

        configuration.getString("settings.update-folder", "update");
        configuration.getInt("settings.spawn-radius", 16);

        configuration.getString("settings.permissions-file", "permissions.yml");

        if (configuration.getNode("aliases") == null) {
            List<String> icanhasbukkit = new ArrayList<String>();
            icanhasbukkit.add("version");
            configuration.setProperty("aliases.icanhasbukkit", icanhasbukkit);
        }
        configuration.save();
    }

    public void loadPlugins() {
        pluginManager.registerInterface(JavaPluginLoader.class);

        File pluginFolder = (File) console.options.valueOf("plugins");

        if (pluginFolder.exists()) {
            Plugin[] plugins = pluginManager.loadPlugins(pluginFolder);
            for (Plugin plugin : plugins) {
                try {
                    plugin.onLoad();
                } catch (Throwable ex) {
                    Logger.getLogger(CraftServer.class.getName()).log(Level.SEVERE, ex.getMessage() + " initializing " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                }
            }
        } else {
            pluginFolder.mkdir();
        }
    }

    public void enablePlugins(PluginLoadOrder type) {
        Plugin[] plugins = pluginManager.getPlugins();

        for (Plugin plugin : plugins) {
            if ((!plugin.isEnabled()) && (plugin.getDescription().getLoad() == type)) {
                loadPlugin(plugin);
            }
        }

        if (type == PluginLoadOrder.POSTWORLD) {
            commandMap.registerServerAliases();
            loadCustomPermissions();
            DefaultPermissions.registerCorePermissions();
        }
    }

    public void disablePlugins() {
        pluginManager.disablePlugins();
    }

    private void loadPlugin(Plugin plugin) {
        try {
            pluginManager.enablePlugin(plugin);

            List<Permission> perms = plugin.getDescription().getPermissions();

            for (Permission perm : perms) {
                try {
                    pluginManager.addPermission(perm);
                } catch (IllegalArgumentException ex) {
                    getLogger().log(Level.WARNING, "Plugin " + plugin.getDescription().getFullName() + " tried to register permission '" + perm.getName() + "' but it's already registered", ex);
                }
            }
        } catch (Throwable ex) {
            Logger.getLogger(CraftServer.class.getName()).log(Level.SEVERE, ex.getMessage() + " loading " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
        }
    }

    @Override
    public String getGameVersion() {
        return this.GameVersion;
    }

    @Override
    public String getName() {
        return serverName;
    }

    public String getPoseidonVersion() {
        return serverVersion;
    }

    public String getPoseidonReleaseType() {
        return releaseType;
    }

    public String getServerEnvironment() {
        return serverEnvironment;
    }

    public String getVersion() {
        return serverVersion + " (MC: " + protocolVersion + ")";
    }

    public Player[] getOnlinePlayers() {
        List<EntityPlayer> online = server.players;
        Player[] players = new Player[online.size()];

        for (int i = 0; i < players.length; i++) {
            players[i] = online.get(i).netServerHandler.getPlayer();
        }

        return players;
    }

    public Player getPlayer(final String name) {
        Player[] players = getOnlinePlayers();

        Player found = null;
        String lowerName = name.toLowerCase();
        int delta = Integer.MAX_VALUE;
        for (Player player : players) {
            if (player.getName().toLowerCase().startsWith(lowerName)) {
                int curDelta = player.getName().length() - lowerName.length();
                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }
                if (curDelta == 0) break;
            }
        }
        return found;
    }

    //Project Poseidon Start
    @Override
    public Player getPlayer(final UUID uuid) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(uuid)) {
                return p;
            }
        }
        return null;
    }

    //Project Poseidon End


    public Player getPlayerExact(String name) {
        String lname = name.toLowerCase();

        for (Player player : getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(lname)) {
                return player;
            }
        }

        return null;
    }

    public int broadcastMessage(String message) {
        return broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    public Player getPlayer(final EntityPlayer entity) {
        return entity.netServerHandler.getPlayer();
    }

    public List<Player> matchPlayer(String partialName) {
        List<Player> matchedPlayers = new ArrayList<Player>();

        for (Player iterPlayer : this.getOnlinePlayers()) {
            String iterPlayerName = iterPlayer.getName();

            if (partialName.equalsIgnoreCase(iterPlayerName)) {
                // Exact match
                matchedPlayers.clear();
                matchedPlayers.add(iterPlayer);
                break;
            }
            if (iterPlayerName.toLowerCase().contains(partialName.toLowerCase())) {
                // Partial match
                matchedPlayers.add(iterPlayer);
            }
        }

        return matchedPlayers;
    }

    public int getMaxPlayers() {
        return server.maxPlayers;
    }

    // NOTE: These are dependent on the corrisponding call in MinecraftServer
    // so if that changes this will need to as well
    public int getPort() {
        return this.getConfigInt("server-port", 25565);
    }

    public int getViewDistance() {
        return this.getConfigInt("view-distance", 10);
    }

    public String getIp() {
        return this.getConfigString("server-ip", "");
    }

    public String getServerName() {
        return this.getConfigString("server-name", "Unknown Server");
    }

    public String getServerId() {
        return this.getConfigString("server-id", "unnamed");
    }

    public boolean getAllowNether() {
        return this.getConfigBoolean("allow-nether", true);
    }

    public boolean hasWhitelist() {
        return this.getConfigBoolean("white-list", false);
    }

    // NOTE: Temporary calls through to server.properies until its replaced
    private String getConfigString(String variable, String defaultValue) {
        return this.console.propertyManager.getString(variable, defaultValue);
    }

    private int getConfigInt(String variable, int defaultValue) {
        return this.console.propertyManager.getInt(variable, defaultValue);
    }

    private boolean getConfigBoolean(String variable, boolean defaultValue) {
        return this.console.propertyManager.getBoolean(variable, defaultValue);
    }

    // End Temporary calls

    public String getUpdateFolder() {
        return this.configuration.getString("settings.update-folder", "update");
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public BukkitScheduler getScheduler() {
        return scheduler;
    }

    public ServicesManager getServicesManager() {
        return servicesManager;
    }

    public List<World> getWorlds() {
        return new ArrayList<World>(worlds.values());
    }

    public ServerConfigurationManager getHandle() {
        return server;
    }


    // NOTE: Should only be called from MinecraftServer.b()
    public void dispatchCommand(CommandSender sender, ServerCommand serverCommand) {
        dispatchCommand(sender, serverCommand.command);
    }

    public boolean dispatchCommand(CommandSender sender, String commandLine) {
        if (commandMap.dispatch(sender, commandLine)) {
            return true;
        }

        sender.sendMessage(ChatColor.RED + "=> Unknown Command. Type /help for a list of commands.");

        return false;
    }

    public void reload() {
        loadConfig();
        PropertyManager config = new PropertyManager(console.options);

        console.propertyManager = config;

        boolean animals = config.getBoolean("spawn-animals", console.spawnAnimals);
        boolean monsters = config.getBoolean("spawn-monsters", console.worlds.get(0).spawnMonsters > 0);

        console.onlineMode = config.getBoolean("online-mode", console.onlineMode);
        console.spawnAnimals = config.getBoolean("spawn-animals", console.spawnAnimals);
        console.pvpMode = config.getBoolean("pvp", console.pvpMode);
        console.allowFlight = config.getBoolean("allow-flight", console.allowFlight);

        for (WorldServer world : console.worlds) {
            world.spawnMonsters = monsters ? 1 : 0;
            world.setSpawnFlags(monsters, animals);
        }

        pluginManager.clearPlugins();
        commandMap.clearCommands();

        int pollCount = 0;

        // Wait for at most 2.5 seconds for plugins to close their threads
        while (pollCount < 50 && getScheduler().getActiveWorkers().size() > 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
            pollCount++;
        }

        List<BukkitWorker> overdueWorkers = getScheduler().getActiveWorkers();
        for (BukkitWorker worker : overdueWorkers) {
            Plugin plugin = worker.getOwner();
            String author = "<NoAuthorGiven>";
            if (plugin.getDescription().getAuthors().size() > 0) {
                author = plugin.getDescription().getAuthors().get(0);
            }
            getLogger().log(Level.SEVERE, String.format(
                    "Nag author: '%s' of '%s' about the following: %s",
                    author,
                    plugin.getDescription().getName(),
                    "This plugin is not properly shutting down its async tasks when it is being reloaded.  This may cause conflicts with the newly loaded version of the plugin"
            ));
        }
        loadPlugins();
        enablePlugins(PluginLoadOrder.STARTUP);
        enablePlugins(PluginLoadOrder.POSTWORLD);
    }

    private void loadCustomPermissions() {
        File file = new File(configuration.getString("settings.permissions-file"));
        FileInputStream stream;

        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            try {
                file.createNewFile();
            } finally {
                return;
            }
        }

        Map<String, Map<String, Object>> perms;

        try {
            perms = (Map<String, Map<String, Object>>) yaml.load(stream);
        } catch (MarkedYAMLException ex) {
            getLogger().log(Level.WARNING, "Server permissions file " + file + " is not valid YAML: " + ex.toString());
            return;
        } catch (Throwable ex) {
            getLogger().log(Level.WARNING, "Server permissions file " + file + " is not valid YAML.", ex);
            return;
        } finally {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }

        if (perms == null) {
            getLogger().log(Level.INFO, "Server permissions file " + file + " is empty, ignoring it");
            return;
        }

        Set<String> keys = perms.keySet();

        for (String name : keys) {
            try {
                pluginManager.addPermission(Permission.loadPermission(name, perms.get(name)));
            } catch (Throwable ex) {
                Bukkit.getServer().getLogger().log(Level.SEVERE, "Permission node '" + name + "' in server config is invalid", ex);
            }
        }
    }

    @Override
    public String toString() {
        return "CraftServer{" + "serverName=" + serverName + ",serverVersion=" + serverVersion + ",protocolVersion=" + protocolVersion + '}';
    }

    public World createWorld(String name, World.Environment environment) {
        return createWorld(name, environment, (new Random()).nextLong());
    }

    public World createWorld(String name, World.Environment environment, long seed) {
        return createWorld(name, environment, seed, null);
    }

    public World createWorld(String name, Environment environment, ChunkGenerator generator) {
        return createWorld(name, environment, (new Random()).nextLong(), generator);
    }

    public World createWorld(String name, Environment environment, long seed, ChunkGenerator generator) {
        File folder = new File(name);
        World world = getWorld(name);

        if (world != null) {
            return world;
        }

        if ((folder.exists()) && (!folder.isDirectory())) {
            throw new IllegalArgumentException("File exists with the name '" + name + "' and isn't a folder");
        }

        if (generator == null) {
            generator = getGenerator(name);
        }

        Convertable converter = new WorldLoaderServer(folder);
        if (converter.isConvertable(name)) {
            getLogger().info("Converting world '" + name + "'");
            converter.convert(name, new ConvertProgressUpdater(console));
        }

        int dimension = 10 + console.worlds.size();
        WorldServer internal = new WorldServer(console, new ServerNBTManager(new File("."), name, true), name, dimension, seed, environment, generator);

        if (!(worlds.containsKey(name.toLowerCase()))) {
            return null;
        }

        internal.worldMaps = console.worlds.get(0).worldMaps;

        internal.tracker = new EntityTracker(console, dimension);
        internal.addIWorldAccess((IWorldAccess) new WorldManager(console, internal));
        internal.spawnMonsters = 1;
        internal.setSpawnFlags(true, true);
        console.worlds.add(internal);

        if (generator != null) {
            internal.getWorld().getPopulators().addAll(generator.getDefaultPopulators(internal.getWorld()));
        }

        pluginManager.callEvent(new WorldInitEvent(internal.getWorld()));
        System.out.print("Preparing start region for level " + (console.worlds.size() - 1) + " (Seed: " + internal.getSeed() + ")");

        if (internal.getWorld().getKeepSpawnInMemory()) {
            short short1 = 196;
            long i = System.currentTimeMillis();
            for (int j = -short1; j <= short1; j += 16) {
                for (int k = -short1; k <= short1; k += 16) {
                    long l = System.currentTimeMillis();

                    if (l < i) {
                        i = l;
                    }

                    if (l > i + 1000L) {
                        int i1 = (short1 * 2 + 1) * (short1 * 2 + 1);
                        int j1 = (j + short1) * (short1 * 2 + 1) + k + 1;

                        System.out.println("Preparing spawn area for " + name + ", " + (j1 * 100 / i1) + "%");
                        i = l;
                    }

                    ChunkCoordinates chunkcoordinates = internal.getSpawn();
                    internal.chunkProviderServer.getChunkAt(chunkcoordinates.x + j >> 4, chunkcoordinates.z + k >> 4);

                    while (internal.doLighting()) {
                        ;
                    }
                }
            }
        }
        pluginManager.callEvent(new WorldLoadEvent(internal.getWorld()));
        return internal.getWorld();
    }

    public boolean unloadWorld(String name, boolean save) {
        return unloadWorld(getWorld(name), save);
    }

    public boolean unloadWorld(World world, boolean save) {
        if (world == null) {
            return false;
        }

        WorldServer handle = ((CraftWorld) world).getHandle();

        if (!(console.worlds.contains(handle))) {
            return false;
        }

        if (!(handle.dimension > 1)) {
            return false;
        }

        if (!handle.players.isEmpty()) {
            return false;
        }

        WorldUnloadEvent e = new WorldUnloadEvent(handle.getWorld());

        if (e.isCancelled()) {
            return false;
        }

        if (save) {
            handle.save(true, (IProgressUpdate) null);
            handle.saveLevel();
            WorldSaveEvent event = new WorldSaveEvent(handle.getWorld());
            getPluginManager().callEvent(event);
        }

        worlds.remove(world.getName().toLowerCase());
        console.worlds.remove(handle);

        return true;
    }

    public MinecraftServer getServer() {
        return console;
    }

    public World getWorld(String name) {
        return worlds.get(name.toLowerCase());
    }

    public World getWorld(UUID uid) {
        for (World world : worlds.values()) {
            if (world.getUID().equals(uid)) {
                return world;
            }
        }
        return null;
    }

    public void addWorld(World world) {
        // Check if a World already exists with the UID.
        if (getWorld(world.getUID()) != null) {
            System.out.println("World " + world.getName() + " is a duplicate of another world and has been prevented from loading. Please delete the uid.dat file from " + world.getName() + "'s world directory if you want to be able to load the duplicate world.");
            return;
        }
        worlds.put(world.getName().toLowerCase(), world);
    }

    public Logger getLogger() {
        return MinecraftServer.log;
    }

    public ConsoleReader getReader() {
        return console.reader;
    }

    public PluginCommand getPluginCommand(String name) {
        Command command = commandMap.getCommand(name);

        if (command instanceof PluginCommand) {
            return (PluginCommand) command;
        } else {
            return null;
        }
    }

    public void savePlayers() {
        server.savePlayers();
    }

    public void configureDbConfig(ServerConfig config) {
        DataSourceConfig ds = new DataSourceConfig();
        ds.setDriver(configuration.getString("database.driver"));
        ds.setUrl(configuration.getString("database.url"));
        ds.setUsername(configuration.getString("database.username"));
        ds.setPassword(configuration.getString("database.password"));
        ds.setIsolationLevel(TransactionIsolation.getLevel(configuration.getString("database.isolation")));

        if (ds.getDriver().contains("sqlite")) {
            config.setDatabasePlatform(new SQLitePlatform());
            config.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
        }

        config.setDataSourceConfig(ds);
    }

    public boolean addRecipe(Recipe recipe) {
        CraftRecipe toAdd;
        if (recipe instanceof CraftRecipe) {
            toAdd = (CraftRecipe) recipe;
        } else {
            if (recipe instanceof ShapedRecipe) {
                toAdd = CraftShapedRecipe.fromBukkitRecipe((ShapedRecipe) recipe);
            } else if (recipe instanceof ShapelessRecipe) {
                toAdd = CraftShapelessRecipe.fromBukkitRecipe((ShapelessRecipe) recipe);
            } else if (recipe instanceof FurnaceRecipe) {
                toAdd = CraftFurnaceRecipe.fromBukkitRecipe((FurnaceRecipe) recipe);
            } else {
                return false;
            }
        }
        toAdd.addToCraftingManager();
        return true;
    }

    public Map<String, String[]> getCommandAliases() {
        ConfigurationNode node = configuration.getNode("aliases");
        Map<String, String[]> result = new LinkedHashMap<String, String[]>();

        if (node != null) {
            for (String key : node.getKeys()) {
                List<String> commands = new ArrayList<String>();

                if (node.getProperty(key) instanceof List) {
                    commands = node.getStringList(key, null);
                } else {
                    commands.add(node.getString(key));
                }

                result.put(key, commands.toArray(new String[0]));
            }
        }

        return result;
    }

    public int getSpawnRadius() {
        return configuration.getInt("settings.spawn-radius", 16);
    }

    public void setSpawnRadius(int value) {
        configuration.setProperty("settings.spawn-radius", value);
        configuration.save();
    }

    public boolean getOnlineMode() {
        return this.console.onlineMode;
    }

    public boolean getAllowFlight() {
        return this.console.allowFlight;
    }

    public ChunkGenerator getGenerator(String world) {
        ConfigurationNode node = configuration.getNode("worlds");
        ChunkGenerator result = null;

        if (node != null) {
            node = node.getNode(world);

            if (node != null) {
                String name = node.getString("generator");

                if ((name != null) && (!name.isEmpty())) {
                    String[] split = name.split(":", 2);
                    String id = (split.length > 1) ? split[1] : null;
                    Plugin plugin = pluginManager.getPlugin(split[0]);

                    if (plugin == null) {
                        getLogger().severe("Could not set generator for default world '" + world + "': Plugin '" + split[0] + "' does not exist");
                    } else if (!plugin.isEnabled()) {
                        getLogger().severe("Could not set generator for default world '" + world + "': Plugin '" + split[0] + "' is not enabled yet (is it load:STARTUP?)");
                    } else {
                        result = plugin.getDefaultWorldGenerator(world, id);
                    }
                }
            }
        }

        return result;
    }

    public CraftMapView getMap(short id) {
        WorldMapCollection collection = console.worlds.get(0).worldMaps;
        WorldMap worldmap = (WorldMap) collection.a(WorldMap.class, "map_" + id);
        if (worldmap == null) {
            return null;
        }
        return worldmap.mapView;
    }

    public CraftMapView createMap(World world) {
        ItemStack stack = new ItemStack(Item.MAP, 1, -1);
        WorldMap worldmap = Item.MAP.a(stack, ((CraftWorld) world).getHandle());
        return worldmap.mapView;
    }

    public void shutdown() {
        console.a();
    }

    public int broadcast(String message, String permission) {
//        int count = 0;
//        Set<Permissible> permissibles = getPluginManager().getPermissionSubscriptions(permission);
//
//        for (Permissible permissible : permissibles) {
//            if (permissible instanceof CommandSender) {
//                CommandSender user = (CommandSender)permissible;
//                user.sendMessage(message);
//                count++;
//            }
//        }
//
//        return count;
        Player[] players = getOnlinePlayers();

        for (Player player : players) {
            player.sendMessage(message);
        }

        return players.length;
    }

    public OfflinePlayer getOfflinePlayer(String name) {
        OfflinePlayer result = getPlayerExact(name);

        if (result == null) {
            result = new CraftOfflinePlayer(this, name);
        }

        return result;
    }

    public Set<String> getIPBans() {
        return new HashSet(server.banByIP);
    }

    public void banIP(String address) {
        server.c(address);
    }

    public void unbanIP(String address) {
        server.d(address);
    }

    public Set<OfflinePlayer> getBannedPlayers() {
        Set<OfflinePlayer> result = new HashSet<OfflinePlayer>();

        for (Object name : server.banByName) {
            result.add(getOfflinePlayer((String) name));
        }

        return result;
    }

    public void setWhitelist(boolean value) {
        server.o = value;
        console.propertyManager.b("white-list", value);
        console.propertyManager.savePropertiesFile();
    }

    public Set<OfflinePlayer> getWhitelistedPlayers() {
        Set<OfflinePlayer> result = new HashSet<OfflinePlayer>();

        for (Object name : server.e()) {
            result.add(getOfflinePlayer((String) name));
        }

        return result;
    }

    public void reloadWhitelist() {
        server.f();
    }

    public boolean isShuttingdown() {
        return shuttingdown;
    }

    public void setShuttingdown(boolean shuttingdown) {
        this.shuttingdown = shuttingdown;
    }

//    public GameMode getDefaultGameMode() {
//        return GameMode.SURVIVAL;
//    }
//
//    public void setDefaultGameMode(GameMode mode) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
}
