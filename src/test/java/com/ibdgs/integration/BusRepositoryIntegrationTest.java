package com.ibdgs.integration;

import com.ibdgs.bus.Bus;
import com.ibdgs.bus.BusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/* Integration tests for all BusRepository operations.*/

class BusRepositoryIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void validBusIsStoredCorrectlyUsingRealTxtFile() throws IOException {
        File tempFile = tempDir.resolve("buses.txt").toFile();
        BusRepository repo = new BusRepository(tempFile);
        Path busFile = tempDir.resolve("buses.txt");

        assertTrue(repo.add("12345678", 45, 80.5, "Diesel"));
        Bus retrieved = repo.retrieve("12345678");

        assertNotNull(retrieved);
        Files.writeString(busFile, retrieved.toString(), StandardCharsets.UTF_8);
        String fileContent = Files.readString(busFile, StandardCharsets.UTF_8);

        assertTrue(fileContent.contains("12345678"));
        assertTrue(fileContent.contains("45"));
        assertTrue(fileContent.contains("Diesel"));
    }

    @Test
    void invalidBusIsRejectedAndNotStoredInTxtFile() throws IOException {
        File tempFile = tempDir.resolve("buses.txt").toFile();
        BusRepository repo = new BusRepository(tempFile);
        Path busFile = tempDir.resolve("buses.txt");

        assertFalse(repo.add("1234ABCD", 45, 80.5, "Diesel"));
        Files.writeString(busFile, "", StandardCharsets.UTF_8);

        assertEquals(0, repo.count());
        assertEquals("", Files.readString(busFile, StandardCharsets.UTF_8));
    }

    @Test
    void busUpdateIsPersistedCorrectlyUsingRealTxtFile() throws IOException {
        File tempFile = tempDir.resolve("buses.txt").toFile();
        BusRepository repo = new BusRepository(tempFile);
        Path busFile = tempDir.resolve("buses.txt");

        assertTrue(repo.add("11111111", 60, 90.0, "Hybrid"));
        assertTrue(repo.update("11111111", 45, 70.0));

        Bus updated = repo.retrieve("11111111");
        Files.writeString(busFile, updated.toString(), StandardCharsets.UTF_8);
        String fileContent = Files.readString(busFile, StandardCharsets.UTF_8);

        assertTrue(fileContent.contains("11111111"));
        assertTrue(fileContent.contains("45"));
        assertTrue(fileContent.contains("70.0"));
    }

    @Test
    void busRecordCountIsUpdatedCorrectlyUsingRealTxtFile() throws IOException {
        File tempFile = tempDir.resolve("buses.txt").toFile();
        BusRepository repo = new BusRepository(tempFile);
        Path countFile = tempDir.resolve("bus-count.txt");

        assertTrue(repo.add("22222222", 40, 75.0, "Diesel"));
        assertTrue(repo.add("33333333", 50, 65.0, "Electricity"));

        Files.writeString(countFile, String.valueOf(repo.count()), StandardCharsets.UTF_8);

        assertEquals(2, repo.count());
        assertEquals("2", Files.readString(countFile, StandardCharsets.UTF_8));
    }
}
