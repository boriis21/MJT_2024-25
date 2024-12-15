package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.util.List;

public class ZScoreRule extends RuleCommon implements Rule {

    private final double zScoreThreshold;

    public ZScoreRule(double zScoreThreshold, double weight) {
        super(0, weight);

        if (zScoreThreshold < 0) {
            throw new IllegalArgumentException("Z score threshold cannot be null");
        }

        this.zScoreThreshold = zScoreThreshold;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("Transactions cannot be null or empty");
        }

        double zScore = transactions.stream()
            .mapToDouble(transaction -> calculateZScore(transactions, transaction))
            .max()
            .orElse(0.0);

        return zScore >= zScoreThreshold;
    }

    private double calculateVariance(List<Transaction> transactions) {
        double average = calculateAverageValue(transactions);

        return transactions.stream()
            .mapToDouble(transaction -> Math.pow(transaction.transactionAmount() - average, 2))
            .average()
            .orElse(0.0);
    }

    private double calculateAverageValue(List<Transaction> transactions) {
        return transactions.stream()
            .mapToDouble(Transaction::transactionAmount)
            .average()
            .orElse(0.0);
    }

    private double calculateZScore(List<Transaction> transactions, Transaction currentTransaction) {
        double variance = calculateVariance(transactions);
        double average = calculateAverageValue(transactions);

        return (currentTransaction.transactionAmount() - average) / Math.sqrt(variance);
    }

}
