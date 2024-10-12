package net.minecraft.server;

import com.legacyminecraft.poseidon.PlusConfig;
import com.legacyminecraft.poseidon.PoseidonConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

// CraftBukkit start
// CraftBukkit end

public class ServerConfigurationManager {

    public static final Logger a = Logger.getLogger("Minecraft");
    public final List<EntityPlayer> players = new ArrayList<>();
    public final MinecraftServer server; // CraftBukkit - private -> public
    // private PlayerManager[] d = new PlayerManager[2]; // CraftBukkit - removed
    public int maxPlayers; // CraftBukkit - private -> public
    public final Set<String> banByName = new HashSet<>(); // CraftBukkit - private -> public
    public final Set<String> banByIP = new HashSet<>(); // CraftBukkit - private -> public
    private final Set<String> h = new HashSet<>();
    private final Set<String> i = new HashSet<>();
    private final File j;
    private final File k;
    private final File l;
    private final File m;
    public PlayerFileData playerFileData; // CraftBukkit - private - >public
    public boolean o; // Craftbukkit - private -> public

    // CraftBukkit start
    private final CraftServer cserver;
    private final String msgKickBanned, msgKickIPBanned, msgKickWhitelist, msgKickServerFull, msgPlayerJoin, msgPlayerLeave, alternateLocationKickMessage;

    public ServerConfigurationManager(MinecraftServer minecraftserver) {
        minecraftserver.server = new CraftServer(minecraftserver, this);
        minecraftserver.console = new ColouredConsoleSender(minecraftserver.server);
        this.cserver = minecraftserver.server;
        alternateLocationKickMessage = PlusConfig.getInstance().getString("messages.kick.alternateLocation", "&cYou logged in from another location!");
        msgKickServerFull = PlusConfig.getInstance().getString("messages.kick.full", "&cThe server is full.");
        msgKickBanned = PlusConfig.getInstance().getString("messages.kick.ban", "&cYou have been banned from this server.");
        msgKickWhitelist = PlusConfig.getInstance().getString("messages.kick.whitelist", "&cServer currently whitelisted. Please try again later.");
        // CraftBukkit end
        this.msgKickIPBanned = PlusConfig.getInstance().getString("messages.kick.ip-banned", "&cYour IP address is banned from this server.");
        this.msgPlayerJoin = PlusConfig.getInstance().getString("messages.player.join", "&e%player% joined the game.");
        this.msgPlayerLeave = PlusConfig.getInstance().getString("messages.player.leave", "&e%player% left the game.");

        this.server = minecraftserver;
        this.j = minecraftserver.a("banned-players.txt");
        this.k = minecraftserver.a("banned-ips.txt");
        this.l = minecraftserver.a("ops.txt");
        this.m = minecraftserver.a("white-list.txt");
        int i = minecraftserver.propertyManager.getInt("view-distance", 10);

        // CraftBukkit - removed playermanagers
        this.maxPlayers = minecraftserver.propertyManager.getInt("max-players", 20);
        this.o = minecraftserver.propertyManager.getBoolean("white-list", false);
        this.g();
        this.i();
        this.k();
        this.m();
        this.h();
        this.j();
        this.l();
        this.n();
    }

    public void setPlayerFileData(WorldServer[] aworldserver) {
        if (this.playerFileData != null) return; // CraftBukkit
        this.playerFileData = aworldserver[0].p().d();
    }

    public void a(EntityPlayer entityplayer) {
        // CraftBukkit - removed playermanagers
        for (WorldServer world : this.server.worlds) {
            if (world.manager.managedPlayers.contains(entityplayer)) {
                world.manager.removePlayer(entityplayer);
                break;
            }
        }
        this.getPlayerManager(entityplayer.dimension).addPlayer(entityplayer);
        WorldServer worldserver = this.server.getWorldServer(entityplayer.dimension);

        worldserver.chunkProviderServer.getChunkAt((int) entityplayer.locX >> 4, (int) entityplayer.locZ >> 4);
    }

    public int a() {
        // CraftBukkit start
        if (this.server.worlds.isEmpty()) {
            return this.server.propertyManager.getInt("view-distance", 10) * 16 - 16;
        }
        return this.server.worlds.get(0).manager.getFurthestViewableBlock();
        // CraftBukkit end
    }

    private PlayerManager getPlayerManager(int i) {
        return this.server.getWorldServer(i).manager; // CraftBukkit
    }

    public void b(EntityPlayer entityplayer) {
        this.playerFileData.b(entityplayer);
    }

    public void c(EntityPlayer entityplayer) {
        this.players.add(entityplayer);
        //PlayerTracker.getInstance().addPlayer(entityplayer.name);
        WorldServer worldserver = this.server.getWorldServer(entityplayer.dimension);

        worldserver.chunkProviderServer.getChunkAt((int) entityplayer.locX >> 4, (int) entityplayer.locZ >> 4);

        if ((boolean) PoseidonConfig.getInstance().getConfigOption("world-settings.teleport-to-highest-safe-block")) {
            while (!worldserver.getEntities(entityplayer, entityplayer.boundingBox).isEmpty()) {
                entityplayer.setPosition(entityplayer.locX, entityplayer.locY + 1.0D, entityplayer.locZ);
            }
        }

        // CraftBukkit start
        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(this.cserver.getPlayer(entityplayer), msgPlayerJoin.replace("%player%", entityplayer.name).replace("&", "\u00A7"));
        this.cserver.getPluginManager().callEvent(playerJoinEvent);

        String joinMessage = playerJoinEvent.getJoinMessage();

        if (joinMessage != null) {
            this.server.serverConfigurationManager.sendAll(new Packet3Chat(joinMessage));
        }
        // CraftBukkit end

        worldserver.addEntity(entityplayer);
        this.getPlayerManager(entityplayer.dimension).addPlayer(entityplayer);
    }

    public void d(EntityPlayer entityplayer) {
        this.getPlayerManager(entityplayer.dimension).movePlayer(entityplayer);
    }

    public String disconnect(EntityPlayer entityplayer) { // CraftBukkit - changed return type
        //if(entityplayer.netServerHandler.disconnected) return null; // CraftBukkit - exploits fix https://github.com/OvercastNetwork/CraftBukkit/commit/6f79ca5c54d30d04803143975757713a01bf4e35


        // CraftBukkit start
        // Quitting must be before we do final save of data, in case plugins need to modify it
        this.getPlayerManager(entityplayer.dimension).removePlayer(entityplayer);
        PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(this.cserver.getPlayer(entityplayer), this.msgPlayerLeave.replace("%player%", entityplayer.name).replace("&", "\u00A7"));
        this.cserver.getPluginManager().callEvent(playerQuitEvent);
        // CraftBukkit end

        //Project POSEIDON Start
//        boolean found = false;
//        for (int i = 0; i < this.players.size(); ++i) {
//            EntityPlayer ep = (EntityPlayer) this.players.get(i);
//            if (entityplayer.name.equalsIgnoreCase(ep.name)) {
//                found = true;
//                break;
//            }
//        }
//        if (!found) {
//            //return null; - This caused a bug which could block future connections if a quit event occurs before a join event, i think
//            playerQuitEvent.setQuitMessage(null);
//        }
//        PlayerTracker.getInstance().removePlayer(entityplayer.name);
        //Project POSEIDON End

        this.playerFileData.a(entityplayer);
        this.server.getWorldServer(entityplayer.dimension).kill(entityplayer);
        this.players.remove(entityplayer);
        this.getPlayerManager(entityplayer.dimension).removePlayer(entityplayer);

        return playerQuitEvent.getQuitMessage(); // CraftBukkit
    }

    public EntityPlayer a(NetLoginHandler netloginhandler, String s) {
        // CraftBukkit start - note: this entire method needs to be changed
        // Instead of kicking then returning, we need to store the kick reason
        // in the event, check with plugins to see if it's ok, and THEN kick
        // depending on the outcome. Also change any reference to this.e.c to entity.world
        EntityPlayer entity = new EntityPlayer(this.server, this.server.getWorldServer(0), s, new ItemInWorldManager(this.server.getWorldServer(0)));
        Player player = (Player) entity.getBukkitEntity();
        PlayerLoginEvent event = new PlayerLoginEvent(player, netloginhandler); //Project Poseidon - pass player IP through

        String s1 = netloginhandler.networkManager.getSocketAddress().toString();

        s1 = s1.substring(s1.indexOf("/") + 1);
        s1 = s1.substring(0, s1.indexOf(":"));

        PlayerLoginEvent.Result result = this.banByName.contains(s.trim().toLowerCase()) ? PlayerLoginEvent.Result.KICK_BANNED : this.banByIP.contains(s1) ? PlayerLoginEvent.Result.KICK_BANNED_IP : !this.isWhitelisted(s) ? PlayerLoginEvent.Result.KICK_WHITELIST : this.players.size() >= this.maxPlayers ? PlayerLoginEvent.Result.KICK_FULL : PlayerLoginEvent.Result.ALLOWED;

        String kickMessage = result.equals(PlayerLoginEvent.Result.KICK_BANNED) ? this.msgKickBanned : result.equals(PlayerLoginEvent.Result.KICK_BANNED_IP) ? this.msgKickIPBanned : result.equals(PlayerLoginEvent.Result.KICK_WHITELIST) ? this.msgKickWhitelist : result.equals(PlayerLoginEvent.Result.KICK_FULL) ? msgKickServerFull : s1;

        event.disallow(result, kickMessage);

        this.cserver.getPluginManager().callEvent(event);
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            netloginhandler.disconnect(event.getKickMessage());
            return null;
        }

        for (EntityPlayer entityPlayer : this.players) {
            if (entityPlayer.name.equalsIgnoreCase(s))
                entityPlayer.netServerHandler.disconnect(alternateLocationKickMessage);
        }

        return entity;
        // CraftBukkit end
    }

    // CraftBukkit start
    public EntityPlayer moveToWorld(EntityPlayer entityplayer, int i) {
        return this.moveToWorld(entityplayer, i, null);
    }

    public EntityPlayer moveToWorld(EntityPlayer entityplayer, int i, Location location) {
        this.server.getTracker(entityplayer.dimension).untrackPlayer(entityplayer);
        // this.server.getTracker(entityplayer.dimension).untrackEntity(entityplayer); // CraftBukkit
        this.getPlayerManager(entityplayer.dimension).removePlayer(entityplayer);
        this.players.remove(entityplayer);
        //PlayerTracker.getInstance().removePlayer(entityplayer.name); //Project POSEIDON
        this.server.getWorldServer(entityplayer.dimension).removeEntity(entityplayer);
        ChunkCoordinates chunkcoordinates = entityplayer.getBed();

        // CraftBukkit start
        org.bukkit.World fromWorld = entityplayer.getBukkitEntity().getWorld();

        if (location == null) {
            boolean isBedSpawn = false;
            CraftWorld cworld = (CraftWorld) this.server.server.getWorld(entityplayer.spawnWorld);
            if (cworld != null && chunkcoordinates != null) {
                ChunkCoordinates chunkcoordinates1 = EntityHuman.getBed(cworld.getHandle(), chunkcoordinates);
                if (chunkcoordinates1 != null) {
                    isBedSpawn = true;
                    location = new Location(cworld, chunkcoordinates1.x + 0.5, chunkcoordinates1.y, chunkcoordinates1.z + 0.5);
                } else {
                    entityplayer.netServerHandler.sendPacket(new Packet70Bed(0));
                }
            }

            if (location == null) {
                cworld = (CraftWorld) this.server.server.getWorlds().get(0);
                chunkcoordinates = cworld.getHandle().getSpawn();
                location = new Location(cworld, chunkcoordinates.x + 0.5, chunkcoordinates.y, chunkcoordinates.z + 0.5);
            }

            Player respawnPlayer = this.cserver.getPlayer(entityplayer);
            PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(respawnPlayer, location, isBedSpawn);
            this.cserver.getPluginManager().callEvent(respawnEvent);

            location = respawnEvent.getRespawnLocation();
            entityplayer.health = 20;
            entityplayer.fireTicks = 0;
            entityplayer.fallDistance = 0;
        } else {
            location.setWorld(this.server.getWorldServer(i).getWorld());
        }
        WorldServer worldserver = ((CraftWorld) location.getWorld()).getHandle();
        entityplayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        // CraftBukkit end

        worldserver.chunkProviderServer.getChunkAt((int) entityplayer.locX >> 4, (int) entityplayer.locZ >> 4);

        while (!worldserver.getEntities(entityplayer, entityplayer.boundingBox).isEmpty()) {
            entityplayer.setPosition(entityplayer.locX, entityplayer.locY + 1.0D, entityplayer.locZ);
        }

        // CraftBukkit start
        byte actualDimension = (byte) (worldserver.getWorld().getEnvironment().getId());
        entityplayer.netServerHandler.sendPacket(new Packet9Respawn((byte) (actualDimension >= 0 ? -1 : 0)));
        entityplayer.netServerHandler.sendPacket(new Packet9Respawn(actualDimension));
        entityplayer.spawnIn(worldserver);
        entityplayer.dead = false;
        entityplayer.netServerHandler.teleport(new Location(worldserver.getWorld(), entityplayer.locX, entityplayer.locY, entityplayer.locZ, entityplayer.yaw, entityplayer.pitch));
        // CraftBukkit end
        this.a(entityplayer, worldserver);
        this.getPlayerManager(entityplayer.dimension).addPlayer(entityplayer);
        worldserver.addEntity(entityplayer);
        this.players.add(entityplayer);
        //PlayerTracker.getInstance().addPlayer(entityplayer1.name); //Project POSEIDON
        this.updateClient(entityplayer); // CraftBukkit
        entityplayer.x();
        // CraftBukkit start - don't fire on respawn
        if (fromWorld != location.getWorld()) {
            org.bukkit.event.player.PlayerChangedWorldEvent event = new org.bukkit.event.player.PlayerChangedWorldEvent((Player) entityplayer.getBukkitEntity(), fromWorld);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
        // CraftBukkit end
        return entityplayer;
    }

    public void f(EntityPlayer entityplayer) {
        // CraftBukkit start -- Replaced the standard handling of portals with a more customised method.
        int dimension = entityplayer.dimension;
        WorldServer fromWorld = this.server.getWorldServer(dimension);
        WorldServer toWorld = null;
        if (dimension < 10) {
            int toDimension = dimension == -1 ? 0 : -1;
            for (WorldServer world : this.server.worlds) {
                if (world.dimension == toDimension) {
                    toWorld = world;
                }
            }
        }
        double blockRatio = dimension == -1 ? 8 : 0.125;

        Location fromLocation = new Location(fromWorld.getWorld(), entityplayer.locX, entityplayer.locY, entityplayer.locZ, entityplayer.yaw, entityplayer.pitch);
        Location toLocation = toWorld == null ? null : new Location(toWorld.getWorld(), (entityplayer.locX * blockRatio), entityplayer.locY, (entityplayer.locZ * blockRatio), entityplayer.yaw, entityplayer.pitch);

        org.bukkit.craftbukkit.PortalTravelAgent pta = new org.bukkit.craftbukkit.PortalTravelAgent();
        PlayerPortalEvent event = new PlayerPortalEvent((Player) entityplayer.getBukkitEntity(), fromLocation, toLocation, pta);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled() || event.getTo() == null) {
            return;
        }

        Location finalLocation = event.getTo();
        if (event.useTravelAgent()) {
            finalLocation = event.getPortalTravelAgent().findOrCreate(finalLocation);
        }
        toWorld = ((CraftWorld) finalLocation.getWorld()).getHandle();
        this.moveToWorld(entityplayer, toWorld.dimension, finalLocation);
        // CraftBukkit end
    }

    public void b() {
        // CraftBukkit start
        for (int i = 0; i < this.server.worlds.size(); ++i) {
            this.server.worlds.get(i).manager.flush();
        }
        // CraftBukkit end
    }

    public void flagDirty(int i, int j, int k, int l) {
        this.getPlayerManager(l).flagDirty(i, j, k);
    }

    public void sendAll(Packet packet) {
        for (EntityPlayer player : this.players) {
            player.netServerHandler.sendPacket(packet);
        }
    }

    public void a(Packet packet, int i) {
        for (EntityPlayer player : this.players) {
            if (player.dimension == i) player.netServerHandler.sendPacket(packet);
        }
    }

    public String c() {
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < this.players.size(); ++i) {
            if (i > 0) {
                s.append(", ");
            }

            s.append(this.players.get(i).name);
        }

        return s.toString();
    }

    public void a(String s) {
        this.banByName.add(s.toLowerCase());
        this.h();
    }

    public void b(String s) {
        this.banByName.remove(s.toLowerCase());
        this.h();
    }

    private void g() {
        try {
            this.banByName.clear();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(this.j));
            String s;

            while ((s = bufferedreader.readLine()) != null) {
                this.banByName.add(s.trim().toLowerCase());
            }

            bufferedreader.close();
        } catch (Exception exception) {
            a.warning("Failed to load ban list: " + exception);
        }
    }

    private void h() {
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.j, false));

            for (String s : this.banByName) {
                printwriter.println(s);
            }

            printwriter.close();
        } catch (Exception exception) {
            a.warning("Failed to save ban list: " + exception);
        }
    }

    public void c(String s) {
        this.banByIP.add(s.toLowerCase());
        this.j();
    }

    public void d(String s) {
        this.banByIP.remove(s.toLowerCase());
        this.j();
    }

    private void i() {
        try {
            this.banByIP.clear();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(this.k));
            String s;

            while ((s = bufferedreader.readLine()) != null) {
                this.banByIP.add(s.trim().toLowerCase());
            }

            bufferedreader.close();
        } catch (Exception exception) {
            a.warning("Failed to load ip ban list: " + exception);
        }
    }

    private void j() {
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.k, false));

            for (String s : this.banByIP) {
                printwriter.println(s);
            }

            printwriter.close();
        } catch (Exception exception) {
            a.warning("Failed to save ip ban list: " + exception);
        }
    }

    public void e(String s) {
        this.h.add(s.toLowerCase());
        this.l();

        // Craftbukkit start
        Player player = server.server.getPlayer(s);
        if (player != null) {
            player.recalculatePermissions();
        }
        // Craftbukkit end
    }

    public void f(String s) {
        this.h.remove(s.toLowerCase());
        this.l();

        // Craftbukkit start
        Player player = server.server.getPlayer(s);
        if (player != null) {
            player.recalculatePermissions();
        }
        // Craftbukkit end
    }

    private void k() {
        try {
            this.h.clear();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(this.l));
            String s;

            while ((s = bufferedreader.readLine()) != null) {
                this.h.add(s.trim().toLowerCase());
            }

            bufferedreader.close();
        } catch (Exception exception) {
            // CraftBukkit - corrected text
            a.warning("Failed to load ops: " + exception);
        }
    }

    private void l() {
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.l, false));

            for (String s : this.h) {
                printwriter.println(s);
            }

            printwriter.close();
        } catch (Exception exception) {
            // CraftBukkit - corrected text
            a.warning("Failed to save ops: " + exception);
        }
    }

    private void m() {
        try {
            this.i.clear();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(this.m));
            String s;

            while ((s = bufferedreader.readLine()) != null) {
                this.i.add(s.trim().toLowerCase());
            }

            bufferedreader.close();
        } catch (Exception exception) {
            a.warning("Failed to load white-list: " + exception);
        }
    }

    private void n() {
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.m, false));

            for (String s : this.i) {
                printwriter.println(s);
            }

            printwriter.close();
        } catch (Exception exception) {
            a.warning("Failed to save white-list: " + exception);
        }
    }

    public boolean isWhitelisted(String s) {
        s = s.trim().toLowerCase();
        return !this.o || this.h.contains(s) || this.i.contains(s);
    }

    public boolean isOp(String s) {
        return this.h.contains(s.trim().toLowerCase());
    }

    public EntityPlayer i(String s) {
        for (EntityPlayer player : this.players) {
            if (player.name.equalsIgnoreCase(s)) return player;
        }

        return null;
    }

    public void a(String s, String s1) {
        EntityPlayer entityplayer = this.i(s);

        if (entityplayer != null) {
            entityplayer.netServerHandler.sendPacket(new Packet3Chat(s1));
        }
    }

    public void sendPacketNearby(double d0, double d1, double d2, double d3, int i, Packet packet) {
        this.sendPacketNearby(null, d0, d1, d2, d3, i, packet);
    }

    public void sendPacketNearby(EntityHuman entityhuman, double d0, double d1, double d2, double d3, int i, Packet packet) {
        for (EntityPlayer player : this.players) {
            if (player != entityhuman && player.dimension == i) {
                double d4 = d0 - player.locX;
                double d5 = d1 - player.locY;
                double d6 = d2 - player.locZ;

                if (d4 * d4 + d5 * d5 + d6 * d6 < d3 * d3) player.netServerHandler.sendPacket(packet);
            }
        }
    }

    public void j(String s) {
        Packet3Chat packet3chat = new Packet3Chat(s);

        for (EntityPlayer player : this.players) {
            if (this.isOp(player.name)) player.netServerHandler.sendPacket(packet3chat);
        }
    }

    public boolean a(String s, Packet packet) {
        EntityPlayer entityplayer = this.i(s);

        if (entityplayer != null) {
            entityplayer.netServerHandler.sendPacket(packet);
            return true;
        } else {
            return false;
        }
    }

    public void savePlayers() {
        for (EntityPlayer player : this.players) {
            this.playerFileData.a(player);
        }
    }

    public void a(int i, int j, int k, TileEntity tileentity) {
    }

    public void k(String s) {
        this.i.add(s);
        this.n();
    }

    public void l(String s) {
        this.i.remove(s);
        this.n();
    }

    public Set<String> e() {
        return this.i;
    }

    public void f() {
        this.m();
    }

    public void a(EntityPlayer entityplayer, WorldServer worldserver) {
        entityplayer.netServerHandler.sendPacket(new Packet4UpdateTime(worldserver.getTime()));
        if (worldserver.v()) {
            entityplayer.netServerHandler.sendPacket(new Packet70Bed(1));
        }
    }

    public void updateClient(EntityPlayer entityplayer) {
        entityplayer.updateInventory(entityplayer.defaultContainer);
        entityplayer.C();
    }
}
