package net.minecraft.server;

import com.legacyminecraft.poseidon.PoseidonConfig;

class NetworkReaderThread extends Thread {
    private boolean fast; // Poseidon
    final NetworkManager a;

    NetworkReaderThread(NetworkManager networkmanager, String s) {
        super(s);
        this.a = networkmanager;
        this.fast = PoseidonConfig.getInstance().getBoolean("settings.faster-packets.enabled", true); // Poseidon
    }

    public void run() {
        Object object = NetworkManager.a;
        boolean flag = false;  // Restore the flag variable
        synchronized (NetworkManager.a) {
            ++NetworkManager.b;
        }

        // Simplified main loop control and flag handling
        boolean continueRunning = true;
        while (continueRunning) {
            try {
                // Caching the repetitive method calls for efficiency
                boolean managerA = NetworkManager.a(this.a);
                boolean managerB = NetworkManager.b(this.a);

                if (!managerA || managerB) {
                    break;
                }

                while (NetworkManager.c(this.a)) {
                    ;
                }

                try {
                    sleep(this.fast ? 2L : 100L);
                } catch (InterruptedException ignored) {
                    ;
                }
            } finally {
                if (flag) {
                    Object object1 = NetworkManager.a;

                    synchronized (NetworkManager.a) {
                        --NetworkManager.b;
                    }
                }
            }
        }

        synchronized (NetworkManager.a) {
            --NetworkManager.b;
        }
    }
}
