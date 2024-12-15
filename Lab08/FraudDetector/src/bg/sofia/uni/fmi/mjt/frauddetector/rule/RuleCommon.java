package bg.sofia.uni.fmi.mjt.frauddetector.rule;

public abstract class RuleCommon implements Rule {

    private static final double DELTA = 1e-9;
    protected final int primaryThreshold;
    protected final double weight;

    public RuleCommon(int primaryThreshold, double weight) {
        if (primaryThreshold < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }

        if (weight < -DELTA || weight > 1.0 + DELTA) {
            throw new IllegalArgumentException("Weight should be between 0.0 and 1.0");
        }

        this.primaryThreshold = primaryThreshold;
        this.weight = weight;
    }

    @Override
    public double weight() {
        return weight;
    }

}
