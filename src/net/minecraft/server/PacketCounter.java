package net.minecraft.server;

class PacketCounter {

    private int a;
    private long b;

    private PacketCounter() {}

    public void a(int i) {
        ++this.a;
        this.b += i;
    }

    PacketCounter(EmptyClass1 emptyclass1) {
        this();
    }
}
