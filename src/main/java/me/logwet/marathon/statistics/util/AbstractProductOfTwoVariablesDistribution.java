package me.logwet.marathon.statistics.util;

public abstract class AbstractProductOfTwoVariablesDistribution
        extends AbstractEnumeratedIntegerDistribution {
    protected final AbstractEnumeratedIntegerDistribution d1;
    protected final AbstractEnumeratedIntegerDistribution d2;

    public AbstractProductOfTwoVariablesDistribution(
            AbstractEnumeratedIntegerDistribution d1, AbstractEnumeratedIntegerDistribution d2) {
        super(buildN(d1, d2), buildProbabilities(d1, d2));

        this.d1 = d1;
        this.d2 = d2;
    }

    protected static int buildN(
            AbstractEnumeratedIntegerDistribution d1, AbstractEnumeratedIntegerDistribution d2) {
        return d1.getNumTrials() * d2.getNumTrials();
    }

    protected static double[] buildProbabilities(
            AbstractEnumeratedIntegerDistribution d1, AbstractEnumeratedIntegerDistribution d2) {
        int n = buildN(d1, d2);
        int n1 = d1.getNumTrials();
        int n2 = d2.getNumTrials();

        double[] probabilities = new double[n + 1];

        for (int k = 0; k <= n; k++) {
            for (int t = 0; t <= Math.min(n1, n2); t++) {
                try {
                    if (k % t == 0) {
                        if (n1 <= n2) {
                            probabilities[k] += d1.getProbability(t) * d2.getProbability(k / t);
                        } else {
                            probabilities[k] += d1.getProbability(k / t) * d2.getProbability(t);
                        }
                    }
                } catch (ArithmeticException ignored) {
                    for (int t2 = 0; t2 <= Math.max(n1, n2); t2++) {
                        if (n1 <= n2) {
                            probabilities[k] += d1.getProbability(0) * d2.getProbability(t2);
                        } else {
                            probabilities[k] += d1.getProbability(t2) * d2.getProbability(0);
                        }
                    }
                }
            }
        }

        return probabilities;
    }
}