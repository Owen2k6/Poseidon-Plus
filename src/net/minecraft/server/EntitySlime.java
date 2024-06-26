package net.minecraft.server;

public class EntitySlime extends EntityLiving implements IMonster {

    public float a;
    public float b;
    private int size = 0;

    public EntitySlime(World world) {
        super(world);
        this.texture = "/mob/slime.png";
        int i = 1 << this.random.nextInt(3);

        this.height = 0.0F;
        this.size = this.random.nextInt(20) + 10;
        this.setSize(i);
    }

    protected void b() {
        super.b();
        this.datawatcher.a(16, (byte) 1);
    }

    public void setSize(int i) {
        this.datawatcher.watch(16, (byte) i);
        this.b(0.6F * (float) i, 0.6F * (float) i);
        this.health = i * i;
        this.setPosition(this.locX, this.locY, this.locZ);
    }

    public int getSize() {
        return this.datawatcher.a(16);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.a("Size", this.getSize() - 1);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setSize(nbttagcompound.e("Size") + 1);
    }

    public void m_() {
        this.b = this.a;
        boolean flag = this.onGround;

        super.m_();
        if (this.onGround && !flag) {
            int i = this.getSize();

            for (int j = 0; j < i * 8; ++j) {
                float f = this.random.nextFloat() * 3.1415927F * 2.0F;
                float f1 = this.random.nextFloat() * 0.5F + 0.5F;
                float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
                float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;

                this.world.a("slime", this.locX + (double) f2, this.boundingBox.b, this.locZ + (double) f3, 0.0D, 0.0D, 0.0D);
            }

            if (i > 2) {
                this.world.makeSound(this, "mob.slime", this.k(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            }

            this.a = -0.5F;
        }

        this.a *= 0.6F;
    }

    protected void c_() {
        this.U();
        EntityHuman entityhuman = this.world.findNearbyPlayer(this, 16.0D);

        if (entityhuman != null) {
            this.a(entityhuman, 10.0F, 20.0F);
        }

        if (this.onGround && this.size-- <= 0) {
            this.size = this.random.nextInt(20) + 10;
            if (entityhuman != null) {
                this.size /= 3;
            }

            this.aC = true;
            if (this.getSize() > 1) {
                this.world.makeSound(this, "mob.slime", this.k(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
            }

            this.a = 1.0F;
            this.az = 1.0F - this.random.nextFloat() * 2.0F;
            this.aA = (float) (this.getSize());
        } else {
            this.aC = false;
            if (this.onGround) {
                this.az = this.aA = 0.0F;
            }
        }
    }

    public void die() {
        int i = this.getSize();

        if (!this.world.isStatic && i > 1 && this.health <= 0) {
            for (int j = 0; j < 4; ++j) {
                float f = ((float) (j % 2) - 0.5F) * (float) i / 4.0F;
                float f1 = ((float) (j / 2) - 0.5F) * (float) i / 4.0F;
                EntitySlime entityslime = new EntitySlime(this.world);

                entityslime.setSize(i / 2);
                entityslime.setPositionRotation(this.locX + (double) f, this.locY + 0.5D, this.locZ + (double) f1, this.random.nextFloat() * 360.0F, 0.0F);
                this.world.addEntity(entityslime);
            }
        }

        super.die();
    }

    public void b(EntityHuman entityhuman) {
        int i = this.getSize();

        if (i > 1 && this.e(entityhuman) && (double) this.f(entityhuman) < 0.6D * (double) i && entityhuman.damageEntity(this, i)) {
            this.world.makeSound(this, "mob.slimeattack", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        }
    }

    protected String h() {
        return "mob.slime";
    }

    protected String i() {
        return "mob.slime";
    }

    protected int j() {
        return this.getSize() == 1 ? Item.SLIME_BALL.id : 0;
    }

    public boolean d() {
        Chunk chunk = this.world.getChunkAtWorldCoords(MathHelper.floor(this.locX), MathHelper.floor(this.locZ));

        return (this.getSize() == 1 || this.world.spawnMonsters > 0) && this.random.nextInt(10) == 0 && chunk.a(987234911L).nextInt(10) == 0 && this.locY < 16.0D;
    }

    protected float k() {
        return 0.6F;
    }
}
