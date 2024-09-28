package org.bukkit.entity;

import org.bukkit.craftbukkit.util.Car;

public interface Boat extends Vehicle {
    double getMaxSpeed();

    void setMaxSpeed(double var1);

    Car getCar();

    void setCar(Car var1);

    int getGear();

    void setGear(int var1);
}
