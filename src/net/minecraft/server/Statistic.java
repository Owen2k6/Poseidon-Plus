package net.minecraft.server;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Statistic {

    public final int e;
    public final String f;
    public boolean g;
    public String h;
    private static final NumberFormat b = NumberFormat.getIntegerInstance(Locale.US);
    public static final Counter i = new UnknownCounter();
    private static final DecimalFormat c = new DecimalFormat("########0.00");
    public static Counter j = new TimeCounter();
    public static Counter k = new DistancesCounter();

    public Statistic(int i, String s, Counter counter) {
        this.g = false;
        this.e = i;
        this.f = s;
    }

    public Statistic(int i, String s) {
        this(i, s, Statistic.i);
    }

    public Statistic e() {
        this.g = true;
        return this;
    }

    public Statistic d() {
        if (StatisticList.a.containsKey(this.e)) {
            throw new RuntimeException("Duplicate stat id: \"" + StatisticList.a.get(this.e).f + "\" and \"" + this.f + "\" at id " + this.e);
        } else {
            StatisticList.b.add(this);
            StatisticList.a.put(this.e, this);
            this.h = AchievementMap.a(this.e);
            return this;
        }
    }

    public String toString() {
        return this.f;
    }
}
