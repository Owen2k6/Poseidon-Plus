package org.bukkit.craftbukkit.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Car {
    public String model = "Fork Model Tango";
    public double[] gearRatios = new double[]{3.63, 3.0};
    public double rpm = 1513.0;
    private double fuelTank; // Capacity of the fuel tank in British gallons (constant)
    private double fuel; // Current fuel level in British gallons

    public Car() {
        this.fuelTank = 8.0; // Default fuel tank size
        this.fuel = 0.1; // Initial fuel level
    }

    public Car(double fuelTank, double initialFuel) {
        this.fuelTank = fuelTank;
        this.fuel = initialFuel;
    }

    // Getter for fuel
    public double getFuel() {
        return fuel;
    }

    // Setter for fuel
    public void setFuel(double fuel) {
        this.fuel = fuel;
    }

    // Getter for fuel tank size (which is constant and cannot change)
    public double getFuelTankSize() {
        return fuelTank;
    }
    public void setFuelTankSize(double amount) {
        this.fuelTank = amount;
    }

    // Method to consume fuel
    public void consumeFuel(double amount) {
        this.fuel -= amount;
        if (this.fuel < 0) {
            this.fuel = 0; // Ensure fuel doesn't go below zero
        }
    }

    // Check if the car has any fuel left
    public boolean hasFuel() {
        return this.fuel > 0;
    }

    // Method to refuel the car
    public void refuel(double amount) {
        this.fuel += amount;
        if (this.fuel > this.fuelTank) {
            this.fuel = this.fuelTank; // Ensure fuel doesn't exceed tank capacity
        }
    }

    // Method to save car data to file
    public void saveCarData(String username) {
        Path carDataPath = Paths.get("plugins/GoCars/playerdata/" + username + "/cars/" + model + ".txt");
        try {
            Files.createDirectories(carDataPath.getParent());
            BufferedWriter writer = Files.newBufferedWriter(carDataPath);
            writer.write("model=" + model + "\n");
            writer.write("rpm=" + rpm + "\n");
            writer.write("fuelTank=" + fuelTank + "\n");
            writer.write("fuel=" + fuel + "\n");
            writer.write("gearRatios=" + Arrays.toString(gearRatios) + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load car data from file
    public static Car loadCarData(String username, String model) {
        Path carDataPath = Paths.get("plugins/GoCars/playerdata/" + username + "/cars/" + model + ".txt");
        Car car = new Car();
        if (Files.exists(carDataPath)) {
            try {
                List<String> lines = Files.readAllLines(carDataPath);
                for (String line : lines) {
                    String[] parts = line.split("=");
                    switch (parts[0]) {
                        case "model":
                            car.model = parts[1];
                            break;
                        case "rpm":
                            car.rpm = Double.parseDouble(parts[1]);
                            break;
                        case "fuelTank":
                            // Since fuelTank is final, we skip setting it from the file, assuming it's correctly initialized
                            break;
                        case "fuel":
                            car.fuel = Double.parseDouble(parts[1]);
                            break;
                        case "gearRatios":
                            String[] ratios = parts[1].replace("[", "").replace("]", "").split(", ");
                            car.gearRatios = Arrays.stream(ratios).mapToDouble(Double::parseDouble).toArray();
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return car;
    }
}
