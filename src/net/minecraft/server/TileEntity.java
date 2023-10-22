package net.minecraft.server;

import java.util.HashMap;
import java.util.Map;

public class TileEntity {

    private static final Map<String, Class<? extends TileEntity>> a = new HashMap<>();
    private static final Map<Class<? extends TileEntity>, String> b = new HashMap<>();
    public World world;
    public int x;
    public int y;
    public int z;
    protected boolean h;

    public TileEntity() {}

    private static void a(Class<? extends TileEntity> oclass, String s) {
        if (a.containsKey(s)) {
            throw new IllegalArgumentException("Duplicate id: " + s);
        } else {
            a.put(s, oclass);
            b.put(oclass, s);
        }
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.x = nbttagcompound.e("x");
        this.y = nbttagcompound.e("y");
        this.z = nbttagcompound.e("z");
    }

    public void b(NBTTagCompound nbttagcompound) {
        String s = (String) b.get(this.getClass());

        if (s == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        } else {
            nbttagcompound.setString("id", s);
            nbttagcompound.a("x", this.x);
            nbttagcompound.a("y", this.y);
            nbttagcompound.a("z", this.z);
        }
    }

    public void g_() {}

    public static TileEntity c(NBTTagCompound nbttagcompound) {
        TileEntity tileentity = null;

        try {
            Class<? extends TileEntity> oclass = a.get(nbttagcompound.getString("id"));

            if (oclass != null) {
                tileentity = oclass.getConstructor().newInstance();
            }
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }

        if (tileentity != null) {
            tileentity.a(nbttagcompound);
        } else {
            System.out.println("Skipping TileEntity with id " + nbttagcompound.getString("id"));
        }

        return tileentity;
    }

    public int e() {
        return this.world.getData(this.x, this.y, this.z);
    }

    public void update() {
        if (this.world != null) {
            this.world.b(this.x, this.y, this.z, this);
        }
    }

    public Packet f() {
        return null;
    }

    public boolean g() {
        return this.h;
    }

    public void h() {
        this.h = true;
    }

    public void j() {
        this.h = false;
    }

    static {
        a(TileEntityFurnace.class, "Furnace");
        a(TileEntityChest.class, "Chest");
        a(TileEntityRecordPlayer.class, "RecordPlayer");
        a(TileEntityDispenser.class, "Trap");
        a(TileEntitySign.class, "Sign");
        a(TileEntityMobSpawner.class, "MobSpawner");
        a(TileEntityNote.class, "Music");
        a(TileEntityPiston.class, "Piston");
    }
}
