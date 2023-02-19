/**
 *
 */
package org.bukkit.entity;

import org.bukkit.material.Colorable;

/**
 * Represents a Sheep.
 *
 * @author Cogito
 */
public interface Sheep extends Animals, Colorable {

    /**
     * @return Whether the sheep is sheared.
     * @author Celtic Minstrel
     */
    public boolean isSheared();

    /**
     * @param flag Whether to shear the sheep
     * @author Celtic Minstrel
     */
    public void setSheared(boolean flag);
}
