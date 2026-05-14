package com.ewalker.anomaly.detector.rules;

public interface DetectionRule {
    RuleResult evaluate(String input);
}
