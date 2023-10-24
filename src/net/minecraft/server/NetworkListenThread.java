package net.minecraft.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkListenThread {

    public static Logger a = Logger.getLogger("Minecraft");
    private ServerSocket d;
    private Thread e;
    public volatile boolean b = false; // i assume this is the "connected" or "running" flag
    public int f = 0; // i believe this is the #/count of network listen threads
    private ArrayList g = new ArrayList();
    private ArrayList h = new ArrayList();
    public MinecraftServer c;

    public NetworkListenThread(MinecraftServer minecraftserver, InetAddress inetaddress, int i) throws IOException {
        this.c = minecraftserver;
        this.d = new ServerSocket(i, 0, inetaddress);
        this.d.setPerformancePreferences(0, 2, 1);
        this.b = true;
        this.e = new NetworkAcceptThread(this, "Listen thread", minecraftserver);
        this.e.start();
    }

    public void a(NetServerHandler netserverhandler) {
        this.h.add(netserverhandler);
    }

    public void a(NetLoginHandler netloginhandler) {
        if (netloginhandler == null) {
            throw new IllegalArgumentException("Got null pendingconnection!");
        } else {
            this.g.add(netloginhandler);
        }
    }

    public void a() {
        int i;

        for (i = 0; i < this.g.size(); ++i) {
            NetLoginHandler netloginhandler = (NetLoginHandler) this.g.get(i);

            try {
                netloginhandler.a();
            } catch (Exception exception) {
                if (netloginhandler == null) {
                    a.log(Level.WARNING, "Looks like someone tried to crash the server, stopped their attempt.");
                    this.g.remove(i);
                    return;
                } else {
                    netloginhandler.disconnect("Internal server error");
                    a.log(Level.WARNING, "Failed to handle packet: " + exception, exception);
                }
            }

            if (netloginhandler.c) {
                this.g.remove(i--);
            }

            netloginhandler.networkManager.a();
        }

        for (i = 0; i < this.h.size(); ++i) {
            NetServerHandler netserverhandler = (NetServerHandler) this.h.get(i);

            try {
                netserverhandler.a();
            } catch (Exception exception1) {
                a.log(Level.WARNING, "Failed to handle packet: " + exception1, exception1);
                netserverhandler.disconnect("Internal server error");
            }

            if (netserverhandler.disconnected) {
                this.h.remove(i--);
            }

            netserverhandler.networkManager.a();
        }
    }

    public ServerSocket getServerSocket()
    {
        return d;
    }

    static ServerSocket a(NetworkListenThread networklistenthread) {
        return networklistenthread.d;
    }

    /**
     * i believe this function increases the count/# of network listen threads, but it does it statically for no good reason as far as i can tell
     * @param networklistenthread
     * @return
     */
    static int b(NetworkListenThread networklistenthread) {
        return networklistenthread.f++;
    }

    static void a(NetworkListenThread networklistenthread, NetLoginHandler netloginhandler) {
        networklistenthread.a(netloginhandler);
    }
}
