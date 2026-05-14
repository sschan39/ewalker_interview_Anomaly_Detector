package com.ewalker.anomaly.detector;

import com.ewalker.anomaly.detector.rules.*;

public class AnomalyDetector {
    private final CompositeDetectionRule rules;
    private final double entropyThreshold;

    public AnomalyDetector() {
        this(3.0);
    }

    public AnomalyDetector(double entropyThreshold) {
        this.entropyThreshold = entropyThreshold;
        this.rules = new CompositeDetectionRule();
        initializeDefaultRules();
    }

    private void initializeDefaultRules() {
        rules.addRule(new SqlInjectionRule());
        rules.addRule(new AdminKeywordRule());
        rules.addRule(new RepeatedPatternRule());
        rules.addRule(new EntropyRule(entropyThreshold));
    }

    public void addRule(DetectionRule rule) {
        rules.addRule(rule);
    }

    public DetectionResult analyze(String message) {
        String normalized = message == null ? "" : message.trim();
        if (normalized.isEmpty()) {
            return new DetectionResult(true, "empty message", 0.0);
        }

        RuleResult ruleResult = rules.evaluate(normalized);
        double entropy = calculateEntropy(normalized);

        if (ruleResult.triggered()) {
            return new DetectionResult(true, ruleResult.reason(), entropy);
        }

        return new DetectionResult(false, "normal", entropy);
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