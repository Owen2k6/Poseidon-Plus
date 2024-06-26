package net.minecraft.server;

import java.util.*;

public class StatisticList {

    protected static final Map<Integer, Statistic> a = new HashMap<>();
    public static final List<Statistic> b = new ArrayList<>();
    public static final List<CounterStatistic> c = new ArrayList<>();
    public static final List<Statistic> d = new ArrayList<>();
    public static final List<Statistic> e = new ArrayList<>();
    public static Statistic f = (new CounterStatistic(1000, StatisticCollector.a("stat.startGame"))).e().d();
    public static Statistic g = (new CounterStatistic(1001, StatisticCollector.a("stat.createWorld"))).e().d();
    public static Statistic h = (new CounterStatistic(1002, StatisticCollector.a("stat.loadWorld"))).e().d();
    public static Statistic i = (new CounterStatistic(1003, StatisticCollector.a("stat.joinMultiplayer"))).e().d();
    public static Statistic j = (new CounterStatistic(1004, StatisticCollector.a("stat.leaveGame"))).e().d();
    public static Statistic k = (new CounterStatistic(1100, StatisticCollector.a("stat.playOneMinute"), Statistic.j)).e().d();
    public static Statistic l = (new CounterStatistic(2000, StatisticCollector.a("stat.walkOneCm"), Statistic.k)).e().d();
    public static Statistic m = (new CounterStatistic(2001, StatisticCollector.a("stat.swimOneCm"), Statistic.k)).e().d();
    public static Statistic n = (new CounterStatistic(2002, StatisticCollector.a("stat.fallOneCm"), Statistic.k)).e().d();
    public static Statistic o = (new CounterStatistic(2003, StatisticCollector.a("stat.climbOneCm"), Statistic.k)).e().d();
    public static Statistic p = (new CounterStatistic(2004, StatisticCollector.a("stat.flyOneCm"), Statistic.k)).e().d();
    public static Statistic q = (new CounterStatistic(2005, StatisticCollector.a("stat.diveOneCm"), Statistic.k)).e().d();
    public static Statistic r = (new CounterStatistic(2006, StatisticCollector.a("stat.minecartOneCm"), Statistic.k)).e().d();
    public static Statistic s = (new CounterStatistic(2007, StatisticCollector.a("stat.boatOneCm"), Statistic.k)).e().d();
    public static Statistic t = (new CounterStatistic(2008, StatisticCollector.a("stat.pigOneCm"), Statistic.k)).e().d();
    public static Statistic u = (new CounterStatistic(2010, StatisticCollector.a("stat.jump"))).e().d();
    public static Statistic v = (new CounterStatistic(2011, StatisticCollector.a("stat.drop"))).e().d();
    public static Statistic w = (new CounterStatistic(2020, StatisticCollector.a("stat.damageDealt"))).d();
    public static Statistic x = (new CounterStatistic(2021, StatisticCollector.a("stat.damageTaken"))).d();
    public static Statistic y = (new CounterStatistic(2022, StatisticCollector.a("stat.deaths"))).d();
    public static Statistic z = (new CounterStatistic(2023, StatisticCollector.a("stat.mobKills"))).d();
    public static Statistic A = (new CounterStatistic(2024, StatisticCollector.a("stat.playerKills"))).d();
    public static Statistic B = (new CounterStatistic(2025, StatisticCollector.a("stat.fishCaught"))).d();
    public static Statistic[] C = a("stat.mineBlock", 16777216);
    public static Statistic[] D;
    public static Statistic[] E;
    public static Statistic[] F;
    private static boolean G;
    private static boolean H;

    public StatisticList() {}

    public static void a() {}

    public static void b() {
        E = a(E, "stat.useItem", 16908288, 0, Block.byId.length);
        F = b(F, "stat.breakItem", 16973824, 0, Block.byId.length);
        G = true;
        d();
    }

    public static void c() {
        E = a(E, "stat.useItem", 16908288, Block.byId.length, 32000);
        F = b(F, "stat.breakItem", 16973824, Block.byId.length, 32000);
        H = true;
        d();
    }

    public static void d() {
        if (G && H) {
            HashSet<Integer> hashset = new HashSet<>();

            for (CraftingRecipe craftingrecipe : CraftingManager.getInstance().b())
            {
                hashset.add(craftingrecipe.b().id);
            }

            for (ItemStack itemstack : FurnaceRecipes.getInstance().b().values())
            {
                hashset.add(itemstack.id);
            }

            D = new Statistic[32000];

            for (Integer integer : hashset)
            {
                if (Item.byId[integer] != null)
                {
                    String s = StatisticCollector.a("stat.craftItem", Item.byId[integer].j());

                    D[integer] = (new CraftingStatistic(16842752 + integer, s, integer)).d();
                }
            }

            a(D);
        }
    }

    private static Statistic[] a(String s, int i) {
        Statistic[] astatistic = new Statistic[256];

        for (int j = 0; j < 256; ++j) {
            if (Block.byId[j] != null && Block.byId[j].m()) {
                String s1 = StatisticCollector.a(s, Block.byId[j].k());

                astatistic[j] = (new CraftingStatistic(i + j, s1, j)).d();
                e.add(astatistic[j]);
            }
        }

        a(astatistic);
        return astatistic;
    }

    private static Statistic[] a(Statistic[] astatistic, String s, int i, int j, int k) {
        if (astatistic == null) {
            astatistic = new Statistic[32000];
        }

        for (int l = j; l < k; ++l) {
            if (Item.byId[l] != null) {
                String s1 = StatisticCollector.a(s, Item.byId[l].j());

                astatistic[l] = (new CraftingStatistic(i + l, s1, l)).d();
                if (l >= Block.byId.length) {
                    d.add(astatistic[l]);
                }
            }
        }

        a(astatistic);
        return astatistic;
    }

    private static Statistic[] b(Statistic[] astatistic, String s, int i, int j, int k) {
        if (astatistic == null) {
            astatistic = new Statistic[32000];
        }

        for (int l = j; l < k; ++l) {
            if (Item.byId[l] != null && Item.byId[l].f()) {
                String s1 = StatisticCollector.a(s, Item.byId[l].j());

                astatistic[l] = (new CraftingStatistic(i + l, s1, l)).d();
            }
        }

        a(astatistic);
        return astatistic;
    }

    private static void a(Statistic[] astatistic) {
        a(astatistic, Block.STATIONARY_WATER.id, Block.WATER.id);
        a(astatistic, Block.STATIONARY_LAVA.id, Block.STATIONARY_LAVA.id);
        a(astatistic, Block.JACK_O_LANTERN.id, Block.PUMPKIN.id);
        a(astatistic, Block.BURNING_FURNACE.id, Block.FURNACE.id);
        a(astatistic, Block.GLOWING_REDSTONE_ORE.id, Block.REDSTONE_ORE.id);
        a(astatistic, Block.DIODE_ON.id, Block.DIODE_OFF.id);
        a(astatistic, Block.REDSTONE_TORCH_ON.id, Block.REDSTONE_TORCH_OFF.id);
        a(astatistic, Block.RED_MUSHROOM.id, Block.BROWN_MUSHROOM.id);
        a(astatistic, Block.DOUBLE_STEP.id, Block.STEP.id);
        a(astatistic, Block.GRASS.id, Block.DIRT.id);
        a(astatistic, Block.SOIL.id, Block.DIRT.id);
    }

    private static void a(Statistic[] astatistic, int i, int j) {
        if (astatistic[i] != null && astatistic[j] == null) {
            astatistic[j] = astatistic[i];
        } else {
            b.remove(astatistic[i]);
            e.remove(astatistic[i]);
            c.remove(astatistic[i]);
            astatistic[i] = astatistic[j];
        }
    }

    static {
        AchievementList.a();
        G = false;
        H = false;
    }
}
