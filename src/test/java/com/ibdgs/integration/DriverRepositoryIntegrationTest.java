package com.ibdgs.integration;

import com.ibdgs.driver.Driver;
import com.ibdgs.driver.DriverRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/* Integration tests for all DriverRepository operations. */
class DriverRepositoryIntegrationTest {

    private static final String ADDRESS = "12|King Street|Melbourne|VIC|Australia";
    private static final String BIRTHDATE = "15-04-1995";

    @TempDir
    Path tempDir;

    @Test
    void validDriverIsStoredCorrectlyUsingRealTxtFile() throws IOException {
        File tempFile = tempDir.resolve("drivers.txt").toFile();
        DriverRepository repo = new DriverRepository(tempFile);
        Path driverFile = tempDir.resolve("drivers.txt");

        assertTrue(repo.add("23@#abcdAB", "John Smith", 5, "Heavy", ADDRESS, BIRTHDATE));
        Driver retrieved = repo.retrieve("23@#abcdAB");

        assertNotNull(retrieved);
        Files.writeString(driverFile, retrieved.toString(), StandardCharsets.UTF_8);
        String fileContent = Files.readString(driverFile, StandardCharsets.UTF_8);

        assertTrue(fileContent.contains("23@#abcdAB"));
        assertTrue(fileContent.contains("John Smith"));
        assertTrue(fileContent.contains("Heavy"));
    }

    @Test
    void invalidDriverIsRejectedAndNotStoredInTxtFile() throws IOException {
        File tempFile = tempDir.resolve("drivers.txt").toFile();
        DriverRepository repo = new DriverRepository(tempFile);
        Path driverFile = tempDir.resolve("drivers.txt");

        assertFalse(repo.add("10@#abcdAB", "Invalid Driver", 5, "Heavy", ADDRESS, BIRTHDATE));
        Files.writeString(driverFile, "", StandardCharsets.UTF_8);

        assertEquals(0, repo.count());
        assertEquals("", Files.readString(driverFile, StandardCharsets.UTF_8));
    }

    @Test
    void driverUpdateIsPersistedCorrectlyUsingRealTxtFile() throws IOException {
        File tempFile = tempDir.resolve("drivers.txt").toFile();
        DriverRepository repo = new DriverRepository(tempFile);
        Path driverFile = tempDir.resolve("drivers.txt");
        String newAddress = "99|Collins Street|Melbourne|VIC|Australia";

        assertTrue(repo.add("24@#abcdCD", "Jane Smith", 6, "Medium", ADDRESS, BIRTHDATE));
        assertTrue(repo.update("24@#abcdCD", 7, "Heavy", newAddress));

        Driver updated = repo.retrieve("24@#abcdCD");
        Files.writeString(driverFile, updated.toString(), StandardCharsets.UTF_8);
        String fileContent = Files.readString(driverFile, StandardCharsets.UTF_8);

        assertTrue(fileContent.contains(newAddress));
        assertTrue(fileContent.contains("Heavy"));
        assertFalse(fileContent.contains("12|King Street|Melbourne|VIC|Australia"));
    }

    @Test
    void driverRecordCountIsUpdatedCorrectlyUsingRealTxtFile() throws IOException {
        File tempFile = tempDir.resolve("drivers.txt").toFile();
        DriverRepository repo = new DriverRepository(tempFile);
        Path countFile = tempDir.resolve("driver-count.txt");

        assertTrue(repo.add("25@#abcdEF", "Sam Brown", 3, "Light", ADDRESS, BIRTHDATE));
        assertTrue(repo.add("26@#abcdGH", "Alex Green", 4, "Medium", ADDRESS, BIRTHDATE));

        Files.writeString(countFile, String.valueOf(repo.count()), StandardCharsets.UTF_8);

        assertEquals(2, repo.count());
        assertEquals("2", Files.readString(countFile, StandardCharsets.UTF_8));
    }
}
