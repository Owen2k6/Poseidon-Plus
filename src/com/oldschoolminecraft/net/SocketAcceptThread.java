package com.oldschoolminecraft.net;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.NetServerHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SocketAcceptThread extends Thread
{
    private static Logger log = Logger.getLogger("Minecraft");
    private final ServerSocket serverSocket;
    private final MinecraftServer minecraftServer;

    public SocketAcceptThread(MinecraftServer minecraftServer, InetAddress bindAddress, int port) throws IOException
    {
        this.serverSocket = new ServerSocket(port, 0, bindAddress);
        this.serverSocket.setPerformancePreferences(0, 2, 1); //TODO: does this make network performance worse??
        this.minecraftServer = minecraftServer;
    }

    public void run()
    {
        try
        {
            Socket socket;
            while ((socket = serverSocket.accept()) != null)
            {
                new SocketHandlerThread(minecraftServer, socket).start();
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
