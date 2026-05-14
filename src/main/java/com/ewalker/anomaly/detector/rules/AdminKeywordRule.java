package com.ewalker.anomaly.detector.rules;

public final class AdminKeywordRule implements DetectionRule {
    private static final String[] KEYWORDS = {
            "admin", "root", "administrator", "superuser", "sudo",
            "passwd", "password", "pwd", "access", "auth", "secret"
    };

    @Override
    public RuleResult evaluate(String input) {
        String lower = input.toLowerCase();
        for (String keyword : KEYWORDS) {
            if (lower.contains(keyword)) {
                return new RuleResult(true, "Suspicious keyword: " + keyword, 0.75);
            }
        }
        return new RuleResult(false, "", 0.0);
    }
}
