package net.minecraft.server;

import com.legacyminecraft.poseidon.PoseidonConfig;
import com.projectposeidon.johnymuffin.UUIDManager;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class PlayerNBTManager implements PlayerFileData, IDataManager {

    private static final Logger a = Logger.getLogger("Minecraft");
    private final File b;
    private final File c;
    private final File d;
    private final long e = System.currentTimeMillis();
    private UUID uuid = null; // CraftBukkit

    public PlayerNBTManager(File file1, String s, boolean flag) {
        this.b = new File(file1, s);
        this.b.mkdirs();
        this.c = new File(this.b, "players");
        this.d = new File(this.b, "data");
        this.d.mkdirs();
        if (flag) {
            this.c.mkdirs();
        }

        this.f();
    }

    private void f() {
        try {
            File file1 = new File(this.b, "session.lock");

            try (DataOutputStream dataoutputstream = new DataOutputStream(Files.newOutputStream(file1.toPath())))
            {
                dataoutputstream.writeLong(this.e);
            }
        } catch (IOException ioexception) {
            ioexception.printStackTrace(System.err);
            throw new RuntimeException("Failed to check session lock, aborting");
        }
    }

    protected File a() {
        return this.b;
    }

    public void b() {
        try {
            File file1 = new File(this.b, "session.lock");

            try (DataInputStream datainputstream = new DataInputStream(Files.newInputStream(file1.toPath())))
            {
                if (datainputstream.readLong() != this.e)
                {
                    throw new MinecraftException("The save is being accessed from another location, aborting");
                }
            }
        } catch (IOException ioexception) {
            throw new MinecraftException("Failed to check session lock, aborting");
        }
    }

    public IChunkLoader a(WorldProvider worldprovider) {
        if (worldprovider instanceof WorldProviderHell) {
            File file1 = new File(this.b, "DIM-1");

            file1.mkdirs();
            return new ChunkLoader(file1, true);
        } else {
            return new ChunkLoader(this.b, true);
        }
    }

    public WorldData c() {
        File file1 = new File(this.b, "level.dat");
        NBTTagCompound nbttagcompound;
        NBTTagCompound nbttagcompound1;

        if (file1.exists()) {
            try {
                nbttagcompound = CompressedStreamTools.a(Files.newInputStream(file1.toPath()));
                nbttagcompound1 = nbttagcompound.k("Data");
                return new WorldData(nbttagcompound1);
            } catch (Exception exception) {
                exception.printStackTrace(System.err);
            }
        }

        file1 = new File(this.b, "level.dat_old");
        if (file1.exists()) {
            try {
                nbttagcompound = CompressedStreamTools.a(Files.newInputStream(file1.toPath()));
                nbttagcompound1 = nbttagcompound.k("Data");
                return new WorldData(nbttagcompound1);
            } catch (Exception exception1) {
                exception1.printStackTrace(System.err);
            }
        }

        return null;
    }

    public void a(WorldData worlddata, List<EntityHuman> list) {
        NBTTagCompound nbttagcompound = worlddata.a(list);
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        nbttagcompound1.a("Data", (NBTBase) nbttagcompound);

        try {
            File file1 = new File(this.b, "level.dat_new");
            File file2 = new File(this.b, "level.dat_old");
            File file3 = new File(this.b, "level.dat");

            CompressedStreamTools.a(nbttagcompound1, Files.newOutputStream(file1.toPath()));
            if (file2.exists()) {
                if (!file2.delete()) file2.deleteOnExit();
            }

            file3.renameTo(file2);
            if (file3.exists()) {
                if (!file3.delete()) file3.deleteOnExit();
            }

            file1.renameTo(file3);
            if (file1.exists()) {
                if (!file1.delete()) file1.deleteOnExit();
            }
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
    }

    public void a(WorldData worlddata) {
        NBTTagCompound nbttagcompound = worlddata.a();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        nbttagcompound1.a("Data", (NBTBase) nbttagcompound);

        try {
            File file1 = new File(this.b, "level.dat_new");
            File file2 = new File(this.b, "level.dat_old");
            File file3 = new File(this.b, "level.dat");

            CompressedStreamTools.a(nbttagcompound1, Files.newOutputStream(file1.toPath()));
            if (file2.exists()) {
                file2.delete();
            }

            file3.renameTo(file2);
            if (file3.exists()) {
                file3.delete();
            }

            file1.renameTo(file3);
            if (file1.exists()) {
                file1.delete();
            }
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
        }
    }

    public void a(EntityHuman entityhuman) {
        if ((boolean) PoseidonConfig.getInstance().getConfigOption("settings.save-playerdata-by-uuid")) {
            try {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                entityhuman.d(nbttagcompound);
                File file1 = new File(this.c, "_tmp_.dat");
                //File file2 = new File(this.c, entityhuman.name + ".dat");
                //UUIDPlayerStorage.getInstance().getUUIDGraceful(entityhuman.name)
                File file2 = new File(this.c, UUIDManager.getInstance().getUUIDGraceful(entityhuman.name) + ".dat");
                CompressedStreamTools.a(nbttagcompound, Files.newOutputStream(file1.toPath()));
                if (file2.exists()) {
                    file2.delete();
                }

                file1.renameTo(file2);
            } catch (Exception exception) {
                a.warning("Failed to save player data for " + entityhuman.name);
            }
        } else {
            try {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                entityhuman.d(nbttagcompound);
                File file1 = new File(this.c, "_tmp_.dat");
                File file2 = new File(this.c, entityhuman.name + ".dat");

                CompressedStreamTools.a(nbttagcompound, Files.newOutputStream(file1.toPath()));
                if (file2.exists()) {
                    file2.delete();
                }

                file1.renameTo(file2);
            } catch (Exception exception) {
                a.warning("Failed to save player data for " + entityhuman.name);
            }
        }
    }

    public void b(EntityHuman entityhuman) {
        NBTTagCompound nbttagcompound = this.a(entityhuman.name);

        if (nbttagcompound != null) {
            entityhuman.e(nbttagcompound);
        }
    }

    //Credit https://www.journaldev.com/861/java-copy-file
    private static void copyFileUsingStream(File source, File dest) throws IOException {
        try (InputStream is = Files.newInputStream(source.toPath()); OutputStream os = Files.newOutputStream(dest.toPath()))
        {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0)
            {
                os.write(buffer, 0, length);
            }
        }
    }

    public NBTTagCompound a(String s) {
        if ((boolean) PoseidonConfig.getInstance().getConfigOption("settings.save-playerdata-by-uuid")) {
            try {
                File file1 = new File(this.c, UUIDManager.getInstance().getUUIDGraceful(s) + ".dat");
                File file2 = new File(this.c, s + ".dat");
                if (!file1.exists()) {
                    if (file2.exists()) {
                        //Convert player data
                        copyFileUsingStream(file2, file1);
                        File file3 = new File(this.c, s + ".datbackup");
                        file2.renameTo(file3);
                        System.out.println("Converting playerdata for " + s + " to a UUID");
                    }
                }


                if (file1.exists()) {
                    return CompressedStreamTools.a(Files.newInputStream(file1.toPath()));
                }
            } catch (Exception exception) {
                a.warning("Failed to load player data for " + s);
            }

            return null;
        } else {
            try {
                File file1 = new File(this.c, s + ".dat");

                if (file1.exists()) {
                    return CompressedStreamTools.a(Files.newInputStream(file1.toPath()));
                }
            } catch (Exception exception) {
                a.warning("Failed to load player data for " + s);
            }

            return null;
        }

    }

    public PlayerFileData d() {
        return this;
    }

    public void e() {
    }

    public File b(String s) {
        return new File(this.d, s + ".dat");
    }

    // CraftBukkit start
    public UUID getUUID() {
        if (uuid != null) return uuid;
        try {
            File file1 = new File(this.b, "uid.dat");
            if (!file1.exists()) {
                DataOutputStream dos = new DataOutputStream(Files.newOutputStream(file1.toPath()));
                uuid = UUID.randomUUID();
                dos.writeLong(uuid.getMostSignificantBits());
                dos.writeLong(uuid.getLeastSignificantBits());
                dos.close();
            } else {
                DataInputStream dis = new DataInputStream(Files.newInputStream(file1.toPath()));
                uuid = new UUID(dis.readLong(), dis.readLong());
                dis.close();
            }
            return uuid;
        } catch (IOException ex) {
            return null;
        }
    }
    // CraftBukkit end
}
