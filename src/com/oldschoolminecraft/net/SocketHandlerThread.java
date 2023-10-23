package com.oldschoolminecraft.net;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.NetServerHandler;

import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SocketHandler;

public class SocketHandlerThread extends Thread
{
    private static Logger log = Logger.getLogger("Minecraft");
    private Socket socket;
    private final ArrayList<NetLoginHandler> loginHandlers = new ArrayList<>();
    private final ArrayList<NetServerHandler> serverHandlers = new ArrayList<>();
    private MinecraftServer minecraftServer;

    public SocketHandlerThread(MinecraftServer minecraftServer, Socket socket)
    {
        this.minecraftServer = minecraftServer;
        this.socket = socket;
    }

    public void registerLoginHandler(NetLoginHandler loginHandler)
    {
        if (loginHandler == null) throw new IllegalArgumentException("Got null pendingconnection!");
        else loginHandlers.add(loginHandler);
    }

    public void registerServerHandler(NetServerHandler serverHandler)
    {
        serverHandlers.add(serverHandler);
    }

    public void run()
    {
        while (MinecraftServer.isRunning(minecraftServer))
        {
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
            }

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
            }
        }
    }

    public Socket getSocket()
    {
        return socket;
    }
}
