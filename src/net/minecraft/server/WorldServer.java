package net.minecraft.server;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.craftbukkit.generator.*;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;

// CraftBukkit start
public class WorldServer extends World implements BlockChangeDelegate {
    // CraftBukkit end

    public ChunkProviderServer chunkProviderServer;
    public boolean weirdIsOpCache = false;
    public boolean canSave;
    public final MinecraftServer server; // CraftBukkit - private -> public final
    private EntityList G = new EntityList();

    // CraftBukkit start - change signature
    public WorldServer(MinecraftServer minecraftserver, IDataManager idatamanager, String s, int i, long j, org.bukkit.World.Environment env, ChunkGenerator gen) {
        super(idatamanager, s, j, WorldProvider.byDimension(env.getId()), gen, env);
        this.server = minecraftserver;

        this.dimension = i;
        this.pvpMode = minecraftserver.pvpMode;
        this.manager = new PlayerManager(minecraftserver, this.dimension, minecraftserver.propertyManager.getInt("view-distance", 10));
    }

    public final int dimension;
    public EntityTracker tracker;
    public PlayerManager manager;
    // CraftBukkit end

    @Override
    public void entityJoinedWorld(Entity entity, boolean flag) {
        /* CraftBukkit start - We prevent spawning in general, so this butchering is not needed
        if (!this.server.spawnAnimals && (entity instanceof EntityAnimal || entity instanceof EntityWaterAnimal)) {
            entity.die();
        }
        // CraftBukkit end */

        if (entity.passenger == null || !(entity.passenger instanceof EntityHuman)) {
            super.entityJoinedWorld(entity, flag);
        }
    }

    public void vehicleEnteredWorld(Entity entity, boolean flag) {
        super.entityJoinedWorld(entity, flag);
    }

    @Override
    protected IChunkProvider b() {
        IChunkLoader ichunkloader = this.w.a(this.worldProvider);

        // CraftBukkit start
        final InternalChunkGenerator gen;
        final long seed = this.getSeed();

        if (this.generator != null) {
            gen = new CustomChunkGenerator(this, seed, this.generator);
        } else if (this.worldProvider instanceof WorldProviderHell) {
            gen = new NetherChunkGenerator(this, seed);
        } else if (this.worldProvider instanceof WorldProviderSky) {
            gen = new SkyLandsChunkGenerator(this, seed);
        } else {
            gen = new NormalChunkGenerator(this, seed);
        }

        this.chunkProviderServer = new ChunkProviderServer(this, ichunkloader, gen);
        // CraftBukkit end

        return this.chunkProviderServer;
    }

    public List getTileEntities(int i, int j, int k, int l, int i1, int j1) {
        // Pre-size to avoid rehashing; most queries touch a small region
        final ArrayList arraylist = new ArrayList(this.c.size());
        final List tileEntities = this.c;
        final int size = tileEntities.size();

        for (int k1 = 0; k1 < size; ++k1) {
            final TileEntity tileentity = (TileEntity) tileEntities.get(k1);

            // Fail-fast: check the axis most likely to exclude first (X, then Z, then Y)
            if (tileentity.x >= i && tileentity.x < l
                    && tileentity.z >= k && tileentity.z < j1
                    && tileentity.y >= j && tileentity.y < i1) {
                arraylist.add(tileentity);
            }
        }

        return arraylist;
    }

    public boolean a(EntityHuman entityhuman, int i, int j, int k) {
        // Use Math.max to resolve the conditional in a single branchless operation
        final int i1 = Math.max(
                (int) MathHelper.abs((float) (i - this.worldData.c())),
                (int) MathHelper.abs((float) (k - this.worldData.e()))
        );

        // CraftBukkit - Configurable spawn protection
        // Cache getServer() to avoid two virtual calls
        final org.bukkit.Server srv = this.getServer();
        return i1 > srv.getSpawnRadius() || this.server.serverConfigurationManager.isOp(entityhuman.name);
    }

    @Override
    protected void c(Entity entity) {
        super.c(entity);
        this.G.a(entity.id, entity);
    }

    @Override
    protected void d(Entity entity) {
        super.d(entity);
        this.G.d(entity.id);
    }

    public Entity getEntity(int i) {
        return (Entity) this.G.a(i);
    }

    public boolean strikeLightning(Entity entity) {
        // CraftBukkit start
        final LightningStrikeEvent lightning = new LightningStrikeEvent(this.getWorld(), (org.bukkit.entity.LightningStrike) entity.getBukkitEntity());
        this.getServer().getPluginManager().callEvent(lightning);

        if (lightning.isCancelled()) {
            return false;
        }

        if (super.strikeLightning(entity)) {
            this.server.serverConfigurationManager.sendPacketNearby(
                    entity.locX, entity.locY, entity.locZ,
                    512.0D, this.dimension, new Packet71Weather(entity));
            // CraftBukkit end
            return true;
        } else {
            return false;
        }
    }

    public void a(Entity entity, byte b0) {
        final Packet38EntityStatus packet38entitystatus = new Packet38EntityStatus(entity.id, b0);

        // CraftBukkit
        this.server.getTracker(this.dimension).sendPacketToEntity(entity, packet38entitystatus);
    }

    @Override
    public Explosion createExplosion(Entity entity, double d0, double d1, double d2, float f, boolean flag) {
        // CraftBukkit start
        final Explosion explosion = super.createExplosion(entity, d0, d1, d2, f, flag);

        if (explosion.wasCanceled) {
            return explosion;
        }

        /* Remove
        explosion.a = flag;
        explosion.a();
        explosion.a(false);
        */
        this.server.serverConfigurationManager.sendPacketNearby(
                d0, d1, d2, 64.0D, this.dimension,
                new Packet60Explosion(d0, d1, d2, f, explosion.blocks));
        // CraftBukkit end
        return explosion;
    }

    @Override
    public void playNote(int i, int j, int k, int l, int i1) {
        super.playNote(i, j, k, l, i1);
        // CraftBukkit
        this.server.serverConfigurationManager.sendPacketNearby(
                (double) i, (double) j, (double) k, 64.0D, this.dimension,
                new Packet54PlayNoteBlock(i, j, k, l, i1));
    }

    public void saveLevel() {
        this.w.e();
    }

    @Override
    protected void i() {
        final boolean flag = this.v();

        super.i();

        if (flag != this.v()) {
            // CraftBukkit start - only sending weather packets to those affected
            // Cache list reference and size to avoid repeated field reads and size() calls
            final List players = this.players;
            final int size = players.size();
            final Packet70Bed packet = new Packet70Bed(flag ? 2 : 1);

            for (int i = 0; i < size; ++i) {
                final EntityPlayer ep = (EntityPlayer) players.get(i);
                if (ep.world == this) {
                    ep.netServerHandler.sendPacket(packet);
                }
            }
            // CraftBukkit end
        }
    }

    // Poseidon
    public PlayerManager getPlayerManager() {
        return this.manager;
    }
}