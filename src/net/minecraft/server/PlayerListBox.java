package net.minecraft.server;

import javax.swing.*;
import java.util.Vector;

public class PlayerListBox extends JList<String> implements IUpdatePlayerListBox {

    private final MinecraftServer a;
    private int b = 0;

    public PlayerListBox(MinecraftServer minecraftserver) {
        this.a = minecraftserver;
        minecraftserver.a(this);
    }

    public void a() {
        if (this.b++ % 20 == 0) {
            Vector<String> vector = new Vector<>();

            for (int i = 0; i < this.a.serverConfigurationManager.players.size(); ++i) {
                vector.add(this.a.serverConfigurationManager.players.get(i).name);
            }

            this.setListData(vector);
        }
    }
}
