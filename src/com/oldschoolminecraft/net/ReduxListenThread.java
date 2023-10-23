package com.oldschoolminecraft.net;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkAcceptThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReduxListenThread extends Thread
{
    private static Logger log = Logger.getLogger("Minecraft");
    private final ArrayList<NetLoginHandler> loginHandlers = new ArrayList<>();
    private final ArrayList<NetServerHandler> serverHandlers = new ArrayList<>();
    private MinecraftServer minecraftServer;
    private ServerSocket serverSocket;
    private Thread networkAcceptThread;
    public volatile boolean running;
    public int connectionThreadCount = 0;

    public ReduxListenThread(MinecraftServer minecraftServer, InetAddress address, int port) throws IOException
    {
        this.minecraftServer = minecraftServer;
        this.serverSocket = new ServerSocket(port, 0, address);
        this.running = true;
        this.networkAcceptThread = new NetworkAcceptThread(this, "Listen thread", minecraftServer);
        this.networkAcceptThread.start();

        log.warning("ReduxListenThread created and initialized!");
    }

    public void registerLoginHandler(NetLoginHandler loginHandler)
    {
        if (loginHandler == null) throw new IllegalArgumentException("Got null pendingconnection!");
        else loginHandlers.add(loginHandler);
    }

    public void registerServerHandler(NetServerHandler serverHandler)
    {
        serverHandlers.add(serverHandler);
        log.info("Registered server handler");
    }

    public void run()
    {
        while (MinecraftServer.isRunning(minecraftServer))
        {
            int processedCount = 0;

            for (int i = 0; i < loginHandlers.size(); i++)
            {
                NetLoginHandler loginHandler = this.loginHandlers.get(i);

                try
                {
                    loginHandler.a();
                } catch (Exception ex) {
                    if (loginHandler == null)
                    {
                        log.log(Level.WARNING, "Looks like someone tried to crash the server, stopped their attempt.");
                        this.loginHandlers.remove(i);
                        return;
                    }
                    loginHandler.disconnect("Internal server error");
                    log.log(Level.WARNING, "Failed to handle packet: " + ex, ex);
                }

                if (loginHandler.c)
                    this.loginHandlers.remove(i--);

                loginHandler.networkManager.a();
                processedCount++;
            }

            //if (processedCount > 0) log.info("Listen thread processed " + processedCount + " login handlers.");
            processedCount = 0;

            for (int i = 0; i < serverHandlers.size(); i++)
            {
                NetServerHandler serverHandler = this.serverHandlers.get(i);

                try
                {
                    serverHandler.a();
                } catch (Exception ex) {
                    log.log(Level.WARNING, "Failed to handle packet: " + ex, ex);
                    serverHandler.disconnect("Internal server error");
                }

                if (serverHandler.disconnected)
                    this.serverHandlers.remove(i--);

                serverHandler.networkManager.a();
                processedCount++;
            }

            //if (processedCount > 0) log.info("Listen thread processed " + processedCount + " login handlers.");

            try
            {
                Thread.sleep(10L);
            } catch (Exception ignored) {}
        }
    }

    public ServerSocket getServerSocket()
    {
        return serverSocket;
    }
}
