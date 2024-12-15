package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class FrequencyRuleTest {

    private final List<Transaction> transactions = List.of(
        Transaction.of("TX000033,AC00060,396.45,2023-11-22 16:35:00,New York,ATM"),
        Transaction.of("TX000062,AC00002,263.99,2023-05-16 16:07:30,Milwaukee,Branch"),
        Transaction.of("TX000049,AC00296,626.9,2023-11-22 17:25:57,Milwaukee,Online"),
        Transaction.of("TX000076,AC00239,232.12,2023-09-24 17:31:03,Omaha,ATM"),
        Transaction.of("TX000037,AC00404,78.13,2023-11-22 16:58:44,Milwaukee,Branch"),
        Transaction.of("TX000082,AC00445,345.39,2023-10-23 17:13:57,Milwaukee,Online")
    );

    @Test
    void testFrequencyRuleThrowsForNegativeThreshold() {
        TemporalAmount testWindow = mock();
        assertThrows(IllegalArgumentException.class, () -> new FrequencyRule(-3, testWindow, 0.2),
            "FrequencyRule expected to throw Illegal Argument exception when negative threshold is passed");
    }

    @Test
    void testFrequencyRuleThrowsForNullTimeWindow() {
        assertThrows(IllegalArgumentException.class, () -> new FrequencyRule(3, null, 0.2),
            "FrequencyRule expected to throw Illegal Argument exception when null time window is passed");
    }

    @Test
    void testLFrequencyRuleThrowsForWightAboveRange() {
        TemporalAmount testWindow = mock();
        assertThrows(IllegalArgumentException.class, () -> new FrequencyRule(3, testWindow, 1.2),
            "FrequencyRule expected to throw Illegal Argument exception when wight above 1.0 is passed");
    }

    @Test
    void testFrequencyRuleThrowsForWightUnderRange() {
        TemporalAmount testWindow = mock();
        assertThrows(IllegalArgumentException.class, () -> new FrequencyRule(3, testWindow, -0.2),
            "FrequencyRule expected to throw Illegal Argument exception when wight under 0.0 is passed");
    }

    @Test
    void testApplicableThrowsForNullList() {
        TemporalAmount testWindow = mock();
        assertThrows(IllegalArgumentException.class, () -> new FrequencyRule(2, testWindow, 0.2).applicable(null),
            "Applicable method of FrequencyRule expected to throw Illegal Argument exception when null list is passed");
    }

    @Test
    void testApplicableThrowsForEmptyList() {
        TemporalAmount testWindow = mock();
        assertThrows(IllegalArgumentException.class, () -> new FrequencyRule(2, testWindow, 0.2).applicable(Collections.EMPTY_LIST),
            "Applicable method of FrequencyRule expected to throw Illegal Argument exception when null list is passed");
    }

    @Test
    void testApplicableReturnsFalseForNotFulfilledCondition() {
        assertFalse(new FrequencyRule(4, Duration.ofDays(2), 0.2).applicable(transactions),
            "Applicable method of FrequencyRule expected to return false when the condition is not fulfilled");
    }

    @Test
    void testApplicableReturnsTrueForFulfilledCondition() {
       assertTrue(new FrequencyRule(2, Duration.ofHours(2), 0.4).applicable(transactions),
            "Applicable method of FrequencyRule expected to return true when the condition is fulfilled");
    }
}
