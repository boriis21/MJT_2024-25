package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocationRuleTest {

    private final List<Transaction> transactions = List.of(
        Transaction.of("TX000033,AC00060,396.45,2023-09-25 16:26:00,New York,ATM"),
        Transaction.of("TX000037,AC00404,78.13,2023-11-21 16:58:44,Milwaukee,Branch"),
        Transaction.of("TX000049,AC00296,626.9,2023-11-27 16:45:57,Milwaukee,Online"),
        Transaction.of("TX000062,AC00002,263.99,2023-05-16 16:07:30,Milwaukee,Branch"),
        Transaction.of("TX000076,AC00239,232.12,2023-12-28 17:31:03,Omaha,ATM"),
        Transaction.of("TX000082,AC00445,345.39,2023-10-23 17:13:57,Milwaukee,Online")
    );

    @Test
    void testLocationsRuleThrowsForNegativeThreshold() {
        assertThrows(IllegalArgumentException.class, () -> new LocationsRule(-3, 0.2),
            "LocationRule expected to throw Illegal Argument exception when negative threshold is passed");
    }

    @Test
    void testLocationRuleThrowsForWightAboveRange() {
        assertThrows(IllegalArgumentException.class, () -> new LocationsRule(3, 1.2),
            "LocationRule expected to throw Illegal Argument exception when wight above 1.0 is passed");
    }

    @Test
    void testLocationRuleThrowsForWightUnderRange() {
        assertThrows(IllegalArgumentException.class, () -> new LocationsRule(3, -0.2),
            "LocationRule expected to throw Illegal Argument exception when wight under 0.0 is passed");
    }

    @Test
    void testApplicableThrowsForNullList() {
        assertThrows(IllegalArgumentException.class, () -> new LocationsRule(2, 0.2).applicable(null),
            "Applicable method of LocationRule expected to throw Illegal Argument exception when null list is passed");
    }

    @Test
    void testApplicableThrowsForEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> new LocationsRule(2, 0.2).applicable(Collections.EMPTY_LIST),
            "Applicable method of LocationRule expected to throw Illegal Argument exception when null list is passed");
    }

    @Test
    void testApplicableReturnsFalseForNotFulfilledCondition() {
        assertFalse(new LocationsRule(4, 0.2).applicable(transactions),
            "Applicable method of LocationRule expected to return false when the condition is not fulfilled");
    }

    @Test
    void testApplicableReturnsTrueForFulfilledCondition() {
        assertTrue(new LocationsRule(3, 0.4).applicable(transactions),
            "Applicable method of LocationRule expected to return true when the condition is fulfilled");
    }

}
