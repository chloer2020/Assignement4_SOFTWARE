package com.ibdgs.bus;

public class Bus {
    private String busID;
    private int capacity;
    private double fuelLevel;
    private String fuelType; // Diesel, Hybrid, Electricity

    public Bus(String busID, int capacity, double fuelLevel, String fuelType) {
        this.busID = busID;
        this.capacity = capacity;
        this.fuelLevel = fuelLevel;
        this.fuelType = fuelType;
    }

    // Getters
    public String getBusID() {
        return busID;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getFuelLevel() {
        return fuelLevel;
    }

    public String getFuelType() {
        return fuelType;
    }

    // Setters with validation
    public void setCapacity(int newCapacity) {
        // B2: Capacity can only decrease or stay same, not increase
        if (newCapacity <= this.capacity) {
            this.capacity = newCapacity;
        }
    }

    public void setFuelLevel(double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    // B1: Validate Bus ID (exactly 8 digits)
    public static boolean isValidBusID(String busID) {
        if (busID == null || busID.length() != 8) {
            return false;
        }
        return busID.matches("\\d{8}");
    }

    @Override
    public String toString() {
        return busID + "\t" + capacity + "\t" + fuelLevel + "\t" + fuelType;
    }
}
