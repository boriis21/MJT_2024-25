package bg.sofia.uni.fmi.mjt.frauddetector.transaction;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionTest {

    @Test
    void testOfThrowsForNullLine() {
        assertThrows(IllegalArgumentException.class, () -> Transaction.of(null),
            "of expected to throw Illegal Argument exception when null argument is passed");
    }

    @Test
    void testOfThrowsForBlankLine() {
        assertThrows(IllegalArgumentException.class, () -> Transaction.of("      "),
            "of expected to throw Illegal Argument exception when blank argument is passed");
    }

    @Test
    void testOfThrowsForBlankTransactionId() {
        assertThrows(IllegalArgumentException.class, () -> Transaction.of("   ,AC00385,815.96,2023-03-31 16:06:57,Nashville,ATM"),
            "of expected to throw Illegal Argument exception when blank transactionId is passed");
    }

    @Test
    void testOfThrowsForBlankAccountId() {
        assertThrows(IllegalArgumentException.class, () -> Transaction.of("TX000010,  ,815.96,2023-03-31 16:06:57,Nashville,ATM"),
            "of expected to throw Illegal Argument exception when blank account Id is passed");
    }

    @Test
    void testOfThrowsForNegativeTransactionAmount() {
        assertThrows(IllegalArgumentException.class, () -> Transaction.of("TX000010,AC00385,-815.96,2023-03-31 16:06:57,Nashville,ATM"),
            "of expected to throw Illegal Argument exception when negative amount of money is passed");
    }

    @Test
    void testOfThrowsForInvalidChannel() {
        assertThrows(IllegalArgumentException.class, () -> Transaction.of("TX000031,AC00367,28.31,2023-07-17 17:49:22,Miami,Website"),
            "of expected to throw Illegal Argument exception when invalid channel is passed");
    }

    @Test
    void testOfThrowsForNotEnoughInfo() {
        assertThrows(IllegalArgumentException.class, () -> Transaction.of("TX000010,AC00385,815.96,2023-03-31 16:06:57"),
            "of expected to throw Illegal Argument exception when not enough info is given");
    }

    @Test
    void testOfReturnsCorrectObject() {
        Transaction testTransaction = Transaction.of("TX000010,AC00385,815.96,2023-03-31 16:06:57,Nashville,ATM");
        LocalDateTime expectedDate = LocalDateTime.of(2023, 3, 31, 16, 6, 57);

        assertEquals("TX000010", testTransaction.transactionID(),
            "of expected to get the correct transaction id");
        assertEquals("AC00385", testTransaction.accountID(),
            "of expected to get the correct account id");
        assertEquals(815.96, testTransaction.transactionAmount(),
            "of expected to get the correct transaction amount");
        assertEquals(expectedDate, testTransaction.transactionDate(),
            "of expected to get the correct transaction date");
        assertEquals("Nashville", testTransaction.location(),
            "of expected to get the correct transaction location");
        assertEquals(Channel.ATM, testTransaction.channel(),
            "of expected to get the correct transaction channel");
    }

}
