package com.ibdgs.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DriverRepository {
    private List<Driver> drivers;

    public DriverRepository() {
        this.drivers = new ArrayList<>();
    }

    // D1: Check if driver ID is unique
    private boolean isDriverIDUnique(String driverID) {
        return drivers.stream().noneMatch(d -> d.getDriverID().equals(driverID));
    }

    // Add a new driver with all validations
    public boolean add(String driverID, String name, int experienceYears, String licenseType, 
                       String address, String birthdate) {
        // D1: Validate driver ID format and uniqueness
        if (!Driver.isValidDriverID(driverID)) {
            System.out.println("Error: Invalid driver ID format. Must be 10 chars with format: 2-9, 2-9, 2+ special chars (3-8), A-Z, A-Z");
            return false;
        }

        if (!isDriverIDUnique(driverID)) {
            System.out.println("Error: Driver ID already exists.");
            return false;
        }

        // D2: Validate address format
        if (!Driver.isValidAddress(address)) {
            System.out.println("Error: Invalid address format. Must be: Street Number|Street Name|City|State|Country");
            return false;
        }

        // D3: Validate birthdate format
        if (!Driver.isValidBirthdate(birthdate)) {
            System.out.println("Error: Invalid birthdate format. Must be DD-MM-YYYY");
            return false;
        }

        Driver newDriver = new Driver(driverID, name, experienceYears, licenseType, address, birthdate);
        drivers.add(newDriver);
        return true;
    }

    // Retrieve a driver by ID
    public Driver retrieve(String driverID) {
        Optional<Driver> driver = drivers.stream()
                .filter(d -> d.getDriverID().equals(driverID))
                .findFirst();
        return driver.orElse(null);
    }

    // Retrieve all drivers
    public List<Driver> retrieveAll() {
        return new ArrayList<>(drivers);
    }

    // Update a driver with validation
    public boolean update(String driverID, int newExperienceYears, String newLicenseType, String newAddress) {
        Driver driver = retrieve(driverID);
        if (driver == null) {
            System.out.println("Error: Driver not found.");
            return false;
        }

        // D5: Driver ID and name are immutable (cannot be changed)
        // D2: Validate address format if being updated
        if (!Driver.isValidAddress(newAddress)) {
            System.out.println("Error: Invalid address format. Must be: Street Number|Street Name|City|State|Country");
            return false;
        }

        // D4: If driver has more than 10 years of experience, license type cannot be changed
        if (driver.getExperienceYears() > 10 && !driver.getLicenseType().equals(newLicenseType)) {
            System.out.println("Error: Cannot change license type for drivers with more than 10 years of experience.");
            return false;
        }

        // Update the driver
        driver.setExperienceYears(newExperienceYears);
        if (!driver.getLicenseType().equals(newLicenseType)) {
            driver.setLicenseType(newLicenseType);
        }
        driver.setAddress(newAddress);

        return true;
    }

    // Count the number of stored drivers
    public int count() {
        return drivers.size();
    }

    // Additional helper methods for validation checks

    // Check if a driver can drive an electric bus (B4: 5+ years experience required)
    public boolean canDriveElectricBus(String driverID) {
        Driver driver = retrieve(driverID);
        if (driver == null) return false;
        return driver.getExperienceYears() >= 5;
    }

    // Check if a driver can drive based on age (B3: Drivers > 50 years cannot drive capacity >= 50)
    public boolean canDriveBusWithCapacity(String driverID, int busCapacity) {
        Driver driver = retrieve(driverID);
        if (driver == null) return false;
        
        int age = driver.getAge();
        if (age > 50 && busCapacity >= 50) {
            return false;
        }
        return true;
    }

    // Check if driver has required license (B5: Heavy or PublicTransport for electric/hybrid)
    public boolean hasRequiredLicense(String driverID, String busType) {
        Driver driver = retrieve(driverID);
        if (driver == null) return false;

        if (busType.equals("Electricity") || busType.equals("Hybrid")) {
            return driver.getLicenseType().equals("Heavy") || driver.getLicenseType().equals("PublicTransport");
        }
        return true;
    }
} 

