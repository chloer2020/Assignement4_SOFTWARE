package com.ibdgs;

import com.ibdgs.driver.Driver;
import com.ibdgs.driver.DriverRepository;
import com.ibdgs.bus.Bus;
import com.ibdgs.bus.BusRepository;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Bus Management System ===\n");

        DriverRepository driverRepo = new DriverRepository();
        BusRepository busRepo = new BusRepository();

        // ===== DRIVER OPERATIONS =====
        System.out.println("--- Adding Drivers ---");
        
        // Add valid drivers
        // Format: [2-9][2-9][6 chars with 2+ special chars][A-Z][A-Z]
        driverRepo.add("23@#ab12AB", "John Doe", 12, "Heavy", "123|Main Street|New York|NY|USA", "15-06-1975");
        driverRepo.add("45$%cd34CD", "Jane Smith", 8, "Medium", "456|Oak Avenue|Los Angeles|CA|USA", "22-03-1985");
        driverRepo.add("67&*ef56EF", "Bob Johnson", 3, "Light", "789|Pine Road|Chicago|IL|USA", "10-11-1995");
        driverRepo.add("89!@gh78GH", "Alice Brown", 6, "PublicTransport", "321|Elm Street|Houston|TX|USA", "05-07-1990");

        System.out.println("Total drivers added: " + driverRepo.count() + "\n");

        // Retrieve a specific driver
        System.out.println("--- Retrieving Specific Driver ---");
        Driver driver = driverRepo.retrieve("23@#ab12AB");
        if (driver != null) {
            System.out.println("Driver found: " + driver.getName() + ", Experience: " + driver.getExperienceYears() + " years, Age: " + driver.getAge());
        }
        System.out.println();

        // Retrieve all drivers
        System.out.println("--- All Drivers ---");
        for (Driver d : driverRepo.retrieveAll()) {
            System.out.println(d);
        }
        System.out.println();

        // Update a driver
        System.out.println("--- Updating Driver ---");
        boolean updated = driverRepo.update("45$%cd34CD", 10, "Medium", "456|Oak Avenue|Los Angeles|CA|USA");
        System.out.println("Update successful: " + updated);
        System.out.println();

        // Try to update license for driver with >10 years experience (should fail)
        System.out.println("--- Attempting invalid update (license change for >10 years experience) ---");
        updated = driverRepo.update("23@#ab12AB", 12, "PublicTransport", "123|Main Street|New York|NY|USA");
        System.out.println("Update successful: " + updated);
        System.out.println();

        // ===== BUS OPERATIONS =====
        System.out.println("--- Adding Buses ---");
        
        busRepo.add("12345678", 50, 75.5, "Diesel");
        busRepo.add("87654321", 30, 50.0, "Hybrid");
        busRepo.add("11223344", 40, 60.0, "Electricity");

        System.out.println("Total buses added: " + busRepo.count() + "\n");

        // Retrieve all buses
        System.out.println("--- All Buses ---");
        for (Bus b : busRepo.retrieveAll()) {
            System.out.println(b);
        }
        System.out.println();

        // Update a bus (decrease capacity)
        System.out.println("--- Updating Bus (decreasing capacity) ---");
        updated = busRepo.update("12345678", 45, 70.0);
        System.out.println("Update successful: " + updated);
        System.out.println();

        // Try to update bus with increased capacity (should fail)
        System.out.println("--- Attempting invalid update (increasing capacity) ---");
        updated = busRepo.update("12345678", 55, 80.0);
        System.out.println("Update successful: " + updated);
        System.out.println();

        // ===== VALIDATION CHECKS =====
        System.out.println("--- Validation Checks ---");
        
        // B3: Check if older drivers can drive large buses
        System.out.println("Can driver with age > 50 drive bus with capacity >= 50? " + 
                          driverRepo.canDriveBusWithCapacity("23@#ab12AB", 50));
        
        // B4: Check if driver can drive electric bus
        System.out.println("Can driver with 6 years experience drive electric bus? " + 
                          driverRepo.canDriveElectricBus("89!@gh78GH"));
        System.out.println("Can driver with 3 years experience drive electric bus? " + 
                          driverRepo.canDriveElectricBus("67&*ef56EF"));
        
        // B5: Check if driver has required license for electric buses
        System.out.println("Does Heavy license driver can drive Electricity bus? " + 
                          driverRepo.hasRequiredLicense("23@#ab12AB", "Electricity"));
        System.out.println("Does Light license driver can drive Electricity bus? " + 
                          driverRepo.hasRequiredLicense("67&*ef56EF", "Electricity"));
        System.out.println();

        System.out.println("=== System Test Complete ===");
    }
}