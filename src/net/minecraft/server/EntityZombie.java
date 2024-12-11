package net.minecraft.server;

import org.bukkit.event.entity.EntityCombustEvent;

public class EntityZombie extends EntityMonster {

    public EntityZombie(World world) {
        super(world);
        this.texture = "/mob/zombie.png";
        this.aE = 0.2F;
        this.damage = 6;
    }

    public void v() {
        if (this.world.d()) {
            float f = this.c(1.0F);
        }

        super.v();
    }

    protected String g() {
        return "mob.zombie";
    }

    protected String h() {
        return "mob.zombiehurt";
    }

    protected String i() {
        return "mob.zombiedeath";
    }

    protected int j() {
        int i =  this.random.nextInt(25);
        if (i == 0) {
            return Item.IRON_INGOT.id;
        } else if (i == 1) {
            return Item.ARROW.id;
        } else if (i == 2) {
            return Item.IRON_INGOT.id;
        } else if (i == 3) {
            return Item.SULPHUR.id;
        } else if (i == 4) {
            return Item.STICK.id;
        } else if (i <= 5 || i >= 10 ){
            return Block.DIRT.id;
        }
        else {
            return Item.FEATHER.id;
        }
    }
}
