package com.ibdgs.bus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ibdgs.data.DataManagement;

public class BusRepository {
    private List<Bus> buses;

    public BusRepository() {
        this.buses = new ArrayList<>();
        this.dataManagement = new DataManagement();
        this.busesFile = new File("buses.txt");
    }
    // constructor so test files do not replace changed txt file
    public BusRepository(File file) {
        this.buses = new ArrayList<>();
        this.dataManagement = new DataManagement();
        this.busesFile = file; 
    }
    // persistence helpers
    private DataManagement dataManagement;
    private File busesFile;

    private void persistAll() {
        try {
            if (busesFile.exists()) {
                busesFile.delete();
            }
            for (Bus b : buses) {
                dataManagement.addData(b.getBusID(), b.getCapacity(), b.getFuelLevel(), b.getFuelType(), busesFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // B1: Check if bus ID is unique
    private boolean isBusIDUnique(String busID) {
        return buses.stream().noneMatch(b -> b.getBusID().equals(busID));
    }

    // Add a new bus with validation
    public boolean add(String busID, int capacity, double fuelLevel, String fuelType) {
        // B1: Validate bus ID format and uniqueness
        if (!Bus.isValidBusID(busID)) {
            System.out.println("Error: Invalid bus ID format. Must be exactly 8 digits.");
            return false;
        }

        if (!isBusIDUnique(busID)) {
            System.out.println("Error: Bus ID already exists.");
            return false;
        }

        Bus newBus = new Bus(busID, capacity, fuelLevel, fuelType);
        buses.add(newBus);
        // persist new entry
        dataManagement.addData(busID, capacity, fuelLevel, fuelType, busesFile);
        return true;
    }

    // Retrieve a bus by ID
    public Bus retrieve(String busID) {
        Optional<Bus> bus = buses.stream()
                .filter(b -> b.getBusID().equals(busID))
                .findFirst();
        return bus.orElse(null);
    }

    // Retrieve all buses
    public List<Bus> retrieveAll() {
        return new ArrayList<>(buses);
    }

    // Update a bus with validation
    public boolean update(String busID, int newCapacity, double newFuelLevel) {
        Bus bus = retrieve(busID);
        if (bus == null) {
            System.out.println("Error: Bus not found.");
            return false;
        }

        // B2: Capacity cannot increase, only stay same or decrease
        if (newCapacity > bus.getCapacity()) {
            System.out.println("Error: Bus capacity cannot increase. Current: " + bus.getCapacity() +
                    ", Attempted: " + newCapacity);
            return false;
        }

        bus.setCapacity(newCapacity);
        bus.setFuelLevel(newFuelLevel);

        // persist all buses back to file
        persistAll();

        return true;
    }

    // Count the number of stored buses
    public int count() {
        return buses.size();
    }
}