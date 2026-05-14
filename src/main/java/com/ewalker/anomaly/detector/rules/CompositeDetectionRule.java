package com.ewalker.anomaly.detector.rules;

import java.util.ArrayList;
import java.util.List;

public final class CompositeDetectionRule implements DetectionRule {
    private final List<DetectionRule> rules;

    public CompositeDetectionRule() {
        this.rules = new ArrayList<>();
    }

    public void addRule(DetectionRule rule) {
        rules.add(rule);
    }

    @Override
    public RuleResult evaluate(String input) {
        for (DetectionRule rule : rules) {
            RuleResult result = rule.evaluate(input);
            if (result.triggered()) {
                return result;
            }
        }
        return new RuleResult(false, "normal", 0.0);
    }
}
