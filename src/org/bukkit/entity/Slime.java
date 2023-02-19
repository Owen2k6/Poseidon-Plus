/**
 *
 */
package org.bukkit.entity;

/**
 * Represents a Slime.
 *
 * @author Cogito
 */
public interface Slime extends LivingEntity {

    /**
     * @return The size of the slime
     * @author Celtic Minstrel
     */
    public int getSize();

    /**
     * @param sz The new size of the slime.
     * @author Celtic Minstrel
     */
    public void setSize(int sz);
}
