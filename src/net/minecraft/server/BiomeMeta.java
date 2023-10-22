package net.minecraft.server;

public class BiomeMeta {

    public Class<? extends Entity> a;
    public int b;

    public BiomeMeta(Class<? extends Entity> oclass, int i) {
        this.a = oclass;
        this.b = i;
    }
}
