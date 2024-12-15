package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Comparator;
import java.util.List;

public class FrequencyRule extends RuleCommon implements Rule {

    private final TemporalAmount timeWindow;

    public FrequencyRule(int transactionCountThreshold, TemporalAmount timeWindow, double weight) {
        super(transactionCountThreshold, weight);

        if (timeWindow == null) {
            throw new IllegalArgumentException("Time window cannot be null");
        }

        this.timeWindow = timeWindow;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("Transactions cannot be null or empty");
        }

        List<Transaction> sortedTransactions = transactions.stream()
            .sorted(Comparator.comparing(Transaction::transactionDate))
            .toList();

        return sortedTransactions.stream()
            .anyMatch(transaction -> {
                LocalDateTime windowStart = transaction.transactionDate();
                LocalDateTime windowEnd = windowStart.plus(timeWindow);

                long count = sortedTransactions.stream()
                    .filter(tr -> tr.transactionDate().equals(windowStart) || tr.transactionDate().equals(windowEnd) ||
                        (tr.transactionDate().isAfter(windowStart) && tr.transactionDate().isBefore(windowEnd)))
                    .count();

                return count >= super.primaryThreshold;
            });
    }

}
