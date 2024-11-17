package bg.sofia.uni.fmi.mjt.olympics.competition;

import bg.sofia.uni.fmi.mjt.olympics.competitor.Athlete;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class CompetitionTest {

    private Set<Competitor> competitors1;
    private Set<Competitor> competitors2;
    private Set<Competitor> competitors3;

    @BeforeEach
    void setUp() {
        Competitor competitor1 = mock();
        Competitor competitor2 = mock();
        Competitor competitor3 = mock();
        competitors1 = new HashSet<>(List.of(competitor1));
        competitors2 = new HashSet<>(List.of(competitor2));
        competitors3 = new HashSet<>(List.of(competitor3));
    }

    @Test
    void testCompetitionWithNullNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Competition(null, "discipline", competitors1),
            "Competition() was expected to throw IllegalArgumentException when given null name");
    }

    @Test
    void testCompetitionWithBlankNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Competition("  ", "discipline", competitors1),
            "Competition() was expected to throw IllegalArgumentException when given blank name");
    }

    @Test
    void testCompetitionWithNullDisciplineThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Competition("name", null, competitors1),
            "Competition() was expected to throw IllegalArgumentException when given null discipline");
    }

    @Test
    void testCompetitionWithBlankDisciplineThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Competition("name", "  ", competitors1),
            "Competition() was expected to throw IllegalArgumentException when given blank discipline");
    }

    @Test
    void testCompetitionWithNullCompetitorsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Competition("name", "discipline", null),
            "Competition() was expected to throw IllegalArgumentException when given null competitors");
    }

    @Test
    void testCompetitionWithEmptyCompetitorsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Competition("name", "discipline", Set.of()),
            "Competition() was expected to throw IllegalArgumentException when given empty competitors");
    }

    @Test
    void testCompetitionWithValidArgumentsNotThrowsException() {
        Competitor competitor = new Athlete("65", "Kiro", "Jamaica");

        assertDoesNotThrow(() -> new Competition("name", "discipline", Set.of(competitor)),
            "Competition() was not expected to throw an exception");
    }

    @Test
    void testUnmodifiableCompetitors() {
        Competitor competitor2 = mock();

        Competition competition = new Competition("name", "discipline", competitors1);
        Set<Competitor> unmodifiableCompetitors = competition.competitors();

        assertThrows(UnsupportedOperationException.class, () -> unmodifiableCompetitors.add(competitor2),
            "competitors expected to return unmodifiable collection");
    }

    @Test
    void testEqualsReflexivity() {
        Competition competition = new Competition("name", "discipline", competitors1);
        assertEquals(competition, competition,
            "equals in Competition expected to be reflective operation");
    }

    @Test
    void testEqualsSymmetry() {
        Competition competition1 = new Competition("name1", "discipline1", competitors1);
        Competition competition2 = new Competition("name1", "discipline1", competitors2);

        assertEquals(competition1, competition2,
            "equals in Competition expected to return true for equal objects");
        assertEquals(competition2, competition1,
            "equals in Competition expected to be symmetrical operation");
    }

    @Test
    void testEqualsTransitivity() {
        Competition competition1 = new Competition("name1", "discipline1", competitors1);
        Competition competition2 = new Competition("name1", "discipline1", competitors2);
        Competition competition3 = new Competition("name1", "discipline1", competitors3);

        assertEquals(competition1, competition2,
            "equals in Competition expected to return true for equal objects");
        assertEquals(competition2, competition3,
            "equals in Competition expected to return true for equal objects");
        assertEquals(competition1, competition3,
            "equals in Competition expected to be transitive operation");
    }

    @Test
    void testEqualsConsistency() {
        Competition competition1 = new Competition("name1", "discipline1", competitors1);
        Competition competition2 = new Competition("name1", "discipline1", competitors2);

        assertEquals(competition1, competition2,
            "equals in Competition expected to return true for equal objects");
        assertEquals(competition1, competition2,
            "equals in Competition expected to be consistent operation");
    }

    @Test
    void testEqualsReturnsFalseWhenPassedNull() {
        Competition competition = new Competition("name", "discipline", competitors1);
        assertNotEquals(null, competition,
            "equals in Competition expected to return false when a null argument is passed");
    }

    @Test
    void testEqualsReturnsFalseForDifferentObjects() {
        Competition competition1 = new Competition("name", "discipline", competitors1);
        Competition competition2 = new Competition("differentName", "differentDiscipline", competitors2);

        assertNotEquals(competition1, competition2,
            "equals in Competition expected to return false for different objects");
    }

    @Test
    void testHashCodeConsistency() {
        Competition competition = new Competition("name", "discipline", competitors1);
        assertEquals(competition.hashCode(), competition.hashCode(),
            "hashCode in Competition expected to return the same hashCode for one object");
    }

    @Test
    void testHashCodeWhenPassedEqualObjects() {
        Competition competition1 = new Competition("name1", "discipline1", competitors1);
        Competition competition2 = new Competition("name1", "discipline1", competitors2);

        assertEquals(competition1, competition2,
            "equal in Competition expected to return true for two equal objects");
        assertEquals(competition1.hashCode(), competition2.hashCode(),
            "hashCode in Competition expected to return equal hashCode for two equal objects");
    }

    @Test
    void testHashCodeReturnsDifferentHashCodeForDifferentObjects() {
        Competition competition1 = new Competition("name", "discipline", competitors1);
        Competition competition2 = new Competition("differentName", "differentDiscipline", competitors2);

        assertNotEquals(competition1.hashCode(), competition2.hashCode(),
            "hashCode in Competition expected to return different hashCode for different objects");
    }

}
