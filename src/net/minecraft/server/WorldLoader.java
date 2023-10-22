package net.minecraft.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

public class WorldLoader implements Convertable {

    protected final File a;

    public WorldLoader(File file1) {
        if (!file1.exists()) {
            file1.mkdirs();
        }

        this.a = file1;
    }

    public WorldData b(String s) {
        File file1 = new File(this.a, s);

        if (!file1.exists()) {
            return null;
        } else {
            File file2 = new File(file1, "level.dat");
            NBTTagCompound nbttagcompound;
            NBTTagCompound nbttagcompound1;

            if (file2.exists()) {
                try {
                    nbttagcompound = CompressedStreamTools.a(Files.newInputStream(file2.toPath()));
                    nbttagcompound1 = nbttagcompound.k("Data");
                    return new WorldData(nbttagcompound1);
                } catch (Exception exception) {
                    exception.printStackTrace(System.err);
                }
            }

            file2 = new File(file1, "level.dat_old");
            if (file2.exists()) {
                try {
                    nbttagcompound = CompressedStreamTools.a(Files.newInputStream(file2.toPath()));
                    nbttagcompound1 = nbttagcompound.k("Data");
                    return new WorldData(nbttagcompound1);
                } catch (Exception exception1) {
                    exception1.printStackTrace(System.err);
                }
            }

            return null;
        }
    }

    protected static void a(File[] afile) {
        for (File file : afile)
        {
            if (file.isDirectory())
                a(Objects.requireNonNull(file.listFiles()));
            if (!file.delete()) file.deleteOnExit();
        }
    }

    public IDataManager a(String s, boolean flag) {
        return new PlayerNBTManager(this.a, s, flag);
    }

    public boolean isConvertable(String s) {
        return false;
    }

    public boolean convert(String s, IProgressUpdate iprogressupdate) {
        return false;
    }
}
