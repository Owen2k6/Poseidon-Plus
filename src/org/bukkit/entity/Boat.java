package org.bukkit.entity;

/**
 * Represents a boat entity.
 *
 * @author sk89q
 */
public interface Boat extends Vehicle {


    public double getMaxSpeed();

    /**
     * Sets the maximum speed of a boat. Must be nonnegative. Default is 0.4D.
     *
     * @param speed
     */
    public void setMaxSpeed(double speed);
}
