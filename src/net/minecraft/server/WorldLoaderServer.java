package net.minecraft.server;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class WorldLoaderServer extends WorldLoader {

    public WorldLoaderServer(File file1) {
        super(file1);
    }

    public IDataManager a(String s, boolean flag) {
        return new ServerNBTManager(this.a, s, flag);
    }

    public boolean isConvertable(String s) {
        WorldData worlddata = this.b(s);

        return worlddata != null && worlddata.i() == 0;
    }

    public boolean convert(String s, IProgressUpdate iprogressupdate) {
        iprogressupdate.a(0);
        ArrayList<ChunkFile> arraylist = new ArrayList<>();
        ArrayList<File> arraylist1 = new ArrayList<>();
        ArrayList<ChunkFile> arraylist2 = new ArrayList<>();
        ArrayList<File> arraylist3 = new ArrayList<>();
        File file1 = new File(this.a, s);
        File file2 = new File(file1, "DIM-1");

        System.out.println("Scanning folders...");
        this.a(file1, arraylist, arraylist1);
        if (file2.exists()) {
            this.a(file2, arraylist2, arraylist3);
        }

        int i = arraylist.size() + arraylist2.size() + arraylist1.size() + arraylist3.size();

        System.out.println("Total conversion count is " + i);
        this.a(file1, arraylist, 0, i, iprogressupdate);
        this.a(file2, arraylist2, arraylist.size(), i, iprogressupdate);
        WorldData worlddata = this.b(s);

        worlddata.a(19132);
        IDataManager idatamanager = this.a(s, false);

        idatamanager.a(worlddata);
        this.a(arraylist1, arraylist.size() + arraylist2.size(), i, iprogressupdate);
        if (file2.exists()) {
            this.a(arraylist3, arraylist.size() + arraylist2.size() + arraylist1.size(), i, iprogressupdate);
        }

        return true;
    }

    private void a(File file1, ArrayList<ChunkFile> arraylist, ArrayList<File> arraylist1) {
        ChunkFileFilter chunkfilefilter = new ChunkFileFilter(null);
        ChunkFilenameFilter chunkfilenamefilter = new ChunkFilenameFilter(null);
        File[] afile = file1.listFiles(chunkfilefilter);
        int i = Objects.requireNonNull(afile).length;

        for (File file2 : afile)
        {
            arraylist1.add(file2);
            File[] afile2 = file2.listFiles(chunkfilefilter);
            int k = Objects.requireNonNull(afile2).length;

            for (int l = 0; l < k; ++l)
            {
                File file3 = afile2[l];
                File[] afile4 = file3.listFiles(chunkfilenamefilter);
                File[] afile5 = afile4;
                int i1 = afile4.length;

                for (int j1 = 0; j1 < i1; ++j1)
                {
                    File file4 = afile5[j1];

                    arraylist.add(new ChunkFile(file4));
                }
            }
        }
    }

    private void a(File file1, ArrayList<ChunkFile> arraylist, int i, int j, IProgressUpdate iprogressupdate) {
        Collections.sort(arraylist);
        byte[] abyte = new byte[4096];
        Iterator<ChunkFile> iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            ChunkFile chunkfile = iterator.next();
            int k = chunkfile.b();
            int l = chunkfile.c();
            RegionFile regionfile = RegionFileCache.a(file1, k, l);

            if (!regionfile.c(k & 31, l & 31)) {
                try {
                    DataInputStream datainputstream = new DataInputStream(new GZIPInputStream(Files.newInputStream(chunkfile.a().toPath())));
                    DataOutputStream dataoutputstream = regionfile.b(k & 31, l & 31);
                    boolean flag = false;

                    int i1;

                    while ((i1 = datainputstream.read(abyte)) != -1) {
                        dataoutputstream.write(abyte, 0, i1);
                    }

                    dataoutputstream.close();
                    datainputstream.close();
                } catch (IOException ioexception) {
                    ioexception.printStackTrace(System.err);
                }
            }

            ++i;
            int j1 = (int) Math.round(100.0D * (double) i / (double) j);

            iprogressupdate.a(j1);
        }

        RegionFileCache.a();
    }

    private void a(ArrayList<File> arraylist, int i, int j, IProgressUpdate iprogressupdate) {

        for (File file1 : arraylist)
        {
            File[] afile = file1.listFiles();

            a(Objects.requireNonNull(afile));
            if (!file1.delete()) file1.deleteOnExit();
            ++i;
            int k = (int) Math.round(100.0D * (double) i / (double) j);

            iprogressupdate.a(k);
        }
    }
}
