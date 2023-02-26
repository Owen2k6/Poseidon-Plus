package org.bukkit.generator;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Random;

/**
 * A block populator is responsible for generating a small area of blocks.
 * For example, generating glowstone inside the nether or generating dungeons full of treasure
 */
public abstract class BlockPopulator {

    public abstract void populate(World world, Random random, Chunk source);
}
