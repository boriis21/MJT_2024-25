package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.util.List;

public class SmallTransactionsRule extends RuleCommon implements Rule {

    private final double amountThreshold;

    public SmallTransactionsRule(int countThreshold, double amountThreshold, double weight) {
        super(countThreshold, weight);

        if (amountThreshold < 0) {
            throw new IllegalArgumentException("Amount threshold cannot be negative");
        }

        this.amountThreshold = amountThreshold;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("Transactions cannot be null or empty");
        }

        long smallTransactionsCount = transactions.stream()
            .filter(transaction -> transaction.transactionAmount() <= amountThreshold)
            .count();

        return smallTransactionsCount >= super.primaryThreshold;
    }

}
