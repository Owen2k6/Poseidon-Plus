
package net.minecraft.server;

class NetworkMasterThread extends Thread {

    final NetworkManager a;
    private volatile boolean running = true;

    NetworkMasterThread(NetworkManager networkmanager) {
        this.a = networkmanager;
    }

    public void run() {
        try {
            int sleepInterval = 500; // Use smaller intervals for sleep
            for (int i = 0; i < 10 && running; i++) {
                Thread.sleep(sleepInterval);
            }

            // Safely stop threads using interrupts
            if (NetworkManager.g(this.a).isAlive()) {
                try {
                    NetworkManager.g(this.a).interrupt();
                } catch (Throwable ignored) {
                    System.err.println("Failed to interrupt thread: " + ignored.getMessage());
                }
            }

            if (NetworkManager.h(this.a).isAlive()) {
                try {
                    NetworkManager.h(this.a).interrupt();
                } catch (Throwable ignored) {
                    System.err.println("Failed to interrupt thread: " + ignored.getMessage());
                }
            }
        } catch (InterruptedException interruptedexception) {
            Thread.currentThread().interrupt(); // Restore the interrupt status
            interruptedexception.printStackTrace();
        }
    }

    // Method to stop the running thread gracefully
    public void shutdown() {
        running = false;
    }
}
