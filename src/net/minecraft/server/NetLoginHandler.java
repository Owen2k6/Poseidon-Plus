package net.minecraft.server;

import com.legacyminecraft.poseidon.PlusConfig;
import com.projectposeidon.ConnectionType;
import com.legacyminecraft.poseidon.PoseidonConfig;
import com.projectposeidon.johnymuffin.LoginProcessHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftServer;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static com.legacyminecraft.poseidon.util.Release2Beta.deserializeAddress;

public class NetLoginHandler extends NetHandler {

    public static Logger a = Logger.getLogger("Minecraft");
    private static Random d = new Random();
    public NetworkManager networkManager;
    public boolean c = false;
    private MinecraftServer server;
    private int f = 0;
    private String g = null;
    private Packet1Login h = null;
    private String serverId = "";
    private ConnectionType connectionType;
    private boolean usingReleaseToBeta = false; //Poseidon -> Release2Beta support
    private boolean receivedLoginPacket = false;
    private int rawConnectionType;
    private boolean receivedKeepAlive = false;

    private final String msgKickShutdown;

    public NetLoginHandler(MinecraftServer minecraftserver, Socket socket, String s) {
        this.msgKickShutdown = PlusConfig.getInstance().getString("messages.kick.shutdown", "&cServer is shutting down. Please rejoin later.");
        this.server = minecraftserver;
        this.networkManager = new NetworkManager(socket, s, this);
        this.networkManager.f = 0;
    }

    // CraftBukkit start
    public Socket getSocket() {
        return this.networkManager.socket;
    }
    // CraftBukkit end

    public void a() {
        if (this.h != null) {
            this.b(this.h);
            this.h = null;
        }

        if (this.f++ == 600) {
            this.disconnect("Took too long to log in");
        } else {
            this.networkManager.b();
        }
    }

    public void disconnect(String s) {
        try {
            a.info("Disconnecting " + this.b() + ": " + s);
            this.networkManager.queue(new Packet255KickDisconnect(s));
            this.networkManager.d();
            this.c = true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void a(Packet2Handshake packet2handshake) {
        if (this.server.onlineMode) {
            this.serverId = Long.toHexString(d.nextLong());
            this.networkManager.queue(new Packet2Handshake(this.serverId));
        } else {
            this.networkManager.queue(new Packet2Handshake("-"));
        }
    }

    public void a(Packet0KeepAlive packet0KeepAlive) {
        receivedKeepAlive = true;
    }

    public void a(Packet1Login packet1login) {
        if (receivedLoginPacket) {
            this.disconnect("Multiple login packets received.");
            return;
        }
        receivedLoginPacket = true;
        this.g = packet1login.name;
        if (packet1login.a != 14) {
            if (packet1login.a > 14) {
                this.disconnect("Outdated server! I'm still on Beta 1.7.3");
            } else {
                this.disconnect("Outdated client! Please use Beta 1.7.3");
            }
        } else {
            //Project Poseidon - Start (Release2Beta)
            List<Byte> legacyMagicHeaders = Arrays.asList((byte) -999, (byte) 26, (byte) 1, (byte) 2);
            boolean ipForwardingEnabled = (boolean) PoseidonConfig.getInstance().getConfigOption("settings.bit-flags.enable");
            String allowedProxy = String.valueOf(PoseidonConfig.getInstance().getConfigOption("settings.bit-flags.allowed-proxy", "127.0.0.1"));
            boolean allowedOnly = (boolean) PoseidonConfig.getInstance().getConfigOption("settings.bit-flags.allowed-only");
            boolean useMagicHeaders = (boolean) PoseidonConfig.getInstance().getConfigOption("settings.bit-flags.magic-headers");
            boolean kickUnforwardedPlayers = (boolean) PoseidonConfig.getInstance().getConfigOption("settings.bit-flags.kick-unforwarded", false);

            // the order of this must be in reverse in order to make sense
            boolean[] bits = getBits(packet1login.d);
            boolean isIPForwarded = bits[0] // first bit reserved for IP forward indicator
                                       || (useMagicHeaders && legacyMagicHeaders.contains(packet1login.d));

            // if forwarding is enabled, check to see if the IP is forwarded
            // if the IP is not forwarded, but forwarding is enabled, kick the user.
            // if it's not enabled, but the IP is still trying to be forwarded, kick the user.

            if (ipForwardingEnabled)
            {
                if (!isIPForwarded && kickUnforwardedPlayers)
                {
                    a.info(packet1login.name + " is not forwarding their IP, despite it being enabled. They have been kicked.");
                    this.disconnect(ChatColor.RED + "This server has IP forwarding enabled. Please enable it on your proxy.");
                    return;
                }

                InetSocketAddress address = deserializeAddress(packet1login.c);
                String forwardedIP = address.getAddress().getHostAddress();
                String proxyIP = this.networkManager.socket.getInetAddress().getHostAddress();

                if (allowedOnly && isIPForwarded && !(proxyIP.equals(allowedProxy.trim()) || proxyIP.equals("127.0.0.1"))) // localhost is always allowed
                {
                    a.info(packet1login.name + " is not using the required proxy for IP forwarding. They have been kicked.");
                    this.disconnect(ChatColor.RED + "The proxy you are using is not authorized for this server");
                    return;
                }

                if (isIPForwarded)
                {
                    a.info(packet1login.name + " is using IP forwarding from an ambiguous compatible proxy: " + forwardedIP);
                    this.networkManager.setSocketAddress(address);
                    this.usingReleaseToBeta = true; //TODO: rename variable?
                }
            } else if (isIPForwarded) {
                a.info(packet1login.name + " is forwarding their IP, despite it being disabled. They have been kicked.");
                this.disconnect(ChatColor.RED + "This server has IP forwarding disabled. Please connect directly.");
                return;
            }
            connectionType = ConnectionType.NORMAL;
            //Project Poseidon - End (Release2Beta

            if (((CraftServer) Bukkit.getServer()).isShuttingdown()) {
                this.disconnect(this.msgKickShutdown);
                return;
            }


            new LoginProcessHandler(this, packet1login, this.server.server, this.server.onlineMode);
            // (new ThreadLoginVerifier(this, packet1login, this.server.server)).start(); // CraftBukkit
//            }
        }
    }

    private static boolean[] getBits(byte b)
    {
        return new boolean[] {
                (b & 0x80) != 0,
                (b & 0x40) != 0,
                (b & 0x20) != 0,
                (b & 0x10) != 0,
                (b & 0x8) != 0,
                (b & 0x4) != 0,
                (b & 0x2) != 0,
                (b & 0x1) != 0,
        };
    }

    public void b(Packet1Login packet1login) {
        EntityPlayer entityplayer = this.server.serverConfigurationManager.a(this, packet1login.name);

        if (entityplayer != null) {
            this.server.serverConfigurationManager.b(entityplayer);
            // entityplayer.a((World) this.server.a(entityplayer.dimension)); // CraftBukkit - set by Entity
            // CraftBukkit - add world and location to 'logged in' message.
            a.info(this.b() + " logged in with entity id " + entityplayer.id + " at ([" + entityplayer.world.worldData.name + "] " + entityplayer.locX + ", " + entityplayer.locY + ", " + entityplayer.locZ + ")");
            WorldServer worldserver = (WorldServer) entityplayer.world; // CraftBukkit
            ChunkCoordinates chunkcoordinates = worldserver.getSpawn();
            NetServerHandler netserverhandler = new NetServerHandler(this.server, this.networkManager, entityplayer);
            //Poseidon Start
            netserverhandler.setUsingReleaseToBeta(usingReleaseToBeta);
            netserverhandler.setConnectionType(connectionType);
            netserverhandler.setRawConnectionType(rawConnectionType);
            netserverhandler.setReceivedKeepAlive(receivedKeepAlive);
            //Poseidon End
            netserverhandler.sendPacket(new Packet1Login("Poseidon", entityplayer.id, worldserver.getSeed(), (byte) worldserver.worldProvider.dimension));
            netserverhandler.sendPacket(new Packet6SpawnPosition(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z));
            this.server.serverConfigurationManager.a(entityplayer, worldserver);
            // this.server.serverConfigurationManager.sendAll(new Packet3Chat("\u00A7e" + entityplayer.name + " joined the game."));  // CraftBukkit - message moved to join event
            this.server.serverConfigurationManager.c(entityplayer);
            netserverhandler.a(entityplayer.locX, entityplayer.locY, entityplayer.locZ, entityplayer.yaw, entityplayer.pitch);
            this.server.networkListenThread.a(netserverhandler);
            netserverhandler.sendPacket(new Packet4UpdateTime(entityplayer.getPlayerTime())); // CraftBukkit - add support for player specific time
            entityplayer.syncInventory();
            // poseidon start
            if (PoseidonConfig.getInstance().getBoolean("settings.support.modloader.enable", false)) {
                net.minecraft.server.ModLoaderMp.HandleAllLogins(entityplayer);
            }
            // poseidon end
        }

        this.c = true;
    }

    public void a(String s, Object[] aobject) {
//        a.info(this.b() + " lost connection");
        this.c = true;
    }

    public void a(Packet packet) {
        this.disconnect("Protocol error");
    }

    public String b() {
        return this.g != null ? this.g + " [" + this.networkManager.getSocketAddress().toString() + "]" : this.networkManager.getSocketAddress().toString();
    }

    //This can and will return null for multiple packets.
    public String getUsername() {
        return this.g;
    }

    public boolean c() {
        return true;
    }

    /**
     * @author moderator_man
     * @returns the session id for this player
     */
    public String getServerID() {
        return serverId;
    }

    static String a(NetLoginHandler netloginhandler) {
        return netloginhandler.serverId;
    }

    public static Packet1Login a(NetLoginHandler netloginhandler, Packet1Login packet1login) {
        return netloginhandler.h = packet1login;
    }
}