package net.minecraft.server;

import net.oldschoolminecraft.poseidon.RedstoneTNTIgnitionEvent;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.Random;

public class BlockTNT extends Block {

    public BlockTNT(int i, int j) {
        super(i, j, Material.TNT);
    }

    public int a(int i) {
        return i == 0 ? this.textureId + 2 : (i == 1 ? this.textureId + 1 : this.textureId);
    }

    public void c(World world, int i, int j, int k) {
        super.c(world, i, j, k);
        if (world.isBlockIndirectlyPowered(i, j, k)) {
            RedstoneTNTIgnitionEvent event = new RedstoneTNTIgnitionEvent(world.getWorld().getBlockAt(i, j, k));
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) return;
            this.postBreak(world, i, j, k, 1);
            world.setTypeId(i, j, k, 0);
        }
    }

    /**
     * Applies physics to TNT
     * @param world This one is obvious
     * @param i presumably X
     * @param j presumably Y
     * @param k presumably Z
     * @param l a Block ID
     */
    public void doPhysics(World world, int i, int j, int k, int l) {
        if (l > 0 && Block.byId[l].isPowerSource() && world.isBlockIndirectlyPowered(i, j, k)) {
            this.postBreak(world, i, j, k, 1);
            world.setTypeId(i, j, k, 0);
        }
    }

    public int a(Random random) {
        return 0;
    }

    public void d(World world, int i, int j, int k) {
        EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F));

        entitytntprimed.fuseTicks = world.random.nextInt(entitytntprimed.fuseTicks / 4) + entitytntprimed.fuseTicks / 8;
        world.addEntity(entitytntprimed);
    }

    public void postBreak(World world, int i, int j, int k, int l) {
        if (!world.isStatic) {
            if ((l & 1) == 0) {
                this.a(world, i, j, k, new ItemStack(Block.TNT.id, 1, 0));
            } else {
                EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F));

                if (new File("tnt.debug").exists()) Thread.dumpStack();

                world.addEntity(entitytntprimed);
                world.makeSound(entitytntprimed, "random.fuse", 1.0F, 1.0F);
            }
        }
    }

    public void b(World world, int i, int j, int k, EntityHuman entityhuman) {
        if (entityhuman.G() != null && entityhuman.G().id == Item.FLINT_AND_STEEL.id) {
            world.setRawData(i, j, k, 1);
        }

        super.b(world, i, j, k, entityhuman);
    }

    public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
        return super.interact(world, i, j, k, entityhuman);
    }
}
