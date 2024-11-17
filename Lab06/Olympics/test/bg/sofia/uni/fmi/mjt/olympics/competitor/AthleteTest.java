package bg.sofia.uni.fmi.mjt.olympics.competitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AthleteTest {

    Athlete athlete = new Athlete("32", "Pepi", "Nigeria");

    @Test
    void testAddMedalWithNullMedal() {
        assertThrows(IllegalArgumentException.class, () -> athlete.addMedal(null),
            "addMedal was expected to throw IllegalArgumentException when given null medal");
    }

    @Test
    void testAddMedalValidMedal() {
        athlete.addMedal(Medal.GOLD);
        assertEquals(1, athlete.getMedals().size(),
            "addMedal was expected to add only 1 medal");
        assertEquals(Medal.GOLD, athlete.getMedals().getFirst(),
            "addMedal was expected to add a GOLD medal");
    }

    @Test
    void testAddMedalDifferentMedals() {
        athlete.addMedal(Medal.BRONZE);
        athlete.addMedal(Medal.SILVER);
        athlete.addMedal(Medal.GOLD);
        assertEquals(3, athlete.getMedals().size(),
            "addMedals was expected to add one medal of each type, total of three");
    }

    @Test
    void testEqualsReflexivity() {
        Athlete athlete1 = new Athlete("ID1", "Name", "Nationality");
        assertEquals(athlete1, athlete1,
            "equals in Athlete expected to be reflective operation");
    }

    @Test
    void testEqualsSymmetry() {
        Athlete athlete1 = new Athlete("ID1", "Name", "Nationality");
        Athlete athlete2 = new Athlete("ID2", "Name", "Nationality");

        assertEquals(athlete1, athlete2,
            "equals in Athlete expected to return true for equal objects");
        assertEquals(athlete2, athlete1,
            "equals in Athlete expected to be symmetrical operation");
    }

    @Test
    void testEqualsTransitivity() {
        Athlete athlete1 = new Athlete("ID1", "Name", "Nationality");
        Athlete athlete2 = new Athlete("ID2", "Name", "Nationality");
        Athlete athlete3 = new Athlete("ID3", "Name", "Nationality");

        assertEquals(athlete1, athlete2,
            "equals in Athlete expected to return true for equal objects");
        assertEquals(athlete2, athlete3,
            "equals in Athlete expected to return true for equal objects");
        assertEquals(athlete1, athlete3,
            "equals in Athlete expected to be transitive operation");
    }

    @Test
    void testEqualsConsistency() {
        Athlete athlete1 = new Athlete("ID1", "Name", "Nationality");
        Athlete athlete2 = new Athlete("ID2", "Name", "Nationality");

        assertEquals(athlete1, athlete2,
            "equals in Athlete expected to return true for equal objects");
        assertEquals(athlete1, athlete2,
            "equals in Athlete expected to be consistent operation");
    }

    @Test
    void testEqualsReturnsFalseWhenPassedNull() {
        assertNotEquals(null, athlete,
            "equals in Athlete expected to return false when a null argument is passed");
    }

    @Test
    void testEqualsReturnsFalseForDifferentObjects() {
        Athlete athlete1 = new Athlete("ID1", "Name", "Nationality");
        Athlete athlete2 = new Athlete("ID2", "DifferentName", "DifferentNationality");

        assertNotEquals(athlete1, athlete2,
            "equals in Athlete expected to return false for different objects");
    }

    @Test
    void testHashCodeConsistency() {
        assertEquals(athlete.hashCode(), athlete.hashCode(),
            "hashCode in Athlete expected to return the same hashCode for one object");
    }

    @Test
    void testHashCodeWhenPassedEqualObjects() {
        Athlete athlete1 = new Athlete("ID1", "Name", "Nationality");
        Athlete athlete2 = new Athlete("ID2", "Name", "Nationality");

        assertEquals(athlete1, athlete2,
            "equal in Athlete expected to return true for two equal objects");
        assertEquals(athlete1.hashCode(), athlete2.hashCode(),
            "hashCode in Athlete expected to return equal hashCode for two equal objects");
    }

    @Test
    void testHashCodeReturnsDifferentHashCodeForDifferentObjects() {
        Athlete athlete1 = new Athlete("ID1", "Name", "Nationality");
        Athlete athlete2 = new Athlete("ID2", "DifferentName", "DifferentNationality");

        assertNotEquals(athlete1.hashCode(), athlete2.hashCode(),
            "hashCode in Athlete expected to return different hashCode for different objects");
    }

}
