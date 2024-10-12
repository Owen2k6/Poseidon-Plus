//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.bukkit.craftbukkit.entity;

import net.minecraft.server.EntityBoat;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.Car;
import org.bukkit.entity.Boat;

public class CraftBoat extends CraftVehicle implements Boat {
    protected EntityBoat boat;

    public CraftBoat(CraftServer server, EntityBoat entity) {
        super(server, entity);
        this.boat = entity;
    }

    public double getMaxSpeed() {
        return this.boat.maxSpeed;
    }

    public void setMaxSpeed(double speed) {
        this.boat.maxSpeed = speed;
    }

    public Car getCar() {
        return this.boat.car;
    }

    public void setCar(Car car) {
        this.boat.car = car;
    }

    public int getGear() {
        return this.boat.gear;
    }

    public void setGear(int gear) {
        this.boat.gear = gear;
    }

    public String toString() {
        return "CraftBoat";
    }
}
