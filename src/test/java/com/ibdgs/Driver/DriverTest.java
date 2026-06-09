package com.ibdgs.driver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

import java.nio.file.Path;

import org.junit.jupiter.api.io.TempDir;

/* Unit tests for Driver-related requirements D1 to D5.*/
class DriverTest {

    private static final String ADDRESS = "12|King Street|Melbourne|VIC|Australia";
    private static final String BIRTHDATE = "15-04-1995";

    @TempDir
    Path tempDir;

    //D1: Driver ID Rules

    @Test
    void d1ValidDriverIdShouldBeAccepted() {
        assertTrue(Driver.isValidDriverID("23@#abcdAB"));
    }

    @Test
    void d1DriverIdShorterThanTenCharactersShouldBeRejected() {
        assertFalse(Driver.isValidDriverID("23@#abAB"));
    }

    @Test
    void d1DriverIdFirstTwoCharactersMustBeDigitsBetweenTwoAndNine() {
        assertFalse(Driver.isValidDriverID("10@#abcdAB"));
    }

    @Test
    void d1DriverIdMustHaveAtLeastTwoSpecialCharactersBetweenCharactersThreeAndEight() {
        assertFalse(Driver.isValidDriverID("23abcdefAB"));
    }

    @Test
    void d1DriverIdLastTwoCharactersMustBeUppercaseLetters() {
        assertFalse(Driver.isValidDriverID("23@#abcdab"));
    }

    @Test
    void d1DuplicateDriverIdShouldBeRejectedByRepository() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());

        assertTrue(repo.add("23@#abcdAB", "John Smith", 5, "Heavy", ADDRESS, BIRTHDATE));
        assertFalse(repo.add("23@#abcdAB", "Jane Smith", 6, "Heavy", ADDRESS, BIRTHDATE));
        assertEquals(1, repo.count());
    }

    // D2: Address Format Restrictions

    @Test
    void d2ValidAddressFormatShouldBeAccepted() {
        assertTrue(Driver.isValidAddress(ADDRESS));
    }

    @Test
    void d2AddressWithMissingFieldsShouldBeRejected() {
        assertFalse(Driver.isValidAddress("12|King Street|Melbourne"));
    }

    @Test
    void d2AddressUsingCommasInsteadOfPipesShouldBeRejected() {
        assertFalse(Driver.isValidAddress("12,King Street,Melbourne,VIC,Australia"));
    }

    @Test
    void d2AddressWithNonNumericStreetNumberShouldBeRejected() {
        assertFalse(Driver.isValidAddress("ABC|King Street|Melbourne|VIC|Australia"));
    }

    //D3: Birthdate Format

    @Test
    void d3ValidBirthdateFormatShouldBeAccepted() {
        assertTrue(Driver.isValidBirthdate("15-04-1995"));
    }

    @Test
    void d3BirthdateUsingSlashSeparatorShouldBeRejected() {
        assertFalse(Driver.isValidBirthdate("15/04/1995"));
    }

    @Test
    void d3BirthdateInWrongOrderShouldBeRejected() {
        assertFalse(Driver.isValidBirthdate("1995-04-15"));
    }

    @Test
    void d3NullBirthdateShouldBeRejected() {
        assertFalse(Driver.isValidBirthdate(null));
    }

    //D4: Licence Update Restrictions

    @Test
    void d4DriverWithMoreThanTenYearsExperienceCannotChangeLicence() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("23@#abcdAB", "John Smith", 11, "Heavy", ADDRESS, BIRTHDATE);

        boolean updated = repo.update("23@#abcdAB", 11, "PublicTransport", ADDRESS);

        assertFalse(updated);
        assertEquals("Heavy", repo.retrieve("23@#abcdAB").getLicenseType());
    }

    @Test
    void d4DriverWithExactlyTenYearsExperienceCanChangeLicence() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("24@#abcdCD", "Jane Smith", 10, "Medium", ADDRESS, BIRTHDATE);

        boolean updated = repo.update("24@#abcdCD", 10, "Heavy", ADDRESS);

        assertTrue(updated);
        assertEquals("Heavy", repo.retrieve("24@#abcdCD").getLicenseType());
    }

    @Test
    void d4DriverWithLessThanTenYearsExperienceCanChangeLicence() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("25@#abcdEF", "Sam Brown", 3, "Light", ADDRESS, BIRTHDATE);

        boolean updated = repo.update("25@#abcdEF", 3, "Medium", ADDRESS);

        assertTrue(updated);
        assertEquals("Medium", repo.retrieve("25@#abcdEF").getLicenseType());
    }

    // D5: Immutable Fields after updates

    @Test
    void d5DriverIdShouldRemainTheSameAfterUpdate() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("26@#abcdGH", "Alex Green", 4, "Light", ADDRESS, BIRTHDATE);

        String newAddress = "99|Collins Street|Melbourne|VIC|Australia";
        assertTrue(repo.update("26@#abcdGH", 4, "Medium", newAddress));

        Driver updated = repo.retrieve("26@#abcdGH");
        assertNotNull(updated);
        assertEquals("26@#abcdGH", updated.getDriverID());
    }

    @Test
    void d5DriverNameShouldRemainTheSameAfterUpdate() {
        DriverRepository repo = new DriverRepository(tempDir.resolve("drivers.txt").toFile());
        repo.add("27@#abcdIJ", "Chris White", 2, "Light", ADDRESS, BIRTHDATE);

        String newAddress = "50|Queen Street|Melbourne|VIC|Australia";
        assertTrue(repo.update("27@#abcdIJ", 2, "Medium", newAddress));

        Driver updated = repo.retrieve("27@#abcdIJ");
        assertNotNull(updated);
        assertEquals("Chris White", updated.getName());
    }

    @Test
    void d5DriverClassShouldNotProvidePublicDriverIdOrNameSetters() {
        assertThrows(NoSuchMethodException.class,
                () -> Driver.class.getDeclaredMethod("setDriverID", String.class));

        assertThrows(NoSuchMethodException.class,
                () -> Driver.class.getDeclaredMethod("setName", String.class));
    }
}
