package net.minecraft.server;

import com.legacyminecraft.poseidon.PoseidonConfig;

import java.util.Random;

public class BlockSand extends Block {

    public static boolean instaFall = false;

    public BlockSand(int i, int j) {
        super(i, j, Material.SAND);
    }

    public void c(World world, int i, int j, int k) {
        world.c(i, j, k, this.id, this.c());
    }

    public void doPhysics(World world, int i, int j, int k, int l) {
        world.c(i, j, k, this.id, this.c());
    }

    public void a(World world, int i, int j, int k, Random random) {
        this.g(world, i, j, k);
    }

    private void g(World world, int i, int j, int k) {
        if (c_(world, i, j - 1, k) && j >= 0) {
            byte b0 = 32;

            if (!instaFall && world.a(i - b0, j - b0, k - b0, i + b0, j + b0, k + b0)) {
                if (PoseidonConfig.getInstance().getConfigBoolean("world.settings.pistons.sand-gravel-duping-fix.enabled")) {
                    world.setTypeId(i, j, k, 0);
                }
                EntityFallingSand entityfallingsand = new EntityFallingSand(world, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), this.id);

                world.addEntity(entityfallingsand);
            } else {
                world.setTypeId(i, j, k, 0);

                while (c_(world, i, j - 1, k) && j > 0) {
                    --j;
                }

                if (j > 0) {
                    world.setTypeId(i, j, k, this.id);
                }
            }
        }
    }

    public int c() {
        return 3;
    }

    public static boolean c_(World world, int i, int j, int k) {
        int l = world.getTypeId(i, j, k);

        if (l == 0) {
            return true;
        } else if (l == Block.FIRE.id) {
            return true;
        } else {
            Material material = Block.byId[l].material;

            return material == Material.WATER || material == Material.LAVA;
        }
    }
}
