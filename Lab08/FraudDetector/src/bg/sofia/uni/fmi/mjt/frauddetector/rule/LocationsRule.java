package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.util.List;

public class LocationsRule extends RuleCommon implements Rule {

    public LocationsRule(int threshold, double weight) {
        super(threshold, weight);
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("Transactions cannot be null or empty");
        }

        long differentLocationsCount = transactions.stream()
            .map(Transaction::location)
            .distinct()
            .count();

        return differentLocationsCount >= super.primaryThreshold;
    }

}
