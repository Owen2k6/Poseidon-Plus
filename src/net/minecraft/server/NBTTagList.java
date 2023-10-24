package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NBTTagList extends NBTBase {

    private List a = new ArrayList();
    private byte b;

    public NBTTagList() {}

    void a(DataOutput dataoutput) throws IOException {
        if (!this.a.isEmpty()) {
            this.b = ((NBTBase) this.a.get(0)).a();
        } else {
            this.b = 1;
        }

        dataoutput.writeByte(this.b);
        dataoutput.writeInt(this.a.size());

        for (Object o : this.a) {
            ((NBTBase) o).a(dataoutput);
        }
    }

    void a(DataInput datainput) throws IOException {
        this.b = datainput.readByte();
        int i = datainput.readInt();

        this.a = new ArrayList();

        for (int j = 0; j < i; ++j) {
            NBTBase nbtbase = NBTBase.a(this.b);

            Objects.requireNonNull(nbtbase).a(datainput);
            this.a.add(nbtbase);
        }
    }

    public byte a() {
        return (byte) 9;
    }

    public String toString() {
        return "" + this.a.size() + " entries of type " + NBTBase.b(this.b);
    }

    public void a(NBTBase nbtbase) {
        this.b = nbtbase.a();
        this.a.add(nbtbase);
    }

    public NBTBase a(int i) {
        return (NBTBase) this.a.get(i);
    }

    public int c() {
        return this.a.size();
    }
}
