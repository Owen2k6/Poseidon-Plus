package net.minecraft.server;

import com.oldschoolminecraft.net.ReduxListenThread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

public class NetworkAcceptThread extends Thread {

    final MinecraftServer minecraftServer;

    final ReduxListenThread reduxListenThread;

    public NetworkAcceptThread(ReduxListenThread reduxListenThread, String threadName, MinecraftServer minecraftserver) {
        super(threadName);
        this.reduxListenThread = reduxListenThread;
        this.minecraftServer = minecraftserver;
    }

    public void run() {
        HashMap<InetAddress, Long> hashmap = new HashMap<>();

        while (this.reduxListenThread.running) {
            try {
                Socket socket = reduxListenThread.getServerSocket().accept();

                if (socket != null) {
                    InetAddress inetaddress = socket.getInetAddress();
                    System.out.println("Inbound connection accepted from " + inetaddress.getHostAddress());

                    if (hashmap.containsKey(inetaddress) && !"127.0.0.1".equals(inetaddress.getHostAddress()) && System.currentTimeMillis() - hashmap.get(inetaddress) < 5000L) {
                        hashmap.put(inetaddress, System.currentTimeMillis());
                        socket.close();
                        System.out.println("Inbound connection closed");
                    } else {
                        hashmap.put(inetaddress, System.currentTimeMillis());
                        NetLoginHandler netloginhandler = new NetLoginHandler(this.minecraftServer, socket, "Connection #" + ++reduxListenThread.connectionThreadCount);

                        reduxListenThread.registerLoginHandler(netloginhandler);
                        System.out.println("Inbound connection login handler registered");
                    }
                }
            } catch (IOException ioexception) {
                ioexception.printStackTrace(System.err);
            }
        }
    }
}
