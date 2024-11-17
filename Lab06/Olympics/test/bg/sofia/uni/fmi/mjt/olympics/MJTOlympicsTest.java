package bg.sofia.uni.fmi.mjt.olympics;

import bg.sofia.uni.fmi.mjt.olympics.competition.Competition;
import bg.sofia.uni.fmi.mjt.olympics.competition.CompetitionResultFetcher;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Medal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MJTOlympicsTest {

    @Mock
    private CompetitionResultFetcher resultFetcher;
    @Mock
    Set<Competitor> registeredCompetitors;

    @InjectMocks
    private MJTOlympics olympics;

    @Test
    void testUpdateMedalStatisticsWithNullCompetition() {
        assertThrows(IllegalArgumentException.class, () -> olympics.updateMedalStatistics(null),
            "updateMedalStatistics expected to throw IllegalArgumentException when competition is null");
    }

    @Test
    void testUpdateMedalStatisticsWithNotRegisteredCompetitorsThrowsException() {
        Competition competition = mock();

        when(olympics.getRegisteredCompetitors().containsAll(competition.competitors())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> olympics.updateMedalStatistics(competition),
            "updateMedalStatistics expected to throw IllegalArgumentException when not all competitors are registered");
    }

    @Test
    void testUpdateMedalStatisticsWithValidCompetition() {
        Competition competition = mock();

        Competitor competitor1 = mock();
        Competitor competitor2 = mock();
        Competitor competitor3 = mock();

        when(competitor1.getNationality()).thenReturn("Argentina");
        when(competitor2.getNationality()).thenReturn("Bulgaria");
        when(competitor3.getNationality()).thenReturn("Canada");

        TreeSet<Competitor> ranking = new TreeSet<>(Comparator.comparing(Competitor::getNationality));
        ranking.add(competitor1);
        ranking.add(competitor2);
        ranking.add(competitor3);

        when(resultFetcher.getResult(competition)).thenReturn(ranking);
        when(competition.competitors()).thenReturn(Set.of(competitor1, competitor2, competitor3));
        when(registeredCompetitors.containsAll(Set.of(competitor1, competitor2, competitor3))).thenReturn(true);

        putMedals("Argentina", 1, 2, 3);
        putMedals("Bulgaria", 2, 1, 0);
        putMedals("Canada", 3, 0, 1);

        olympics.updateMedalStatistics(competition);

        assertEquals(2, olympics.getNationsMedalTable().get("Argentina").getOrDefault(Medal.GOLD, 0),
            "updateMedalStatistics was expected to to add a Gold medal to the medals count of Argentina");
        assertEquals(2, olympics.getNationsMedalTable().get("Bulgaria").getOrDefault(Medal.SILVER, 0),
            "updateMedalStatistics was expected to to add a Silver medal to the medals count of Bulgaria");
        assertEquals(2, olympics.getNationsMedalTable().get("Canada").getOrDefault(Medal.BRONZE, 0),
            "updateMedalStatistics was expected to to add a Bronze medal to the medals count of Canada");
    }


    private void putMedals(String nationality, int goldMedals, int silverMedals, int bronzeMedals) {
        olympics.getNationsMedalTable().putIfAbsent(nationality, new EnumMap<>(Medal.class));
        olympics.getNationsMedalTable().get(nationality).put(Medal.GOLD, goldMedals);
        olympics.getNationsMedalTable().get(nationality).put(Medal.SILVER, silverMedals);
        olympics.getNationsMedalTable().get(nationality).put(Medal.BRONZE, bronzeMedals);
    }

    @Test
    void testGetNationsRankListCorrectlySortsNationsWithAllDifferentMedalCount() {
        // TOTAL: 13
        putMedals("Bulgaria", 4, 3, 6);

        // TOTAL: 6
        putMedals("Russia", 1, 4, 1);

        // TOTAL: 8
        putMedals("Serbia", 5, 0, 5);

        // TOTAL: 5
        putMedals("Romania", 1, 2, 2);

        List<String> expectedRankList = List.of("Bulgaria", "Serbia", "Russia", "Romania");
        assertIterableEquals(expectedRankList, olympics.getNationsRankList(),
            "getNationsRankList expected to return correctly sorted collection of nations when all nations have different medals count");
    }

    @Test
    void testGetNationsRankListCorrectlySortsNationsWithAllEqualMedalCount() {
        // TOTAL: 13
        putMedals("Bulgaria", 4, 3, 6);

        // TOTAL: 13
        putMedals("Russia", 6, 4, 3);

        // TOTAL: 13
        putMedals("Romania", 4, 3, 6);

        List<String> expectedRankList = List.of("Bulgaria", "Romania", "Russia");
        assertIterableEquals(expectedRankList, olympics.getNationsRankList(),
            "getNationsRankList expected to return correctly (alphabetical) sorted collection of nations when all have equal medals count");
    }

    @Test
    void testGetNationsRankListCorrectlySortsNationsWithSomeEqualMedalsCount() {
        // TOTAL: 13
        putMedals("Bulgaria", 4, 3, 6);

        // TOTAL: 5
        putMedals("Russia", 1, 3, 1);

        // TOTAL: 8
        putMedals("Serbia", 3, 0, 5);

        // TOTAL: 5
        putMedals("Romania", 1, 2, 2);

        List<String> expectedRankList = List.of("Bulgaria", "Serbia", "Romania", "Russia");
        assertIterableEquals(expectedRankList, olympics.getNationsRankList(),
            "getNationsRankList to return correctly sorted collection of nations when some of them have equal medals count");
    }

    @Test
    void testGetTotalMedalsWithNullNationalityThrowsException() {
        assertThrows(IllegalArgumentException.class,  () -> olympics.getTotalMedals(null),
            "getTotalMedals expected to throw IllegalArgumentException when nationality is null");
    }

    @Test
    void testGetTotalMedalsWithNationalityNotInMedalTableThrowsException() {
        assertThrows(IllegalArgumentException.class,  () -> olympics.getTotalMedals("UNKNOWN"),
            "getTotalMedals expected to throw IllegalArgumentException");
    }

    @Test
    void testGetTotalMedalsWithNationalityInMedalTable() {
        putMedals("Bulgaria", 4, 3, 6);

        assertEquals(13, olympics.getTotalMedals("Bulgaria"),
            "getTotalMedals expected to return 13 total medals for Bulgaria");
    }

    @Test
    void testGetTotalMedalsWithNullNationMedals() {
        olympics.getNationsMedalTable().put("Bulgaria", null);

        assertEquals(0,  olympics.getTotalMedals("Bulgaria"),
            "getTotalMedals expected to 0 when the nation's medals are null");
    }

    @Test
    void testGetTotalMedalsWithEmptyNationMedals() {
        olympics.getNationsMedalTable().put("Bulgaria", new EnumMap<>(Medal.class));

        assertEquals(0,  olympics.getTotalMedals("Bulgaria"),
            "getTotalMedals expected to 0 when the nation's medals are empty");
    }

}
