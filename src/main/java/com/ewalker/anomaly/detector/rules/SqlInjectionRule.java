package com.ewalker.anomaly.detector.rules;

public final class SqlInjectionRule implements DetectionRule {
    private static final String[] SQL_PATTERNS = {
            "'", "\"", "OR 1=1", "1=1", "1 = 1", "';", "--", "/*", "*/",
            "xp_", "sp_", "DROP", "DELETE", "INSERT", "UPDATE", "UNION",
            "EXEC", "EXECUTE", "SCRIPT", "JAVASCRIPT"
    };

    @Override
    public RuleResult evaluate(String input) {
        String upper = input.toUpperCase();
        for (String pattern : SQL_PATTERNS) {
            if (upper.contains(pattern.toUpperCase())) {
                return new RuleResult(true, "SQL injection pattern: " + pattern, 0.95);
            }
        }
        return new RuleResult(false, "", 0.0);
    }
}
