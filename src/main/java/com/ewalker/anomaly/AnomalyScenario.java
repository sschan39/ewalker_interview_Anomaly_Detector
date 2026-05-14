package com.ewalker.anomaly;

public final class AnomalyScenario {
    private final String input;
    private final boolean expectedAnomaly;

    public AnomalyScenario(String input, boolean expectedAnomaly) {
        this.input = input;
        this.expectedAnomaly = expectedAnomaly;
    }

    public String input() {
        return input;
    }

    public boolean expectedAnomaly() {
        return expectedAnomaly;
    }
}