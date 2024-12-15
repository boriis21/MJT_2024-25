package bg.sofia.uni.fmi.mjt.frauddetector.analyzer;

import bg.sofia.uni.fmi.mjt.frauddetector.rule.Rule;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TransactionAnalyzerImpl implements TransactionAnalyzer {

    private static final double DELTA = 1e-9;
    private final List<Transaction> transactions;
    private final List<Rule> rules;

    public TransactionAnalyzerImpl(Reader reader, List<Rule> rules) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null");
        }

        if (rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("Rules cannot be null or empty");
        }

        double rulesWeightSum = rules.stream()
            .mapToDouble(Rule::weight)
            .sum();

        if (rulesWeightSum < -DELTA || rulesWeightSum > 1.0 + DELTA) {
            throw new IllegalArgumentException("Invalid weight sum");
        }

        var transactionReader = new BufferedReader(reader);
        this.transactions = transactionReader.lines()
            .skip(1)
            .map(Transaction::of)
            .toList();
        this.rules = List.copyOf(rules);
    }

    @Override
    public List<Transaction> allTransactions() {
        return transactions;
    }

    @Override
    public List<String> allAccountIDs() {
        return transactions.stream()
            .map(Transaction::accountID)
            .distinct()
            .toList();
    }

    @Override
    public Map<Channel, Integer> transactionCountByChannel() {
        return transactions.stream()
            .collect(Collectors.groupingBy(Transaction::channel,
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
    }

    @Override
    public double amountSpentByUser(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Account ID cannot be null or blank");
        }

        if (isUserNonExisting(accountId)) {
            throw new IllegalArgumentException("No matching account ID found");
        }

        return transactions.stream()
            .filter(transaction -> transaction.accountID().equals(accountId))
            .mapToDouble(Transaction::transactionAmount)
            .sum();
    }

    @Override
    public List<Transaction> allTransactionsByUser(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Account ID cannot be null or blank");
        }

        if (isUserNonExisting(accountId)) {
            throw new IllegalArgumentException("No matching account ID found");
        }

        return transactions.stream()
            .filter(transaction -> transaction.accountID().equals(accountId))
            .toList();
    }

    @Override
    public double accountRating(String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("Account ID cannot be null or blank");
        }

        if (isUserNonExisting(accountId)) {
            throw new IllegalArgumentException("No matching account ID found");
        }

        List<Transaction> accountTransactions = transactions.stream()
            .filter(transaction -> transaction.accountID().equals(accountId))
            .toList();

        return rules.stream()
            .filter(rule -> rule.applicable(accountTransactions))
            .mapToDouble(Rule::weight)
            .sum();
    }

    @Override
    public SortedMap<String, Double> accountsRisk() {
        Map<String, Double> accountRisks = transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::accountID,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    transactionList -> accountRating(transactionList.getFirst().accountID())
                )
            ));

        TreeMap<String, Double> sortedAccountRisks = new TreeMap<>(
            (key1, key2) -> {
                int valueComparison = accountRisks.get(key2).compareTo(accountRisks.get(key1));
                return valueComparison != 0 ? valueComparison : key1.compareTo(key2);
            }
        );

        sortedAccountRisks.putAll(accountRisks);

        return sortedAccountRisks;
    }

    private Boolean isUserNonExisting(String accountID) {
        return transactions.stream()
            .noneMatch(transaction -> transaction.accountID().equals(accountID));
    }

}
