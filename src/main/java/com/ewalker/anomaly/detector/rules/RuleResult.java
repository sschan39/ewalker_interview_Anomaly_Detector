package com.ewalker.anomaly.detector.rules;

public final class RuleResult {
    private final boolean triggered;
    private final String reason;
    private final double confidence;

    public RuleResult(boolean triggered, String reason, double confidence) {
        this.triggered = triggered;
        this.reason = reason;
        this.confidence = confidence;
    }

    public boolean triggered() {
        return triggered;
    }

    public String reason() {
        return reason;
    }

    public double confidence() {
        return confidence;
    }
}
