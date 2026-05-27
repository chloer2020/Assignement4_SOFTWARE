package com.ibdgs.driver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Driver {
    private String driverID;
    private String name;
    private int experienceYears;
    private String licenseType; // Light, Medium, Heavy, PublicTransport
    private String address;
    private String birthdate;

    public Driver(String driverID, String name, int experienceYears, String licenseType, String address, String birthdate) {
        this.driverID = driverID;
        this.name = name;
        this.experienceYears = experienceYears;
        this.licenseType = licenseType;
        this.address = address;
        this.birthdate = birthdate;
    }

    // Getters
    public String getDriverID() {
        return driverID;
    }

    public String getName() {
        return name;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public String getAddress() {
        return address;
    }

    public String getBirthdate() {
        return birthdate;
    }

    // Setters with validation
    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // D1: Validate Driver ID
    public static boolean isValidDriverID(String driverID) {
        if (driverID == null || driverID.length() != 10) {
            return false;
        }

        // First two characters must be digits between 2 and 9
        if (!Character.isDigit(driverID.charAt(0)) || !Character.isDigit(driverID.charAt(1))) {
            return false;
        }
        int firstDigit = driverID.charAt(0) - '0';
        int secondDigit = driverID.charAt(1) - '0';
        if (firstDigit < 2 || firstDigit > 9 || secondDigit < 2 || secondDigit > 9) {
            return false;
        }

        // Characters 3-8 (indices 2-7) must have at least 2 special characters
        int specialCharCount = 0;
        for (int i = 2; i < 8; i++) {
            char c = driverID.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                specialCharCount++;
            }
        }
        if (specialCharCount < 2) {
            return false;
        }

        // Last two characters must be uppercase letters
        if (!Character.isUpperCase(driverID.charAt(8)) || !Character.isUpperCase(driverID.charAt(9))) {
            return false;
        }
        if (!Character.isLetter(driverID.charAt(8)) || !Character.isLetter(driverID.charAt(9))) {
            return false;
        }

        return true;
    }

    // D2: Validate Address Format (Street Number|Street Name|City|State|Country)
    public static boolean isValidAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        String[] parts = address.split("\\|");
        return parts.length == 5 && parts[0].matches("\\d+") && !parts[1].isEmpty() && !parts[2].isEmpty() 
               && !parts[3].isEmpty() && !parts[4].isEmpty();
    }

    // D3: Validate Birthday Format (DD-MM-YYYY)
    public static boolean isValidBirthdate(String birthdate) {
        if (birthdate == null || !birthdate.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate.parse(birthdate, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // Calculate driver age from birthdate
    public int getAge() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate birthDate = LocalDate.parse(birthdate, formatter);
            LocalDate today = LocalDate.now();
            return today.getYear() - birthDate.getYear() - 
                   (today.getDayOfYear() < birthDate.getDayOfYear() ? 1 : 0);
        } catch (DateTimeParseException e) {
            return -1;
        }
    }

    @Override
    public String toString() {
        return driverID + "\t" + name + "\t" + experienceYears + "\t" + licenseType + "\t" + address + "\t" + birthdate;
    }
}
