package net.minecraft.server;

import java.util.Random;

public class ChunkProviderHell implements IChunkProvider {

    private Random h;
    private NoiseGeneratorOctaves i;
    private NoiseGeneratorOctaves j;
    private NoiseGeneratorOctaves k;
    private NoiseGeneratorOctaves l;
    private NoiseGeneratorOctaves m;
    public NoiseGeneratorOctaves a;
    public NoiseGeneratorOctaves b;
    private World n;
    private double[] o;
    private double[] p = new double[256];
    private double[] q = new double[256];
    private double[] r = new double[256];
    private MapGenBase s = new MapGenCavesHell();
    double[] c;
    double[] d;
    double[] e;
    double[] f;
    double[] g;

    public ChunkProviderHell(World world, long i) {
        this.n = world;
        this.h = new Random(i);
        this.i = new NoiseGeneratorOctaves(this.h, 16);
        this.j = new NoiseGeneratorOctaves(this.h, 16);
        this.k = new NoiseGeneratorOctaves(this.h, 8);
        this.l = new NoiseGeneratorOctaves(this.h, 4);
        this.m = new NoiseGeneratorOctaves(this.h, 4);
        this.a = new NoiseGeneratorOctaves(this.h, 10);
        this.b = new NoiseGeneratorOctaves(this.h, 16);
    }

    public void a(int i, int j, byte[] abyte) {
        byte b0 = 4;
        byte b1 = 32;
        int k = b0 + 1;
        byte b2 = 17;
        int l = b0 + 1;

        this.o = this.a(this.o, i * b0, 0, j * b0, k, b2, l);

        for (int i1 = 0; i1 < b0; ++i1) {
            for (int j1 = 0; j1 < b0; ++j1) {
                for (int k1 = 0; k1 < 16; ++k1) {
                    double d0 = 0.125D;
                    double d1 = this.o[((i1) * l + j1) * b2 + k1];
                    double d2 = this.o[((i1) * l + j1 + 1) * b2 + k1];
                    double d3 = this.o[((i1 + 1) * l + j1) * b2 + k1];
                    double d4 = this.o[((i1 + 1) * l + j1 + 1) * b2 + k1];
                    double d5 = (this.o[((i1) * l + j1) * b2 + k1 + 1] - d1) * d0;
                    double d6 = (this.o[((i1) * l + j1 + 1) * b2 + k1 + 1] - d2) * d0;
                    double d7 = (this.o[((i1 + 1) * l + j1) * b2 + k1 + 1] - d3) * d0;
                    double d8 = (this.o[((i1 + 1) * l + j1 + 1) * b2 + k1 + 1] - d4) * d0;

                    for (int l1 = 0; l1 < 8; ++l1) {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;

                        for (int i2 = 0; i2 < 4; ++i2) {
                            int j2 = i2 + i1 * 4 << 11 | 0 + j1 * 4 << 7 | k1 * 8 + l1;
                            short short1 = 128;
                            double d14 = 0.25D;
                            double d15 = d10;
                            double d16 = (d11 - d10) * d14;

                            for (int k2 = 0; k2 < 4; ++k2) {
                                int l2 = 0;

                                if (k1 * 8 + l1 < b1) {
                                    l2 = Block.STATIONARY_LAVA.id;
                                }

                                if (d15 > 0.0D) {
                                    l2 = Block.NETHERRACK.id;
                                }

                                abyte[j2] = (byte) l2;
                                j2 += short1;
                                d15 += d16;
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    public void b(int i, int j, byte[] abyte) {
        byte b0 = 64;
        double d0 = 0.03125D;

        this.p = this.l.a(this.p, (double) (i * 16), (double) (j * 16), 0.0D, 16, 16, 1, d0, d0, 1.0D);
        this.q = this.l.a(this.q, (double) (i * 16), 109.0134D, (double) (j * 16), 16, 1, 16, d0, 1.0D, d0);
        this.r = this.m.a(this.r, (double) (i * 16), (double) (j * 16), 0.0D, 16, 16, 1, d0 * 2.0D, d0 * 2.0D, d0 * 2.0D);

        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                boolean flag = this.p[k + l * 16] + this.h.nextDouble() * 0.2D > 0.0D;
                boolean flag1 = this.q[k + l * 16] + this.h.nextDouble() * 0.2D > 0.0D;
                int i1 = (int) (this.r[k + l * 16] / 3.0D + 3.0D + this.h.nextDouble() * 0.25D);
                int j1 = -1;
                byte b1 = (byte) Block.NETHERRACK.id;
                byte b2 = (byte) Block.NETHERRACK.id;

                for (int k1 = 127; k1 >= 0; --k1) {
                    int l1 = (l * 16 + k) * 128 + k1;

                    if (k1 >= 127 - this.h.nextInt(5)) {
                        abyte[l1] = (byte) Block.BEDROCK.id;
                    } else if (k1 <= this.h.nextInt(5)) {
                        abyte[l1] = (byte) Block.BEDROCK.id;
                    } else {
                        byte b3 = abyte[l1];

                        if (b3 == 0) {
                            j1 = -1;
                        } else if (b3 == Block.NETHERRACK.id) {
                            if (j1 == -1) {
                                if (i1 <= 0) {
                                    b1 = 0;
                                    b2 = (byte) Block.NETHERRACK.id;
                                } else if (k1 >= b0 - 4 && k1 <= b0 + 1) {
                                    b1 = (byte) Block.NETHERRACK.id;
                                    b2 = (byte) Block.NETHERRACK.id;
                                    if (flag1) {
                                        b1 = (byte) Block.GRAVEL.id;
                                    }

                                    if (flag) {
                                        b1 = (byte) Block.SOUL_SAND.id;
                                    }

                                    if (flag) {
                                        b2 = (byte) Block.SOUL_SAND.id;
                                    }
                                }

                                if (k1 < b0 && b1 == 0) {
                                    b1 = (byte) Block.STATIONARY_LAVA.id;
                                }

                                j1 = i1;
                                if (k1 >= b0 - 1) {
                                    abyte[l1] = b1;
                                } else {
                                    abyte[l1] = b2;
                                }
                            } else if (j1 > 0) {
                                --j1;
                                abyte[l1] = b2;
                            }
                        }
                    }
                }
            }
        }
    }

    public Chunk getChunkAt(int i, int j) {
        return this.getOrCreateChunk(i, j);
    }

    public Chunk getOrCreateChunk(int i, int j) {
        this.h.setSeed((long) i * 341873128712L + (long) j * 132897987541L);
        byte[] abyte = new byte['\u8000'];

        this.a(i, j, abyte);
        this.b(i, j, abyte);
        this.s.a(this, this.n, i, j, abyte);

        return new Chunk(this.n, abyte, i, j);
    }

    private double[] a(double[] adouble, int i, int j, int k, int l, int i1, int j1) {
        if (adouble == null) {
            adouble = new double[l * i1 * j1];
        }

        double d0 = 684.412D;
        double d1 = 2053.236D;

        this.f = this.a.a(this.f, (double) i, (double) j, (double) k, l, 1, j1, 1.0D, 0.0D, 1.0D);
        this.g = this.b.a(this.g, (double) i, (double) j, (double) k, l, 1, j1, 100.0D, 0.0D, 100.0D);
        this.c = this.k.a(this.c, (double) i, (double) j, (double) k, l, i1, j1, d0 / 80.0D, d1 / 60.0D, d0 / 80.0D);
        this.d = this.i.a(this.d, (double) i, (double) j, (double) k, l, i1, j1, d0, d1, d0);
        this.e = this.j.a(this.e, (double) i, (double) j, (double) k, l, i1, j1, d0, d1, d0);
        int k1 = 0;
        int l1 = 0;
        double[] adouble1 = new double[i1];

        int i2;

        for (i2 = 0; i2 < i1; ++i2) {
            adouble1[i2] = Math.cos((double) i2 * 3.141592653589793D * 6.0D / (double) i1) * 2.0D;
            double d2 = (double) i2;

            if (i2 > i1 / 2) {
                d2 = (double) (i1 - 1 - i2);
            }

            if (d2 < 4.0D) {
                d2 = 4.0D - d2;
                adouble1[i2] -= d2 * d2 * d2 * 10.0D;
            }
        }

        for (i2 = 0; i2 < l; ++i2) {
            for (int j2 = 0; j2 < j1; ++j2) {
                double d3 = (this.f[l1] + 256.0D) / 512.0D;

                if (d3 > 1.0D) {
                    d3 = 1.0D;
                }

                double d4 = 0.0D;
                double d5 = this.g[l1] / 8000.0D;

                if (d5 < 0.0D) {
                    d5 = -d5;
                }

                d5 = d5 * 3.0D - 3.0D;
                if (d5 < 0.0D) {
                    d5 /= 2.0D;
                    if (d5 < -1.0D) {
                        d5 = -1.0D;
                    }

                    d5 /= 1.4D;
                    d5 /= 2.0D;
                    d3 = 0.0D;
                } else {
                    if (d5 > 1.0D) {
                        d5 = 1.0D;
                    }

                    d5 /= 6.0D;
                }

                d3 += 0.5D;
                d5 = d5 * (double) i1 / 16.0D;
                ++l1;

                for (int k2 = 0; k2 < i1; ++k2) {
                    double d6 = 0.0D;
                    double d7 = adouble1[k2];
                    double d8 = this.d[k1] / 512.0D;
                    double d9 = this.e[k1] / 512.0D;
                    double d10 = (this.c[k1] / 10.0D + 1.0D) / 2.0D;

                    if (d10 < 0.0D) {
                        d6 = d8;
                    } else if (d10 > 1.0D) {
                        d6 = d9;
                    } else {
                        d6 = d8 + (d9 - d8) * d10;
                    }

                    d6 -= d7;
                    double d11;

                    if (k2 > i1 - 4) {
                        d11 = (double) ((float) (k2 - (i1 - 4)) / 3.0F);
                        d6 = d6 * (1.0D - d11) + -10.0D * d11;
                    }

                    adouble[k1] = d6;
                    ++k1;
                }
            }
        }

        return adouble;
    }

    public boolean isChunkLoaded(int i, int j) {
        return true;
    }

    public void getChunkAt(IChunkProvider ichunkprovider, int i, int j) {
        BlockSand.instaFall = true;
        int k = i * 16;
        int l = j * 16;

        int i1;
        int j1;
        int k1;
        int l1;

        for (i1 = 0; i1 < 8; ++i1) {
            j1 = k + this.h.nextInt(16) + 8;
            k1 = this.h.nextInt(120) + 4;
            l1 = l + this.h.nextInt(16) + 8;
            (new WorldGenHellLava(Block.LAVA.id)).a(this.n, this.h, j1, k1, l1);
        }

        i1 = this.h.nextInt(this.h.nextInt(10) + 1) + 1;

        int i2;

        for (j1 = 0; j1 < i1; ++j1) {
            k1 = k + this.h.nextInt(16) + 8;
            l1 = this.h.nextInt(120) + 4;
            i2 = l + this.h.nextInt(16) + 8;
            (new WorldGenFire()).a(this.n, this.h, k1, l1, i2);
        }

        i1 = this.h.nextInt(this.h.nextInt(10) + 1);

        for (j1 = 0; j1 < i1; ++j1) {
            k1 = k + this.h.nextInt(16) + 8;
            l1 = this.h.nextInt(120) + 4;
            i2 = l + this.h.nextInt(16) + 8;
            (new WorldGenLightStone2()).a(this.n, this.h, k1, l1, i2);
        }

        for (j1 = 0; j1 < 10; ++j1) {
            k1 = k + this.h.nextInt(16) + 8;
            l1 = this.h.nextInt(128);
            i2 = l + this.h.nextInt(16) + 8;
            (new WorldGenLightStone1()).a(this.n, this.h, k1, l1, i2);
        }

        if (this.h.nextInt(1) == 0) {
            j1 = k + this.h.nextInt(16) + 8;
            k1 = this.h.nextInt(128);
            l1 = l + this.h.nextInt(16) + 8;
            (new WorldGenFlowers(Block.BROWN_MUSHROOM.id)).a(this.n, this.h, j1, k1, l1);
        }

        if (this.h.nextInt(1) == 0) {
            j1 = k + this.h.nextInt(16) + 8;
            k1 = this.h.nextInt(128);
            l1 = l + this.h.nextInt(16) + 8;
            (new WorldGenFlowers(Block.RED_MUSHROOM.id)).a(this.n, this.h, j1, k1, l1);
        }

        BlockSand.instaFall = false;
    }

    public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
        return true;
    }

    public boolean unloadChunks() {
        return false;
    }

    public boolean canSave() {
        return true;
    }
}
