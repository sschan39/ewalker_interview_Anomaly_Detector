package com.ewalker.anomaly.detector.rules;

public final class RepeatedPatternRule implements DetectionRule {
    @Override
    public RuleResult evaluate(String input) {
        String stripped = input.replaceAll("\\s+", "");
        int length = stripped.length();

        if (length < 4) {
            return new RuleResult(false, "", 0.0);
        }

        for (int patternLength = 1; patternLength <= length / 2; patternLength++) {
            if (length % patternLength != 0) {
                continue;
            }

            String pattern = stripped.substring(0, patternLength);
            if (pattern.repeat(length / patternLength).equals(stripped)) {
                return new RuleResult(true, "Repeated pattern detected", 0.85);
            }
        }

        return new RuleResult(false, "", 0.0);
    }
}
