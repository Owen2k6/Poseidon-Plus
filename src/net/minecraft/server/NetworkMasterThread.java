package net.minecraft.server;

class NetworkMasterThread extends Thread {

    final NetworkManager a;

    NetworkMasterThread(NetworkManager networkmanager) {
        this.a = networkmanager;
    }

    public void run() {
        try {
            Thread.sleep(5000L);
            if (NetworkManager.g(this.a).isAlive()) {
                try {
                    NetworkManager.g(this.a).stop();
                } catch (Throwable ignored) {
                    ;
                }
            }

            if (NetworkManager.h(this.a).isAlive()) {
                try {
                    NetworkManager.h(this.a).stop();
                } catch (Throwable ignored) {
                    ;
                }
            }
        } catch (InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
        }
    }
}
