package com.ewalker.anomaly.detector.rules;

public final class EntropyRule implements DetectionRule {
    private final double threshold;

    public EntropyRule(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public RuleResult evaluate(String input) {
        double entropy = calculateEntropy(input);
        if (entropy < threshold) {
            return new RuleResult(true, "Low entropy: " + String.format("%.2f", entropy), 0.8);
        }
        return new RuleResult(false, "", 0.0);
    }

    private double calculateEntropy(String value) {
        int[] counts = new int[Character.MAX_VALUE + 1];
        int length = value.length();

        for (int index = 0; index < length; index++) {
            counts[value.charAt(index)]++;
        }

        double entropy = 0.0;
        for (int count : counts) {
            if (count == 0) {
                continue;
            }

            double probability = (double) count / length;
            entropy -= probability * (Math.log(probability) / Math.log(2));
        }

        return entropy;
    }
}
