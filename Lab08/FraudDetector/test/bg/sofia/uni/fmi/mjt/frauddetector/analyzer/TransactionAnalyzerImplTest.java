package bg.sofia.uni.fmi.mjt.frauddetector.analyzer;

import bg.sofia.uni.fmi.mjt.frauddetector.rule.FrequencyRule;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.LocationsRule;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.Rule;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.SmallTransactionsRule;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.ZScoreRule;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.time.Period;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionAnalyzerImplTest {

    private static TransactionAnalyzer analyzer;

    @BeforeAll
    public static void fillList() {
        String transactionsData = """
            
            TX000092,AC00310,223.85,2023-10-02 16:36:10,Kansas City,ATM
            TX000093,AC00149,230.66,2023-03-27 18:16:04,Houston,Branch
            TX000094,AC00011,5.64,2023-07-17 17:08:24,Louisville,ATM
            TX000095,AC00178,442.12,2023-12-21 16:37:03,Los Angeles,Online
            TX000096,AC00425,644.17,2023-03-18 16:05:09,San Antonio,ATM
            TX000097,AC00178,508.68,2023-06-26 16:16:50,Mesa,Branch
            TX000098,AC00310,858.63,2023-04-18 17:27:23,San Francisco,Branch
            TX000099,AC00011,2.13,2023-07-18 17:08:24,Montana,ATM
            TX000100,AC00310,147.62,2023-09-01 17:26:02,Charlotte,Branch
            TX000101,AC00178,115.02,2023-09-11 16:19:04,San Antonio,Branch
            TX000102,AC00310,383.37,2023-01-24 17:23:56,Fort Worth,Branch
            TX000103,AC00425,220.41,2023-03-02 16:05:09,San Francisco,Online
            TX000104,AC00011,643.73,2023-07-27 17:08:24,Boston,ATM
            TX000105,AC00425,102.17,2023-03-21 16:05:09,Chicago,Branch
            TX000106,AC00011,153.54,2023-08-01 17:08:24,Louisville,ATM
            TX000107,AC00425,220.41,2023-03-12 16:05:09,Miami,ATM
            TX000108,AC00011,824.1,2023-08-02 17:08:24,Louisville,ATM
            TX000109,AC00425,102.17,2023-03-01 16:05:09,Austin,Branch
            TX000110,AC00011,276.65,2023-09-12 17:08:24,Louisville,ATM
            """;

        StringReader reader = new StringReader(transactionsData);
        List<Rule> rules = List.of(
            new ZScoreRule(1.5, 0.3),
            new LocationsRule(2, 0.4),
            new FrequencyRule(4, Period.ofWeeks(4), 0.25),
            new SmallTransactionsRule(1, 10.20, 0.05)
        );

        analyzer = new TransactionAnalyzerImpl(reader, rules);
    }

    @Test
    void testTransactionAnalyzerImplThrowsForNullReader() {
        List<Rule> testList = mock();
        when(testList.isEmpty()).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> new TransactionAnalyzerImpl(null, testList),
            "TransactionAnalyzerImpl expected to throw Illegal argument exception when null reader passed");
    }

    @Test
    void testTransactionAnalyzerImplThrowsForNullRules() {
        Reader testReader = mock();
        assertThrows(IllegalArgumentException.class, () -> new TransactionAnalyzerImpl(testReader, null),
            "TransactionAnalyzerImpl expected to throw Illegal argument exception when null rules passed");
    }

    @Test
    void testTransactionAnalyzerImplThrowsForEmptyRules() {
        Reader testReader = mock();
        List<Rule> testList = mock();
        when(testList.isEmpty()).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> new TransactionAnalyzerImpl(testReader, testList),
            "TransactionAnalyzerImpl expected to throw Illegal argument exception when empty list of rules passed");
    }

    @Test
    void testTransactionAnalyzerImplThrowsForInvalidWeightSum() {
        Reader testReader = mock();
        List<Rule> testList = List.of(
            new ZScoreRule(1.5, 0.7),
            new LocationsRule(2, 0.4));

        assertThrows(IllegalArgumentException.class, () -> new TransactionAnalyzerImpl(testReader, testList),
            "TransactionAnalyzerImpl expected to throw Illegal argument exception when the sum of the rules' weight is bigger than 1.0");
    }

    @Test
    void testAllAccountsIDsReturnsCorrectList() {
        List<String> accountIDs = List.of("AC00310", "AC00149", "AC00011", "AC00178", "AC00425");

        assertEquals(accountIDs, analyzer.allAccountIDs(),
            "AllAccountsIDs expected to return correct list of account IDs");
    }

    @Test
    void testTransactionCountByChannelReturnsCorrectMap() {
        // 9x ATM, 8x Branch, 2x Online
        Map<Channel, Integer> countsByChannel = Map.of(Channel.ATM, 9, Channel.BRANCH, 8, Channel.ONLINE, 2);

        assertEquals(countsByChannel, analyzer.transactionCountByChannel(),
            "TransactionCountByChannel expected to return correct map");
    }

    @Test
    void testAmountSpentByUserThrowsForNullAccountID() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.amountSpentByUser(null),
            "AmountSpentByUser expected to throw Illegal argument exception when null accountID is passed");
    }

    @Test
    void testAmountSpentByUserThrowsForBlankAccountID() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.amountSpentByUser("         "),
            "AmountSpentByUser expected to throw Illegal argument exception when blank accountID is passed");
    }

    @Test
    void testAmountSpentByUserThrowsForNonExistingAccount() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.amountSpentByUser("ASD"),
            "AmountSpentByUser expected to throw Illegal argument exception when non existing accountID is passed");
    }

    @Test
    void testAmountSpentByUserReturnsCorrectValue() {
        assertEquals(1613.47, analyzer.amountSpentByUser("AC00310"),
            "AmountSpentByUser expected to return correct value");
    }

    @Test
    void testAllTransactionsByUserThrowsForNullAccountID() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.allTransactionsByUser(null),
            "AllTransactionsByUser expected to throw Illegal argument exception when null accountID is passed");
    }

    @Test
    void testAllTransactionsByUserThrowsForBlankAccountID() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.allTransactionsByUser("        "),
            "AllTransactionsByUser expected to throw Illegal argument exception when blank accountID is passed");
    }

    @Test
    void testAllTransactionsByUserThrowsForNonExistingAccount() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.allTransactionsByUser("AGFD"),
            "AllTransactionsByUser expected to throw Illegal argument exception when non existing accountID is passed");
    }

    @Test
    void testAllTransactionsByUserReturnsCorrectList() {
        List<Transaction> transactions = List.of(
            Transaction.of("TX000095,AC00178,442.12,2023-12-21 16:37:03,Los Angeles,Online"),
            Transaction.of("TX000097,AC00178,508.68,2023-06-26 16:16:50,Mesa,Branch"),
            Transaction.of("TX000101,AC00178,115.02,2023-09-11 16:19:04,San Antonio,Branch")
        );

        assertEquals(transactions, analyzer.allTransactionsByUser("AC00178"),
            "AllTransactionsByUser expected to return correct list of transactions");
    }

    @Test
    void testAccountRatingThrowsForNullAccountID() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.accountRating(null),
            "AccountRating expected to throw Illegal argument exception when null accountID is passed");
    }

    @Test
    void testAccountRatingThrowsForBlankAccountID() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.accountRating("        "),
            "AccountRating expected to throw Illegal argument exception when blank accountID is passed");
    }

    @Test
    void testAccountRatingThrowsForNonExistingAccount() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.accountRating("AFSDF"),
            "AccountRating expected to throw Illegal argument exception when non existing accountID is passed");
    }

    @Test
    void testAccountRatingReturnsCorrectRatingNoRulesApplicable() {
        assertEquals(0.0, analyzer.accountRating("AC00149"),
            "AccountRating expected to return 0.0 when no rules are applicable");
    }

    @Test
    void testAccountRatingReturnsCorrectRatingOnlyOneRuleApplicable() {
        assertEquals(0.4, analyzer.accountRating("AC00178"),
            "AccountRating expected to return correct rating when only one rule is applicable");
    }

    @Test
    void testAccountRatingReturnsCorrectRatingTwoRulesApplicable() {
        assertEquals(0.7, analyzer.accountRating("AC00310"),
            "AccountRating expected to return correct rating when two rules are applicable");
    }

    @Test
    void testAccountRatingReturnsCorrectRatingThreeRulesApplicable() {
        assertEquals(0.95, analyzer.accountRating("AC00425"),
            "AccountRating expected to return correct rating when three rules are applicable");
    }

    @Test
    void testAccountRatingReturnsCorrectRatingAllRulesApplicable() {
        assertEquals(1.0, analyzer.accountRating("AC00011"),
            "AccountRating expected to return 1.0 when all rules are applicable");
    }

    @Test
    void testAccountsRiskReturnsCorrectMap() {
        Map<String, Double> accountsRisk =
            Map.of("AC00011", 1.0, "AC00425", 0.95, "AC00310", 0.7, "AC00178", 0.4, "AC00149", 0.0);

        assertEquals(accountsRisk, analyzer.accountsRisk(),
            "AccountsRisk expected to return correct values");
    }

}
