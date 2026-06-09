package com.ibdgs.bus;

import com.ibdgs.driver.DriverRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.time.LocalDate;
import java.io.File;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

/* Unit tests for Bus-related requirements B1 to B5. */
class BusTest {

    private static final String ADDRESS = "12|King Street|Melbourne|VIC|Australia";
    private static final DateTimeFormatter BIRTHDATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static String birthdateForAge(int age) {
        return LocalDate.now().minusYears(age).minusDays(1).format(BIRTHDATE_FORMATTER);
    }

    @TempDir
    Path tempDir;
    //B1: Bus ID Rules

    @Test
    void b1ValidBusIdShouldBeAccepted() {
        assertTrue(Bus.isValidBusID("12345678"));
    }

    @Test
    void b1BusIdShorterThanEightDigitsShouldBeRejected() {
        assertFalse(Bus.isValidBusID("1234567"));
    }

    @Test
    void b1BusIdContainingLettersShouldBeRejected() {
        assertFalse(Bus.isValidBusID("1234ABCD"));
    }

    @Test
    void b1DuplicateBusIdShouldBeRejectedByRepository() {
        BusRepository repo = new BusRepository(tempDir.resolve("buses.txt").toFile());

        assertTrue(repo.add("12345678", 45, 80.0, "Diesel"));
        assertFalse(repo.add("12345678", 50, 70.0, "Hybrid"));
        assertEquals(1, repo.count());
    }

    //Capacity Update Restriction

    @Test
    void b2BusCapacityCanDecreaseDuringUpdate() {
        BusRepository repo = new BusRepository(tempDir.resolve("buses.txt").toFile());
        repo.add("11111111", 60, 90.0, "Diesel");

        boolean updated = repo.update("11111111", 45, 70.0);

        assertTrue(updated);
        assertEquals(45, repo.retrieve("11111111").getCapacity());
    }

    @Test
    void b2BusCapacityCannotIncreaseDuringUpdate() {
        BusRepository repo = new BusRepository(tempDir.resolve("buses.txt").toFile());
        repo.add("22222222", 40, 90.0, "Diesel");

        boolean updated = repo.update("22222222", 60, 70.0);

        assertFalse(updated);
        assertEquals(40, repo.retrieve("22222222").getCapacity());
    }

    @Test
    void b2BusCapacityCanStayTheSameDuringUpdate() {
        BusRepository repo = new BusRepository(tempDir.resolve("buses.txt").toFile());
        repo.add("33333333", 50, 90.0, "Diesel");

        boolean updated = repo.update("33333333", 50, 60.0);

        assertTrue(updated);
        assertEquals(50, repo.retrieve("33333333").getCapacity());
        assertEquals(60.0, repo.retrieve("33333333").getFuelLevel(), 0.001);
    }

    //Driver Age Restrictions

    @Test
    void b3DriverOlderThanFiftyCannotDriveBusWithCapacityFiftyOrMore() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("23@#abcdAB", "Older Driver", 20, "Heavy", ADDRESS, birthdateForAge(51));

        assertFalse(repo.canDriveBusWithCapacity("23@#abcdAB", 50));
    }

    @Test
    void b3DriverAgedExactlyFiftyCanDriveBusWithCapacityFifty() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("24@#abcdCD", "Fifty Driver", 20, "Heavy", ADDRESS, birthdateForAge(50));

        assertTrue(repo.canDriveBusWithCapacity("24@#abcdCD", 50));
    }

    @Test
    void b3DriverOlderThanFiftyCanDriveBusWithCapacityLessThanFifty() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("25@#abcdEF", "Older Small Bus Driver", 20, "Heavy", ADDRESS, birthdateForAge(55));

        assertTrue(repo.canDriveBusWithCapacity("25@#abcdEF", 40));
    }

    // Electric Bus Restriction

    @Test
    void b4DriverWithFiveYearsExperienceCanDriveElectricBus() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("26@#abcdGH", "Experienced Driver", 5, "Heavy", ADDRESS, birthdateForAge(35));

        assertTrue(repo.canDriveElectricBus("26@#abcdGH"));
    }

    @Test
    void b4DriverWithFourYearsExperienceCannotDriveElectricBus() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("27@#abcdIJ", "Less Experienced Driver", 4, "Heavy", ADDRESS, birthdateForAge(35));

        assertFalse(repo.canDriveElectricBus("27@#abcdIJ"));
    }

    @Test
    void b4DriverWithZeroYearsExperienceCannotDriveElectricBus() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("28@#abcdKL", "New Driver", 0, "Heavy", ADDRESS, birthdateForAge(30));

        assertFalse(repo.canDriveElectricBus("28@#abcdKL"));
    }

    // -------------------- B5: Driver Licence Restriction --------------------

    @Test
    void b5HeavyLicenceCanOperateElectricBus() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("29@#abcdMN", "Heavy Driver", 8, "Heavy", ADDRESS, birthdateForAge(40));

        assertTrue(repo.hasRequiredLicense("29@#abcdMN", "Electricity"));
    }

    @Test
    void b5PublicTransportLicenceCanOperateHybridBus() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("32@#abcdOP", "PT Driver", 8, "PublicTransport", ADDRESS, birthdateForAge(40));

        assertTrue(repo.hasRequiredLicense("32@#abcdOP", "Hybrid"));
    }

    @Test
    void b5LightLicenceCannotOperateHybridBus() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("34@#abcdQR", "Light Driver", 8, "Light", ADDRESS, birthdateForAge(40));

        assertFalse(repo.hasRequiredLicense("34@#abcdQR", "Hybrid"));
    }

    @Test
    void b5MediumLicenceCannotOperateElectricBus() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("35@#abcdST", "Medium Driver", 8, "Medium", ADDRESS, birthdateForAge(40));

        assertFalse(repo.hasRequiredLicense("35@#abcdST", "Electricity"));
    }
}
